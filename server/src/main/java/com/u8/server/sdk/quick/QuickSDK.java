package com.u8.server.sdk.quick;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;

import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 快发助手SDK
 * Created by xiaohei on 16/10/16.
 */
public class QuickSDK implements ISDKScript {


    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {


        try {

            Log.d("the quick verify param is %s", extension);

            JSONObject json = JSONObject.fromObject(extension);
            String token = json.getString("token");
            final String uid = json.getString("uid");
            final String channel_code = json.getString("channel_code");
            final String unique_id = uid + "@" + channel_code;

            String product_code = channel.getCpAppID();//product_code -- appId
            String verifyUrl = channel.getChannelAuthUrl();
            String product_key = channel.getCpAppKey();//product_key -- appKey

            Log.d("the quick verify url is %s", verifyUrl);

            UHttpAgent httpClient = UHttpAgent.getInstance();

//            Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
//                public int compare(String obj1, String obj2) {
//                    // 降序排序
//                    return obj1.compareTo(obj2);
//                }
//            });

            //初始化
            Map<String, String> params = new HashMap<String, String>(); //存储post提交的参数
            params.put("token", token);  //放真实token
            params.put("product_code", product_code); //放真实gamekey
            params.put("uid", uid);
            params.put("channel_code", channel_code);

            Log.d("the quick verify params is %s", params.toString());


            httpClient.post(verifyUrl, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {

                    Log.d("The auth result is " + result);

                    if ("1".equals(result)) {
                        callback.onSuccess(new SDKVerifyResult(true, unique_id, "", ""));
                        return;
                    }

                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the post result is " + result);

                }

                @Override
                public void failed(String e) {
                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
                }
            });


        } catch (Exception e) {
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
            Log.e(e.getMessage());
        }

    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (callback != null) {
            callback.onSuccess(user.getChannel().getPayCallbackUrl());
        }
    }

}
