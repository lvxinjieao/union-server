package com.u8.server.sdk.vivo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;

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
import com.u8.server.utils.JsonUtils;

import freemarker.template.utility.StringUtil;
import net.sf.json.JSONObject;

/**
 * VIVO
 */
public class VivoSDK implements ISDKScript {

	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		try {
			JSONObject json = JSONObject.fromObject(extension);
			final String openid = json.getString("openid");
			final String name = json.getString("name");
			final String token = json.getString("token");
			Map<String, String> params = new HashMap<String, String>();
			params.put("authtoken", token);

			UHttpAgent.getInstance().post(channel.getChannelAuthUrl(), params, new UHttpFutureCallback() {

				public void completed(String content) {
					Log.e("The auth result is " + content);
					try {
						AuthInfo authInfo = (AuthInfo) JsonUtils.decodeJson(content, AuthInfo.class);
						if (authInfo.getRetcode() == 0) {
							callback.onSuccess(new SDKVerifyResult(true, openid, name, name));
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
			callback.onFailed(
					channel.getMaster().getSdkName() + " verify execute failed. the exception is " + e.getMessage());
		}

	}

	public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
		if (callback != null) {
			try {
				UChannel channel = order.getChannel();
				if (channel == null) {
					Log.e("The channel is not exists of order " + order.getOrderID());
					return;
				}

				String url = channel.getPayCallbackUrl();
				if (!TextUtils.isEmpty(url)) {
					callback.onSuccess(url);
				} else {
					callback.onFailed("the vivo order result is failed.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				callback.onFailed(e.getMessage());
			}
		}
	}

}
