package com.u8.server.sdk.nianwan;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;

import net.sf.json.JSONObject;

public class NianWanSDK implements ISDKScript {

	private String user_id;
	private String user_name;
	private String token;

	public void verify(UChannel channel, String extension, ISDKVerifyListener callback) {

		try {
			JSONObject json = JSONObject.fromObject(extension);
			user_id = json.optString("account_id");
			user_name = json.optString("account");
			token = json.optString("token");
			callback.onSuccess(new SDKVerifyResult(true, user_id, user_name, user_name));
		} catch (Exception e) {
			callback.onFailed(channel.getMaster().getSdkName() + " verify failed ");
			e.printStackTrace();
		}

	}

	public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
		if (callback != null) {
			Long o = order.getOrderID();
			Log.i("----------------------------------mini : " + o);
			callback.onSuccess(String.valueOf(o));
		}
	}

}
