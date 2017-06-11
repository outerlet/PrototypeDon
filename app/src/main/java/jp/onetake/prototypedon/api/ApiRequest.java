package jp.onetake.prototypedon.api;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Field;

public abstract class ApiRequest {
	public enum Method {
		Get,
		Post,
	}

	private Context mContext;
	private int mIdentifier;

	public ApiRequest(Context context, int identifier) {
		mContext = context;
		mIdentifier = identifier;
	}

	public Context getContext() {
		return mContext;
	}

	public int getIdentifier() {
		return mIdentifier;
	}

	/**
	 * このオブジェクトの持つフィールド名とその値を、"key1=value1&key2=value2"フォーマットの文字列にして返す
	 * @return	"key1=value1&key2=value2"フォーマットの文字列
	 */
	public String createParams() {
		StringBuilder buffer = new StringBuilder();

		Class thisClass = getClass();
		buffer.append(createParams(thisClass, this));

		Class superClass = thisClass.getSuperclass();

		// クラスがObjectの場合はgetSuperClass()でnullが返されるはずだが、返されない場合があるようなので
		if (superClass != null && !superClass.equals(Object.class)) {
			String superParams = createParams(superClass, this);
			if (!TextUtils.isEmpty(superParams)) {
				buffer.append("&").append(superParams);
			}
		}

		return buffer.toString();
	}

	/**
	 * sourceオブジェクトの全フィールドとその値を、HTTPリクエストに投げるために
	 * "key1=value1&key2=value2"フォーマットの文字列にして返す
	 * @param cls		文字列の元となるクラスオブジェクト
	 * @param source	値を取り出すcls型のオブジェクト
	 * @return	"key1=value1&key2=value2"フォーマットの文字列
	 */
	private String createParams(Class cls, Object source) {
		StringBuilder buffer = new StringBuilder();

		Field[] fields = cls.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				buffer.append("&");
			}

			try {
				Field field = fields[i];
				buffer.append(field.getName()).append("=").append(field.get(source));
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
		}

		return buffer.toString();
	}

	public abstract String getPath();

	public abstract Method getMethod();

	public abstract ApiResponse createResponse();
}
