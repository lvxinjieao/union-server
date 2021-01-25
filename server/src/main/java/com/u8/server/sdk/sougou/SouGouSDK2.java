package com.u8.server.sdk.sougou;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜狗SDK V2
 * 登录认证地址：http://gamesdk.sogou.com/api/v2/login/verify
 * Created by ant on 2016/5/10.
 */
public class SouGouSDK2 implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try{

            JSONObject json = JSONObject.fromObject(extension);
            String sessionKey = json.getString("sessionKey");
            final String userID = json.getString("userID");

            StringBuilder sb = new StringBuilder();
            sb.append("gid=").append(URLEncoder.encode(channel.getCpAppID(), "UTF-8"))
                    .append("&sessionKey=").append(URLEncoder.encode(sessionKey, "UTF-8"))
                    .append("&userId=").append(URLEncoder.encode(userID, "UTF-8"))
                    .append("&"+channel.getCpAppSecret());

            String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

            Map<String,String> params = new HashMap<String, String>();
            params.put("gid", channel.getCpAppID());
            params.put("sessionKey", sessionKey);
            params.put("userId", userID);
            params.put("auth", sign);


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    try {

                        JSONObject json = JSONObject.fromObject(result);

                        if (json.optInt("code", -1) == 0) {
                            SDKVerifyResult vResult = new SDKVerifyResult(true, userID, "", "");

                            callback.onSuccess(vResult);

                            return;
                        }


                    } catch (Exception e) {
                        Log.e(e.getMessage(), e);
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);
                }

                @Override
                public void failed(String e) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }

            });

        }catch(Exception e){
            Log.e(e.getMessage(), e);
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
