package jp.onetake.prototypedon.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.onetake.prototypedon.util.DebugLog;

public class ImageLoadThread extends Thread {
	public interface ImageLoadListener {
		void onLoad(Bitmap bitmap);
	}

	private static class ResultHandler extends Handler {
		ImageLoadListener mmListener;

		void setListener(ImageLoadListener listener) {
			mmListener = listener;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_WHAT_BITMAP_LOADED && mmListener != null) {
				mmListener.onLoad((Bitmap)msg.obj);
			}
		}
	}

	private static final int MSG_WHAT_BITMAP_LOADED = 10001;

	private URL mUrl;
	private int mConnectionTimeout;
	private int mReadTimeout;
	private ResultHandler mHandler;

	public ImageLoadThread(URL url, int connectionTimeout, int readTimeout) {
		mUrl = url;
		mConnectionTimeout = connectionTimeout;
		mReadTimeout = readTimeout;

		mHandler = new ResultHandler();
	}

	public void setListener(ImageLoadListener listener) {
		mHandler.setListener(listener);
	}

	@Override
	public void run() {
		BufferedInputStream input = null;
		ByteArrayOutputStream output = null;

		try {
			HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(mConnectionTimeout);
			conn.setReadTimeout(mReadTimeout);
			conn.setUseCaches(true);
			conn.setDoInput(true);
			conn.connect();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				DebugLog.debug(getClass(), "(ERROR) HTTP Response code = " + conn.getResponseCode());
				return;
			}

			output = new ByteArrayOutputStream();
			input = new BufferedInputStream(conn.getInputStream());
			byte[] buffer = new byte[2048];
			int read;
			while ((read = input.read(buffer)) > 0) {
				output.write(buffer, 0, read);
			}

			output.flush();

			byte[] bytes = output.toByteArray();

			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			mHandler.obtainMessage(MSG_WHAT_BITMAP_LOADED, bitmap).sendToTarget();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ignore) {}
			}

			if (output != null) {
				try {
					output.close();
				} catch (IOException ignore) {}
			}
		}
	}
}
