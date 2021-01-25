package com.u8.server.web.user;

import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.common.UAnalyticsRedisTemplate;
import com.u8.server.common.UStringBuilder;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UUser;
import com.u8.server.log.BLog;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.service.*;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.utils.TimeUtils;
import com.u8.server.utils.UGenerator;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/***
 * 用户登录
 */
@Controller
@Namespace("/user")
public class UserAction extends UActionSupport {

    private int appID;                  //游戏 AppID
    private int channelID;              //渠道号
    private Integer subChannelID;       //子渠道号， 没有默认填0

    private String extension;
    private String deviceID;

    private String sdkVersionCode;      //脚本类版本号

    private int userID;
    private String token;
    private String sign;


    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private USubChannelManager subChannelManager;

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UChannelMasterManager channelMasterManager;


    @Action("getToken")
    public void getLoginToken() {
        Log.d("=========================================getToken======================================================start");

        Log.d("==============================appID : %s", appID);
        Log.d("==============================channelID: %s", channelID);
        Log.d("==============================extension: %s", extension);
        Log.d("==============================sign: %s", sign);

        try {
            final UGame game = gameManager.getGame(this.appID);
            if (game == null) {
                renderState(StateCode.CODE_GAME_NONE, null);
                return;
            }

            final UChannel channel = channelManager.getChannel(this.channelID);
            if (channel == null) {
                renderState(StateCode.CODE_CHANNEL_NONE, null);
                return;
            }

            if (subChannelID != null && subChannelID > 0 && !subChannelManager.isSubChannelValid(channelID, subChannelID)) {
                renderState(StateCode.CODE_SUBCHANNEL_NONE, null);
                return;
            }

            if (channel.getAppID() != this.appID) {
                renderState(StateCode.CODE_CHANNEL_NOT_MATCH, null);
                return;
            }

            final UChannelMaster master = channelMasterManager.getChannelMaster(channel.getMasterID());
            if (master == null) {
                renderState(StateCode.CODE_CHANNEL_NONE, null);
                return;
            }

            String appkey = game.getAppkey();
            Log.d("==============================appkey: %s", appkey);

            UStringBuilder sb = new UStringBuilder();
            sb.append("appID=").append(this.appID)
                    .append("channelID=").append(this.channelID)
                    .append("extension=").append(this.extension == null ? "" : this.extension).append(appkey);


            String signStr = sb.toString();
            Log.d("服务端 签名字符串: %s", signStr);

            if (!userManager.isSignOK(signStr, sign)) {
                Log.e("签名不通过 :" + sign);
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            ISDKScript verifier = SDKCacheManager.getInstance().getSDKScript(channel, sdkVersionCode);

            if (verifier == null) {
                Log.e("the ISDKScript is not found . channelID:" + channelID);
                renderState(StateCode.CODE_VERIFY_FAILED, null);
                return;
            }

            Log.d("The auth url is " + channel.getChannelAuthUrl());
            Log.d("channel is " + channel.getChannelID() + ";extension is " + extension);

            verifier.verify(channel, extension, new ISDKVerifyListener() {
                @Override
                public void onSuccess(SDKVerifyResult sdkResult) {

                    try {
                        Log.d("user verify success. result:" + sdkResult.getUserID());
                        if (sdkResult.isSuccess() && !StringUtils.isEmpty(sdkResult.getUserID())) {
                            UUser user = null;
                            if (channel.getUniChannel() != null && channel.getUniChannel() != 0) {
                                //有互通渠道号，按照互通渠道号来查询用户
                                Log.d("query user with unichannel. channelId:" + channelID + ";uniChannel:" + channel.getUniChannel() + ";sdk UserId:" + sdkResult.getUserID());
                                user = userManager.getUserByUniChannel(channel.getAppID(), channel.getUniChannel(), sdkResult.getUserID());
                            }

                            if (user == null) {
                                user = userManager.getUserByCpID(channel.getAppID(), channel.getChannelID(), sdkResult.getUserID());
                            }

                            if (user == null) {
                                user = userManager.generateUser(channel, game, subChannelID, master.getNameSuffix(), sdkResult, deviceID);
                                user.setToken(UGenerator.generateToken(user, game.getAppSecret()));
                                userManager.saveUser(user);
                                userManager.addNewUserCount(user);      //增加当日新增用户实时统计
                                BLog.getInstance().printJson(BLog.T_USER, BLog.OPTYPE.USER.OP_REG, user);
                            } else {
                                user.setChannelUserName(sdkResult.getUserName() == null ? "" : sdkResult.getUserName());
                                user.setChannelUserNick(sdkResult.getNickName() == null ? "" : sdkResult.getNickName());
                                user.setLastLoginTime(new Date());
                                user.setToken(UGenerator.generateToken(user, game.getAppSecret()));
                                if (channel.getUniChannel() != null && channel.getUniChannel() != 0) {
                                    user.setUniChannel(channel.getUniChannel());
                                }
                                userManager.saveUser(user);
                                BLog.getInstance().printJson(BLog.T_USER, BLog.OPTYPE.USER.OP_LOGIN, user);
                            }

                            userManager.addUserCount(user); //增加当日活跃用户实时统计

                            JSONObject data = new JSONObject();
                            data.put("userID", user.getId());
                            data.put("sdkUserID", user.getChannelUserID());
                            data.put("username", user.getName());
                            data.put("sdkUserName", user.getChannelUserName());
                            data.put("token", user.getToken());
                            data.put("extension", sdkResult.getExtension());
                            data.put("timestamp", user.getLastLoginTime().getTime());
                            renderState(StateCode.CODE_SUCCESS, data);
                        } else {
                            renderState(StateCode.CODE_AUTH_FAILED, null);
                        }
                    } catch (Exception e) {
                        renderState(StateCode.CODE_AUTH_FAILED, null);
                        Log.e(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String errorMsg) {
                    Log.e("The user verify failed. errorMsg:" + errorMsg);
                    renderState(StateCode.CODE_AUTH_FAILED, null);
                }
            });
        } catch (Exception e) {
            Log.e(e.getMessage());
            e.printStackTrace();
            renderState(StateCode.CODE_AUTH_FAILED, null);
        }
    }

    private void renderState(int state, JSONObject data) {
        try {
            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("data", data);
            super.renderJson(json.toString());
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }


    /***
     * 上面协议返回客户端之后，开始连接登录游戏服。游戏服可以调用该协议进行再次登录认证。
     * 但是，该步骤是可选的。游戏服务器也可以自己验证token以及token的时效性，这样就不用来
     * U8Server进行再次登录认证了。
     *
     * 服务器自己验证token，根据U8Server分配给每个游戏参数中的AppSecret，按照生成token的
     * 规则，进行验证。同时，需要验证timestamp的时效性
     *
     */


    @Action("verifyAccount")
    public void loginVerify() {
        try {
            Log.d("=========================================verifyAccount======================================================start");
            Log.d("==============================userID : %s", userID);
            Log.d("==============================token: %s", token);
            Log.d("==============================sign: %s", sign);

            UUser user = userManager.getUser(this.userID);
            if (user == null) {
                renderState(StateCode.CODE_USER_NONE, null);
                return;
            }

            if (StringUtils.isEmpty(this.token)) {
                renderState(StateCode.CODE_VERIFY_FAILED, null);
                return;
            }

            UGame game = gameManager.getGame(user.getAppID());
            String appSecret = game.getAppSecret();
            Log.d("==============================appSecret: %s", appSecret);

            StringBuilder sb = new StringBuilder();
            sb.append("userID=").append(this.userID)
                    .append("token=").append(this.token)
                    .append(appSecret);

            Log.d("服务端 登录认证签名字符串 :%s", sb.toString());
            String newSign = EncryptUtils.md5(sb.toString());
            Log.d("服务端 sign :%s", newSign);

            Log.d("客户端 sign :%s", sign);

            if (!newSign.toLowerCase().equals(sign)) {
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            if (!userManager.checkUser(user, token)) {
                renderState(StateCode.CODE_TOKEN_ERROR, null);
                return;
            }

            JSONObject data = new JSONObject();
            data.put("userID", user.getId());
            data.put("username", user.getName());
            data.put("sdkUserID", user.getChannelUserID());
            data.put("sdkUserName", user.getChannelUserName());
            data.put("channelID", user.getChannelID());
            renderState(StateCode.CODE_SUCCESS, data);
            return;
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        renderState(StateCode.CODE_VERIFY_FAILED, null);
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Integer getSubChannelID() {
        return subChannelID;
    }

    public void setSubChannelID(Integer subChannelID) {
        this.subChannelID = subChannelID;
    }

    public String getSdkVersionCode() {
        return sdkVersionCode;
    }

    public void setSdkVersionCode(String sdkVersionCode) {
        this.sdkVersionCode = sdkVersionCode;
    }

}
