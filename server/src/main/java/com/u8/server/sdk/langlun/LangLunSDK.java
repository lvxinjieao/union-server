package com.u8.server.sdk.langlun;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;

import net.sf.json.JSONObject;

public class LangLunSDK implements ISDKScript {

	private String userId;
	private String userName;
	private String token;

	public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

		try {
			JSONObject json = JSONObject.fromObject(extension);
			userId = json.optString("userId");
			userName = json.optString("userName");
			token = json.optString("token");
			callback.onSuccess(new SDKVerifyResult(true, userId, userName, userName));
		} catch (Exception e) {
			callback.onFailed(channel.getMaster().getSdkName() + " verify failed ");
			e.printStackTrace();
		}

	}

	public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
		if (callback != null) {
			Long o = order.getOrderID();
			Log.i("----------------------------------langlun : " + o);
			callback.onSuccess(String.valueOf(o));
		}
	}

}
