package com.u8.server.sdk.yaolaiwan;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.u8.server.common.SignUtils;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;

public class YaoLaiWanSDK implements ISDKScript {

    public String app_id;
    public String mem_id;
    public String user_token;
    public String sign;

    public void verify(UChannel channel, String extension, final ISDKVerifyListener callback) {
        try {
            JSONObject json = JSONObject.parseObject(extension);
            app_id = channel.getCpAppID();
            mem_id = json.getString("mem_id");
            user_token = json.getString("user_token");
            sign = generateSign(app_id, mem_id, user_token, channel.getCpAppKey());

            String url = channel.getChannelAuthUrl();
            Log.i("【yao lai wan】  json: " + json.toString());
            Log.i("【yao lai wan】  url: " + url);

            Map<String, String> params = new HashMap<String, String>();
            params.put("app_id", app_id);
            params.put("mem_id", mem_id);
            params.put("user_token", user_token);
            params.put("sign", sign);

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {

                @Override
                public void completed(String content) {
                    Log.i("【yao lai wan】 success result: " + content);
                    JSONObject json = JSONObject.parseObject(content);
                    String status = json.getString("status");
                    if ("1".equals(status))
                        callback.onSuccess(new SDKVerifyResult(true, mem_id, mem_id, "", "登陆认证成功"));
                }

                @Override
                public void failed(String err) {
                    Log.i("【yao lai wan】  failed result: " + err);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (callback != null) {
            callback.onSuccess("");
        }
    }

    // 登录 生成sign
    public String generateSign(String appid, String mem_id, String user_token, String app_key) {
        String gignRes = "app_id=" + appid + "&mem_id=" + mem_id + "&user_token=" + user_token + "&app_key=" + app_key;
        String sign = SignUtils.md5(gignRes.toString());
        return sign;
    }

}
