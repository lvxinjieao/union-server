package com.u8.server.sdk.xiaomi;

import java.util.HashMap;
import java.util.Map;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.utils.HmacSHA1Encryption;
import com.u8.server.utils.JsonUtils;

import net.sf.json.JSONObject;

/**
 * 小米SDK
 */
public class XiaoMiSDK implements ISDKScript {

	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);
		final String sid = json.getString("sid");
		final String session = json.getString("token");

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("appId", channel.getCpAppID());
			params.put("session", session);
			params.put("uid", sid);

			StringBuilder sb = new StringBuilder();
			sb.append("appId=").append(channel.getCpAppID()).append("&session=").append(session).append("&uid=").append(sid);

			String signature = HmacSHA1Encryption.HmacSHA1Encrypt(sb.toString(), channel.getCpAppSecret());
			params.put("signature", signature);
			UHttpAgent.getInstance().get(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {

				public void completed(String content) {
					try {
						AuthInfo info = (AuthInfo) JsonUtils.decodeJson(content, AuthInfo.class);
						if (info != null && info.getErrcode() == 200) {
							SDKVerifyResult vResult = new SDKVerifyResult(true, sid + "", "", "");
							callback.onSuccess(vResult);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				public void failed(String err) {
					callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
			callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
		}

	}

	public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
		if (callback != null) {
			callback.onSuccess("");
		}
	}

}
