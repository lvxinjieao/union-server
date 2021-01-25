package com.u8.server.sdk.dashen;

import com.u8.server.common.SignUtils;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DaShenSDK implements ISDKScript {

    private String uid;
    private String token;

    private String appId;
    private String appKey;

    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

        String url = channel.getChannelAuthUrl();
        appId = channel.getCpAppID();
        appKey = channel.getCpAppKey();

        JSONObject json = JSONObject.fromObject(extension);
        uid = json.optString("uid");
        token = json.optString("token");


        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("uid", uid);
        params.put("token", token);
        String sign = generateSign(appId, token, uid, appKey);
        params.put("sign", sign);

        String map2json = JsonUtils.map2json(params);

        Log.i("【da shen】  url: " + url);
        Log.i("【da shen】  json: " + map2json);

        try {


            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            UHttpAgent.getInstance().post(url, headers, new StringEntity(map2json), new UHttpFutureCallback() {

                @Override
                public void completed(String content) {
                    Log.i("【da shen】  result : " + content);
                    JSONObject jsonObject = JSONObject.fromObject(content);
                    JSONObject body = jsonObject.getJSONObject("body");
                    if (body != null) {
                        String uid = body.getString("uid");
                        String userName = body.getString("userName");
                        callback.onSuccess(new SDKVerifyResult(true, uid, userName, userName));
                    } else {
                        callback.onFailed(channel.getMaster().getSdkName() + " verify failed ");
                    }
                }

                @Override
                public void failed(String err) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed ");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (callback != null) {
            Long o = order.getOrderID();
            Log.i("----------------------------------【da shen】 : " + o);
            callback.onSuccess(String.valueOf(o));
        }
    }

    //登录 生成sign
    public String generateSign(String appId, String token, String uid, String appKey) {
        String gignRes =
                "appId" + appId +
                        "token" + token +
                        "uid" + uid +
                        appKey;
        Log.i("【da shen】  sign_str : " + gignRes);
        String sign = SignUtils.md5(gignRes.toString());
        return sign;
    }


}
