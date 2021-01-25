package com.u8.server.web.platform;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.data.UPlatformUser;
import com.u8.server.log.Log;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UPlatformUserManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 聚合平台测试账号处理逻辑,登录，注册，支付
 * Created by ant on 2017/7/7.
 */
@Controller
@Namespace("/user/default")
public class PlatformUserAction extends UActionSupport {

    //通用
    private String gameAppId;
    private String time;
    private String sign;

    //登录，注册
    private String name;
    private String password;

    //支付
    private String uid;
    private String price;
    private String productID;
    private String productName;
    private String orderID;
    private String payNotifyUrl;

    @Autowired
    private UPlatformUserManager platformUserManager;

    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UOrderManager orderManager;


    @Action("debugLogin")
    public void debugLogin() {

        try {

            UGame game = gameManager.getGame(Integer.valueOf(gameAppId));
            if (game == null) {
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            UPlatformUser user = platformUserManager.getUserByName(name);
            if (user == null) {
                user = new UPlatformUser();
                user.setName(name);
                user.setPassword(password);
                user.setCreateTime(new Date());
                platformUserManager.saveUser(user);
            }

            if (!user.getPassword().equals(password)) {
                Log.d("sdk login failed. user password is not matched.");
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            JSONObject data = new JSONObject();
            data.put("userId", user.getId() + "");
            data.put("username", user.getName());
            renderState(StateCode.CODE_SUCCESS, data);
        } catch (Exception e) {
            renderState(StateCode.CODE_FAIL, null);
            Log.e(e.getMessage(), e);
        }

    }

    @Action("login")
    public void login() {
        try {

            UGame game = gameManager.getGame(Integer.valueOf(gameAppId));
            if (game == null) {
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("name", name);
            params.put("password", password);
            params.put("gameAppId", gameAppId);
            params.put("time", time);
            String signStr = StringUtils.generateUrlSortedParamString(params, "&", true);
            signStr += "&key=" + game.getAppkey();
            String signLocal = EncryptUtils.md5(signStr).toLowerCase();
            if (!signLocal.equals(sign)) {
                Log.d("sdk login failed. sign not matched." + signStr);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            UPlatformUser user = platformUserManager.getUserByName(name);
            if (user == null) {
                Log.d("sdk login failed. user name is invalid." + name);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            if (!user.getPassword().equals(password)) {
                Log.d("sdk login failed. user password is not matched.");
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            JSONObject data = new JSONObject();
            data.put("userId", user.getId() + "");
            data.put("username", user.getName());
            renderState(StateCode.CODE_SUCCESS, data);

        } catch (Exception e) {
            renderState(StateCode.CODE_FAIL, null);
            Log.e(e.getMessage(), e);
        }
    }

    @Action("register")
    public void register() {
        try {

            UGame game = gameManager.getGame(Integer.valueOf(gameAppId));
            if (game == null) {
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("name", name);
            params.put("password", password);
            params.put("gameAppId", gameAppId);
            params.put("time", time);
            String signStr = StringUtils.generateUrlSortedParamString(params, "&", true);
            signStr += "&key=" + game.getAppkey();
            String signLocal = EncryptUtils.md5(signStr).toLowerCase();
            if (!signLocal.equals(sign)) {
                Log.d("sdk register failed. sign not matched." + signStr);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            UPlatformUser user = platformUserManager.getUserByName(name);
            if (user != null) {
                Log.d("sdk register failed. user name is alread exists." + name);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Log.d("sdk register failed. user password is empty.");
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            UPlatformUser newUser = new UPlatformUser();
            newUser.setName(name);
            newUser.setPassword(password);
            newUser.setCreateTime(new Date());
            platformUserManager.saveUser(newUser);

            JSONObject data = new JSONObject();
            data.put("userId", newUser.getId() + "");
            data.put("username", newUser.getName());
            renderState(StateCode.CODE_SUCCESS, data);

        } catch (Exception e) {
            renderState(StateCode.CODE_FAIL, null);
            Log.e(e.getMessage(), e);
        }
    }

    @Action("fast_register")
    public void fastRegister() {
        try {

            UGame game = gameManager.getGame(Integer.valueOf(gameAppId));
            if (game == null) {
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("gameAppId", gameAppId);
            params.put("time", time);
            String signStr = StringUtils.generateUrlSortedParamString(params, "&", true);
            signStr += "&key=" + game.getAppkey();
            String signLocal = EncryptUtils.md5(signStr).toLowerCase();
            if (!signLocal.equals(sign)) {
                Log.d("sdk fast register failed. sign not matched." + signStr);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            //一键注册测试账号，这里不做严格的限制和判断了

            UPlatformUser newUser = new UPlatformUser();
            newUser.setName("m" + System.currentTimeMillis());
            newUser.setPassword("123456");
            newUser.setCreateTime(new Date());
            platformUserManager.saveUser(newUser);

            JSONObject data = new JSONObject();
            data.put("userId", newUser.getId() + "");
            data.put("username", newUser.getName());
            data.put("password", newUser.getPassword());
            renderState(StateCode.CODE_SUCCESS, data);

        } catch (Exception e) {
            renderState(StateCode.CODE_FAIL, null);
            Log.e(e.getMessage(), e);
        }
    }

    @Action("pay")
    public void pay() {
        boolean paySuccess = false;
        try {

            UGame game = gameManager.getGame(Integer.valueOf(gameAppId));
            if (game == null) {
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            UPlatformUser user = platformUserManager.getUserByID(Integer.valueOf(uid));
            if (user == null) {
                Log.d("sdk pay failed. user is not exists. uid:" + uid);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("gameAppId", gameAppId);
            params.put("uid", uid);
            params.put("price", price);
            params.put("productID", productID);
            params.put("productName", productName);
            params.put("orderID", orderID);
            params.put("payNotifyUrl", payNotifyUrl);
            params.put("time", time);
            String signStr = StringUtils.generateUrlSortedParamString(params, "&", true);
            signStr += "&key=" + game.getAppkey();
            String signLocal = EncryptUtils.md5(signStr).toLowerCase();
            if (!signLocal.equals(sign)) {
                Log.d("sdk pay failed. sign not matched." + signStr);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }

            //测试充值这里，直接模拟充值成功，回调给游戏服务器。

            UOrder order = orderManager.getOrder(Long.valueOf(orderID));
            if (order == null) {
                Log.d("sdk pay failed. order is not exists.orderID:" + orderID);
                renderState(StateCode.CODE_FAIL, null);
                return;
            }
            renderState(StateCode.CODE_SUCCESS, null);
            paySuccess = true;
            SendAgent.resendCallbackToServer(orderManager, order);
        } catch (Exception e) {
            if (!paySuccess) {
                renderState(StateCode.CODE_FAIL, null);
            }
            Log.e(e.getMessage(), e);
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

    public String getGameAppId() {
        return gameAppId;
    }

    public void setGameAppId(String gameAppId) {
        this.gameAppId = gameAppId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPayNotifyUrl() {
        return payNotifyUrl;
    }

    public void setPayNotifyUrl(String payNotifyUrl) {
        this.payNotifyUrl = payNotifyUrl;
    }
}
