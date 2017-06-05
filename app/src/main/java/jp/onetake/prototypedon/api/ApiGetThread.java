package jp.onetake.prototypedon.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.common.MastodonInstance;

/**
 * API呼び出しをGETメソッドで行うスレッド
 */
public class ApiGetThread extends ApiThread {
	protected ApiGetThread(Context context, MastodonInstance instance, ApiRequest request) {
		super(context, instance, request);
	}

	protected void connect(HttpURLConnection connection) throws ApiException {
		try {
			connection.connect();
		} catch (IOException ioe) {
			throw new ApiException(ioe);
		}
	}
}
