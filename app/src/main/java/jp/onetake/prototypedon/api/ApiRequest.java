package jp.onetake.prototypedon.api;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class ApiRequest {
	public enum Method {
		Get,
		Post,
	}

	private Context mContext;
	private int mApiId;
	private Map<String, String> mParams;

	public ApiRequest(Context context, int apiId) {
		mContext = context;
		mApiId = apiId;
		mParams = new HashMap<>();
	}

	public Context getContext() {
		return mContext;
	}

	public int getApiId() {
		return mApiId;
	}

	public void addParameter(String key, String value) {
		mParams.put(key, value);
	}

	/**
	 * このオブジェクトの持つフィールド名とその値を、"key1=value1&key2=value2"フォーマットの文字列にして返す
	 * @return	"key1=value1&key2=value2"フォーマットの文字列
	 */
	public String createParams() {
		StringBuilder buffer = new StringBuilder();

		for (String key : mParams.keySet()) {
			if (buffer.length() > 0) {
				buffer.append("&");
			}

			try {
				String value = URLEncoder.encode(mParams.get(key), "UTF-8");
				buffer.append(key).append("=").append(value);
			} catch (UnsupportedEncodingException uee) {
				uee.printStackTrace();
			}
		}

		return buffer.toString();
	}

	public abstract String getPath();

	public abstract Method getMethod();

	public abstract ApiResponse createResponse();
}
