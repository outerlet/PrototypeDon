package jp.onetake.prototypedon.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Instance;

/**
 * API呼び出しを実行するスレッド<br />
 * コンストラクタで必要な情報を入れてstartすると、リスナにレスポンスや、失敗したときには例外が返る
 */
public abstract class ApiExecuteThread extends Thread {
	public interface ApiResultListener {
		void onApiSuccess(int apiId, ApiResponse response);
		void onApiFailure(int apiId, ApiException exception);
	}

	// 実行結果をUIスレッドに戻すためのHandler
	private static class ApiResultHandler extends Handler {
		private ApiExecuteThread mmThread;

		ApiResultHandler(ApiExecuteThread thread) {
			mmThread = thread;
		}

		@Override
		public void handleMessage(Message msg) {
			if (mmThread.mListener != null) {
				ApiResult result = (ApiResult)msg.obj;

				switch (msg.what) {
					case MSG_WHAT_SUCCESS:
						mmThread.mListener.onApiSuccess(result.apiId, result.response);
						break;
					case MSG_WHAT_FAILURE:
						mmThread.mListener.onApiFailure(result.apiId, result.exception);
						break;
				}
			}
		}
	}

	// 実行結果を伝えるために必要なオブジェクトを保持させるためのクラス
	private static class ApiResult {
		int apiId;
		ApiResponse response;
		ApiException exception;

		ApiResult(int apiId, ApiResponse response, ApiException exception) {
			this.apiId = apiId;
			this.response = response;
			this.exception = exception;
		}
	}

	private static final int MSG_WHAT_SUCCESS = 10001;
	private static final int MSG_WHAT_FAILURE = 10002;

	private Context mContext;
	private Instance mInstance;
	private ApiRequest mRequest;
	private ApiResultHandler mHandler;
	private ApiResultListener mListener;

	/**
	 * requestから取得されるHTTPメソッドに応じて、適切なAPI実行用のスレッドオブジェクトを生成して返す
	 * @param instance	Instanceオブジェクト
	 * @param request	リクエストオブジェクト
	 * @return	requestを適切に処理するためのスレッドオブジェクト
	 */
	public static ApiExecuteThread newInstance(Instance instance, ApiRequest request) {
		if (request.getMethod() == ApiRequest.Method.Get) {
			return new ApiGetExecuteThread(request.getContext(), instance, request);
		}

		return new ApiPostExecuteThread(request.getContext(), instance, request);
	}

	protected ApiExecuteThread(Context context, Instance instance, ApiRequest request) {
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

			ApiResponse response = mRequest.createResponse();

			if (response == null) {
				throw new RuntimeException("Request returns null response object.");
			}

			response.parse(buffer.toString());

			return response;
		} catch (IOException ioe) {
			throw new ApiException(ioe);
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
				what, new ApiResult(mRequest.getApiId(), response, exception)).sendToTarget();
	}

	protected abstract void connect(HttpURLConnection connection) throws ApiException;
}
