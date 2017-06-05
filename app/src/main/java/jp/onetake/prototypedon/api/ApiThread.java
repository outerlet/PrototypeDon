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
 * API呼び出しを実行するスレッド<br />
 * コンストラクタで必要な情報を入れてstartすると、リスナにレスポンスや、失敗したときには例外が返る
 */
public abstract class ApiThread extends Thread {
	public interface ApiResultListener {
		void onApiSuccess(int identifier, ApiResponse response);
		void onApiFailure(int identifier, ApiException exception);
	}

	// 実行結果をUIスレッドに戻すためのHandler
	private static class ApiResultHandler extends Handler {
		private ApiThread mmThread;

		ApiResultHandler(ApiThread thread) {
			mmThread = thread;
		}

		@Override
		public void handleMessage(Message msg) {
			if (mmThread.mListener != null) {
				ApiResult result = (ApiResult)msg.obj;

				switch (msg.what) {
					case MSG_WHAT_SUCCESS:
						mmThread.mListener.onApiSuccess(result.identifier, result.response);
						break;
					case MSG_WHAT_FAILURE:
						mmThread.mListener.onApiFailure(result.identifier, result.exception);
						break;
				}
			}
		}
	}

	// 実行結果を伝えるために必要なオブジェクトを保持させるためのクラス
	private static class ApiResult {
		int identifier;
		ApiResponse response;
		ApiException exception;

		ApiResult(int identifier, ApiResponse response, ApiException exception) {
			this.identifier = identifier;
			this.response = response;
			this.exception = exception;
		}
	}

	private static final int MSG_WHAT_SUCCESS = 10001;
	private static final int MSG_WHAT_FAILURE = 10002;

	private Context mContext;
	private MastodonInstance mInstance;
	private ApiRequest mRequest;
	private ApiResultHandler mHandler;
	private ApiResultListener mListener;

	/**
	 * requestから取得されるHTTPメソッドに応じて、適切なAPI実行用のスレッドオブジェクトを生成して返す
	 * @param context	コンテキスト
	 * @param instance	MastodonInstanceオブジェクト
	 * @param request	リクエストオブジェクト
	 * @return	requestを適切に処理するためのスレッドオブジェクト
	 */
	public static ApiThread newInstance(Context context, MastodonInstance instance, ApiRequest request) {
		if (request.getMethod() == ApiRequest.Method.Get) {
			return new ApiGetThread(context, instance, request);
		}

		return new ApiPostThread(context, instance, request);
	}

	protected ApiThread(Context context, MastodonInstance instance, ApiRequest request) {
		mContext = context;
		mInstance = instance;
		mRequest = request;
		mHandler = new ApiResultHandler(this);
	}

	public void setListener(ApiResultListener listener) {
		mListener = listener;
	}

	protected HttpURLConnection prepareConnection(URL url) throws ApiException {
		try {
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(mRequest.getMethod().toString().toUpperCase());
			connection.setConnectTimeout(mContext.getResources().getInteger(R.integer.api_connection_timeout));
			connection.setReadTimeout(mContext.getResources().getInteger(R.integer.api_read_timeout));
			connection.setInstanceFollowRedirects(false);
			connection.setDoInput(true);

			connection.setRequestProperty("User-Agent", mContext.getString(R.string.api_user_agent));

			if (!TextUtils.isEmpty(mInstance.getAccessToken())) {
				connection.setRequestProperty("Authorization", "Bearer " + mInstance.getAccessToken());
			}

			return connection;
		} catch (IOException ioe) {
			throw new ApiException(ioe);
		}
	}

	protected ApiResponse prepareResponse(HttpURLConnection connection) throws ApiException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder buffer = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {
				buffer.append(str);
			}

			android.util.Log.d(getClass().getSimpleName(), buffer.toString());

			JSONObject json = new JSONObject(buffer.toString());

			if (json.has("error")) {
				throw new ApiException(json.getString("error"));
			}

			ApiResponse response = ApiResponseFactory.createResponse(mRequest);

			if (response == null) {
				throw new RuntimeException("Cannot create suitable response class. Check 'ApiResponseFactory'");
			}

			response.parse(json);

			return response;
		} catch (IOException | JSONException e) {
			throw new ApiException(e);
		}
	}

	protected ApiRequest getRequest() {
		return mRequest;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(String.format(
					Locale.US, mContext.getString(R.string.api_path_format), mInstance.getHostName(), mRequest.getPath()));

			HttpURLConnection connection = prepareConnection(url);

			connect(connection);

			int resCode = connection.getResponseCode();
			if (resCode == HttpURLConnection.HTTP_OK) {
				ApiResponse response = prepareResponse(connection);

				sendResultToHandler(MSG_WHAT_SUCCESS, response, null);
			} else {
				String msg = String.format(Locale.US, mContext.getString(R.string.message_format_http_error), resCode);
				sendResultToHandler(MSG_WHAT_FAILURE, null, new ApiException(msg));
			}
		} catch (IOException | ApiException e) {
			sendResultToHandler(MSG_WHAT_FAILURE, null, new ApiException(e));
		}
	}

	private void sendResultToHandler(int what, ApiResponse response, ApiException exception) {
		mHandler.obtainMessage(
				what, new ApiResult(mRequest.getIdentifier(), response, exception)).sendToTarget();
	}

	protected abstract void connect(HttpURLConnection connection) throws ApiException;
}
