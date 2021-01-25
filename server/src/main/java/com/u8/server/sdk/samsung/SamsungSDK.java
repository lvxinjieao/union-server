package com.u8.server.sdk.samsung;

import com.u8.server.common.SignUtils;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.sdk.huawei.huawei.Base64Util;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 三星SDK
 * https://siapcn.galaxyappstore.com/samsung/coupon-merch/player/playerInfo
 */
public class SamsungSDK implements ISDKScript {

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            final String username = json.getString("username");

            Map<String, String> params = new HashMap<String, String>();
            params.put("applyNo", channel.getCpAppID());
            params.put("uid", uid);
            params.put("sign", getSign(uid, channel.getCpAppID()));

            UHttpAgent.getInstance().get(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {

                @Override
                public void completed(String result) {

                    Log.d("samsung new sdk auth result is " + result);

                    try {
                        JSONObject json = JSONObject.fromObject(result);
                        if (json != null && json.optInt("code", -1) == 0) {
                            callback.onSuccess(new SDKVerifyResult(true, uid, username, ""));
                            return;
                        }
                    } catch (Exception e) {
                        Log.e(e.getMessage(), e);
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
            callback.onSuccess("");
        }
    }


    public String getSign(String uid, String applyNo) {
        StringBuffer sb = new StringBuffer();
        sb.append("uid=");
        sb.append(uid);
        sb.append("&");
        sb.append("applyNo=");
        sb.append(applyNo);

        String s = SignUtils.md5(sb.toString());
        return Base64Util.encode(s.getBytes());
    }
}
