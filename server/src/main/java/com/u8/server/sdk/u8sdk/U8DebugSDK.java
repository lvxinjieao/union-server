package com.u8.server.sdk.u8sdk;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import net.sf.json.JSONObject;

/**
 * U8平台Debug账号，默认渠道号88888
 * Created by ant on 2017/7/8.
 */
public class U8DebugSDK implements ISDKScript{

    @Override
    public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {
        try{
            JSONObject json = JSONObject.fromObject(extension);
            String userId = json.optString("userId");
            String username = json.optString("username");

            //U8默认测试模式，无需认证
            callback.onSuccess(new SDKVerifyResult(true, userId, username, ""));


        }catch (Exception e){
            callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
            Log.e(e.getMessage());
        }
    }

    @Override
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
