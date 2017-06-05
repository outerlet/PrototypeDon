package jp.onetake.prototypedon.api;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jp.onetake.prototypedon.common.MastodonInstance;

/**
 * API呼び出しをPOSTメソッドで行うスレッド
 */
public class ApiPostThread extends ApiThread {
	protected ApiPostThread(Context context, MastodonInstance instance, ApiRequest request) {
		super(context, instance, request);
	}

	protected HttpURLConnection prepareConnection(URL url) throws ApiException {
		HttpURLConnection connection = super.prepareConnection(url);
		connection.setDoOutput(true);
		return connection;
	}

	@Override
	protected void connect(HttpURLConnection connection) throws ApiException {
		OutputStreamWriter writer = null;

		// 無駄にOutputStreamWriterは生成したくないので、try-catch-resourcesは使わない
		try {
			String params = getRequest().createParams();

			if (!TextUtils.isEmpty(params)) {
				writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
				writer.write(params);
				writer.flush();
			}

			connection.connect();
		} catch (IOException ioe) {
			throw new ApiException(ioe);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignore) {}
			}
		}
	}
}
