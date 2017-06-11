package jp.onetake.prototypedon.api;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;

import jp.onetake.prototypedon.mastodon.Instance;

/**
 * API呼び出しをGETメソッドで行うスレッド
 */
public class ApiGetExecuteThread extends ApiExecuteThread {
	protected ApiGetExecuteThread(Context context, Instance instance, ApiRequest request) {
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
