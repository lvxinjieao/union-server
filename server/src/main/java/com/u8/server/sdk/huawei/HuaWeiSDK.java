package com.u8.server.sdk.huawei;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 华为3.0 SDK登录认证
 * https://jos-api.cloud.huawei.com/gameservice/api/gbClientApi
 */
public class HuaWeiSDK implements ISDKScript {

    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

        try {
            JSONObject json = JSONObject.fromObject(extension);
            final String ts = json.getString("ts");
            final String playerId = json.getString("playerId");
            final String playerName = json.getString("playerName");
            final String playerLevel = json.getString("playerLevel");
            final String playerSSign = json.getString("playerSSign");

            SDKVerifyResult vResult = new SDKVerifyResult(true, playerId, playerName, "");
            callback.onSuccess(vResult);

//            Map<String, String> mockRequestParams = new HashMap<String, String>();
//            mockRequestParams.put("method","external.hms.gs.checkPlayerSign");
//            mockRequestParams.put("appId",channel.getCpAppID());
//            mockRequestParams.put("cpId",channel.getCpID());
//            mockRequestParams.put("ts",ts);
//            mockRequestParams.put("playerId",playerId);
//            mockRequestParams.put("playerLevel",playerLevel);
//            mockRequestParams.put("playerSSign", playerSSign);
//
//            String signStr = StringUtils.generateUrlSortedParamString(mockRequestParams, "&", true, true);
//            signStr = signStr.replaceAll("\\+", "%20").replaceAll("\\*", "%2A"); // 空格和星号转义
//
//            String sign = RSAUtils.sign(signStr, channel.getCpPayPriKey(), RSAUtils.SIGNATURE_SHA256WithRSA);
//            mockRequestParams.put("cpSign", sign);
//            Log.d("huawei3 auth sign str:%s; sign:%s", signStr, sign);
//
//            String url = channel.getChannelAuthUrl();
//            UHttpAgent.getInstance().post(url, mockRequestParams, new UHttpFutureCallback() {
//                @Override
//                public void completed(String result) {
//
//                    try {
//
//                        JSONObject json = JSONObject.fromObject(result);
//
//                        if(json.optInt("rtnCode", -1) == 0){
//                            SDKVerifyResult vResult = new SDKVerifyResult(true, playerId, "", "");
//                            callback.onSuccess(vResult);
//                            return;
//                        }
//
//                    } catch (Exception e) {
//                        Log.e(e.getMessage(), e);
//                    }
//
//                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the result is " + result);
//                }
//
//                @Override
//                public void failed(String e) {
//                    callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + e);
//                }
//
//            });
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if (callback != null) {
            callback.onSuccess("");
        }
    }
}
