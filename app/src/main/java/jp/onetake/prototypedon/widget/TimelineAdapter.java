package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.Status;
import jp.onetake.prototypedon.net.ImageLoadThread;
import jp.onetake.prototypedon.text.StatusLinkMovementMethod;

public class TimelineAdapter extends ArrayAdapter<Status> {
	private class ViewHolder {
		TextView textView;
		ImageView avatarView;
	}

	private AvatarHolder mHolder;
	private StatusLinkMovementMethod mMethod;
	private Instance mInstance;

	public TimelineAdapter(Context context, StatusLinkMovementMethod method) {
		super(context, -1);

		mHolder = new AvatarHolder(context);
		mMethod = method;
	}

	public void setInstance(Instance instance) {
		mInstance = instance;

		mHolder.setHostName(instance.getHostName());
	}

	public void add(List<Status> statusList) {
		for (Status status : statusList) {
			add(status);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timeline, parent, false);
			viewHolder.avatarView = (ImageView)convertView.findViewById(R.id.imageview_avatar);
			viewHolder.textView = (TextView)convertView.findViewById(R.id.textview_content);
			viewHolder.textView.setMovementMethod(mMethod);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		Status status = getItem(position);

		// contentのセット
		viewHolder.textView.setText(Html.fromHtml(status.content));

		// アバター画像のセット(未取得ならロード)
		final ViewHolder holder = viewHolder;
		Bitmap avatarBitmap = mHolder.get(status.account, new ImageLoadThread.ImageLoadListener() {
			@Override
			public void onLoad(Bitmap bitmap) {
				holder.avatarView.setImageBitmap(bitmap);
			}
		});
		viewHolder.avatarView.setImageBitmap(avatarBitmap);

		return convertView;
	}
}
