package com.u8.server.web.pay;

import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.*;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.service.*;
import com.u8.server.utils.*;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/***
 * 请求获取订单号
 */
@Controller
@Namespace("/pay")
public class PayAction extends UActionSupport {

    private int userID = 0;     //当前用户ID  对于单机游戏用户ID可以不传，默认0
    private String productID;   //当前商品ID
    private String productName;
    private String productDesc;
    private int money;          //单位 分
    private String roleID;      //玩家在游戏服中的角色ID
    private String roleName;    //玩家在游戏服中的角色名称
    private String roleLevel;   //角色等级
    private String serverID;    //玩家所在的服务器ID
    private String serverName;  //玩家所在的服务器名称
    private String extension;
    private String notifyUrl;   //支付回调通知的游戏服地址

    private Integer appID;               //appID
    private Integer channelID;          //渠道号
    private Integer subChannelID;       //子渠道号

    private String signType;    //签名算法， RSA|MD5
    private String sign;        //RSA签名

    private String sdkVersionCode;      //脚本类版本号

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private USubChannelManager subChannelManager;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UChannelProductManager channelProductManager;


    @Action("getOrderID")
    public void getOrderID() {

        Log.i("----------------------------------start getOrderID----------------------------------");
        Log.i("------------------------------------userID :  " + userID);
        Log.i("------------------------------------productID :  " + productID);
        Log.i("------------------------------------productName ： " + productName);
        Log.i("------------------------------------productDesc ： " + productDesc);
        Log.i("------------------------------------money ： " + money);
        Log.i("------------------------------------roleID ： " + roleID);
        Log.i("------------------------------------roleName ： " + roleName);
        Log.i("------------------------------------serverID :  " + serverID);
        Log.i("------------------------------------serverName ： " + serverName);
        Log.i("------------------------------------extension ： " + extension);
        Log.i("------------------------------------notifyUrl ： " + notifyUrl);
        Log.i("------------------------------------signType ： " + signType);
        Log.i("------------------------------------sign ： " + sign);

        try {
            UUser user = null;
            UGame game = null;
            UChannel channel = null;
            int currSubChannelID = 0;
            if (this.userID > 0) {
                //网游或者需要登录功能的单机，传userID; 校验userID信息是否正确
                user = userManager.getUser(this.userID);

                if (user == null) {
                    Log.e("the user is not found. userID:" + this.userID);
                    renderState(StateCode.CODE_USER_NONE, null);
                    return;
                }

                game = user.getGame();
                channel = user.getChannel();
                currSubChannelID = user.getSubChannelID();
            } else {
                //纯粹单机，不带登录，无userID
                game = gameManager.getGame(appID);
                if (game == null) {
                    Log.e("游戏不存在 appID:" + this.appID);
                    renderState(StateCode.CODE_GAME_NONE, null);
                    return;
                }

                channel = channelManager.getChannel(this.channelID);
                if (channel == null) {
                    Log.e("渠道不存在 channelID:" + this.channelID);
                    renderState(StateCode.CODE_CHANNEL_NONE, null);
                    return;
                }

                if (channel.getAppID() != appID) {
                    Log.e("渠道跟appID不匹配");
                    renderState(StateCode.CODE_CHANNEL_NOT_MATCH, null);
                    return;
                }
                currSubChannelID = this.subChannelID;
            }

            if (money < 0) {
                Log.e("金额错误 money:" + money);
                renderState(StateCode.CODE_MONEY_ERROR, null);
                return;
            }

            if (!isSignOK(game)) {
                Log.e("签名验证失败 sign:" + this.sign);
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            if (channel != null && !channel.isPayOpen()) {
                Log.e("充值未开放");
                renderState(StateCode.CODE_PAY_CLOSED, null);
                return;
            }

            if (currSubChannelID > 0) {
                USubChannel subChannel = subChannelManager.getSubChannel(channel.getChannelID(), currSubChannelID);
                if (subChannel != null && !subChannel.isPayOpen()) {
                    Log.e("充值未开放");
                    renderState(StateCode.CODE_PAY_CLOSED, null);
                    return;
                }
            }


            long orderID = IDGenerator.getInstance().nextOrderID();
            if (orderID <= 0) {
                Log.e("生成订单号失败");
                renderState(StateCode.CODE_ORDER_ID_FAILED, null);
                return;
            }

            final UOrder order = orderManager.generateOrder(userID, user == null ? "" : user.getName(), game.getAppID(), channel.getChannelID(), currSubChannelID, orderID, money, productID, productName, productDesc, roleID, roleName, roleLevel, serverID, serverName, extension, notifyUrl);

            if (order != null) {

                order.setIp(HttpUtils.getIpAddr(request));

                ISDKScript script = SDKCacheManager.getInstance().getSDKScript(order.getChannel(), sdkVersionCode);

                if (script == null) {
                    Log.e("the ISDKScript is not found. channelID:" + order.getChannelID());
                    renderState(StateCode.CODE_ORDER_ERROR, null);
                    return;
                }

                Log.d("generate order execute script. " + script.getClass().getName());
                final UChannel currChannel = channel;

                script.onGetOrderID(user, order, new ISDKOrderListener() {
                    @Override
                    public void onSuccess(String jsonStr) {
                        JSONObject data = new JSONObject();
                        data.put("orderID", order.getOrderID());
                        data.put("extension", jsonStr);
                        data.put("productID", channelProductManager.getChannelProductID(currChannel.getChannelID(), productID));     //如果有渠道商品映射关系配置， 这里返回的productID就是渠道商品ID
                        Log.d("The onGetOrderID extension is " + jsonStr);
                        renderState(StateCode.CODE_SUCCESS, data);
                    }

                    @Override
                    public void onFailed(String err) {
                        Log.e("script execute failed. " + err);
                        JSONObject data = new JSONObject();
                        data.put("orderID", order.getOrderID());
                        data.put("extension", "");
                        data.put("productID", channelProductManager.getChannelProductID(currChannel.getChannelID(), productID)); //如果有渠道商品映射关系配置， 这里返回的productID就是渠道商品ID
                        renderState(StateCode.CODE_ORDER_ERROR, data);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderState(StateCode.CODE_ORDER_ERROR, null);
            Log.e(e.getMessage());
        }
    }

    private boolean isSignOK(UGame game) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder(150);
        sb.append("userID=").append(this.userID).append("&")
                .append("productID=").append(this.productID == null ? "" : this.productID).append("&")
                .append("productName=").append(this.productName == null ? "" : this.productName).append("&")
                .append("productDesc=").append(this.productDesc == null ? "" : this.productDesc).append("&")
                .append("money=").append(this.money).append("&")
                .append("roleID=").append(this.roleID == null ? "" : this.roleID).append("&")
                .append("roleName=").append(this.roleName == null ? "" : this.roleName).append("&")
                .append("roleLevel=").append(this.roleLevel == null ? "" : this.roleLevel).append("&")
                .append("serverID=").append(this.serverID == null ? "" : this.serverID).append("&")
                .append("serverName=").append(this.serverName == null ? "" : this.serverName).append("&")
                .append("extension=").append(this.extension == null ? "" : this.extension);

        if (!StringUtils.isEmpty(notifyUrl)) {
            sb.append("&notifyUrl=").append(this.notifyUrl);
        }

        if (appID != null && appID > 0) {
            sb.append("&appID=").append(this.appID);
        }

        if (channelID != null && channelID > 0) {
            sb.append("&channelID=").append(this.channelID);
        }

        if (subChannelID != null && subChannelID > 0) {
            sb.append("&subChannelID=").append(this.subChannelID);
        }

        if ("rsa".equalsIgnoreCase(this.signType)) {
            try {
                String encoded = URLEncoder.encode(sb.toString(), "UTF-8");
                Log.d("The encoded getOrderID sign is " + encoded);
                Log.d("The getOrderID sign is " + sign);
                return RSAUtils.verify(encoded, sign, game.getAppRSAPubKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //md5 sign
        sb.append(game.getAppkey());
        Log.d("appkey :%s", game.getAppkey());

        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");

        Log.d("获取订单号的签名  字符串: " + encoded);
        Log.d("获取订单号的签名  sign: " + sign);
        String newSign = EncryptUtils.md5(encoded);
        Log.d("获取订单号的签名  newSign: " + newSign);
        return newSign.toLowerCase().equals(this.sign);
    }


    private void renderState(int state, JSONObject data) {
        JSONObject json = new JSONObject();
        json.put("state", state);
        json.put("data", data);
        super.renderJson(json.toString());
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setSdkVersionCode(String sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public Integer getSubChannelID() {
        return subChannelID;
    }

    public void setSubChannelID(Integer subChannelID) {
        this.subChannelID = subChannelID;
    }
}
