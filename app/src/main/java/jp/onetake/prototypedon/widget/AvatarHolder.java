package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import java.net.MalformedURLException;
import java.net.URL;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Account;
import jp.onetake.prototypedon.net.ImageLoadThread;

public class AvatarHolder {
	private Context mContext;
	private SparseArray<Bitmap> mBitmaps;

	public AvatarHolder(Context context) {
		mContext = context;
		mBitmaps = new SparseArray<>();
	}

	public Bitmap get(final Account account, final ImageLoadThread.ImageLoadListener listener) {
		Bitmap bitmap = mBitmaps.get(account.id);

		if (bitmap == null) {
			try {
				URL url = new URL(account.avatar);

				ImageLoadThread thread = new ImageLoadThread(
						url,
						mContext.getResources().getInteger(R.integer.api_connection_timeout),
						mContext.getResources().getInteger(R.integer.api_read_timeout));
				thread.setListener(new ImageLoadThread.ImageLoadListener() {
					@Override
					public void onLoad(Bitmap bitmap) {
						mBitmaps.put(account.id, bitmap);

						if (listener != null) {
							listener.onLoad(bitmap);
						}
					}
				});
				thread.start();
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
		}

		return bitmap;
	}

	public void clear() {
		mBitmaps.clear();
	}
}
