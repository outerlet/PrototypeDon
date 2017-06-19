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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.Status;
import jp.onetake.prototypedon.net.ImageLoadThread;
import jp.onetake.prototypedon.text.StatusLinkMovementMethod;

public class TimelineAdapter extends ArrayAdapter<Status> {
	private class ViewHolder {
		ImageView avatarView;
		TextView displayNameView;
		TextView userNameView;
		TextView createdAtView;
		TextView textView;
	}

	private AvatarHolder mHolder;
	private StatusLinkMovementMethod mMethod;

	public TimelineAdapter(Context context, StatusLinkMovementMethod method) {
		super(context, -1);

		mHolder = new AvatarHolder(context);
		mMethod = method;
	}

	public void setInstance(Instance instance) {
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

			convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_listitem_timeline, parent, false);
			viewHolder.avatarView = (ImageView)convertView.findViewById(R.id.imageview_avatar);
			viewHolder.displayNameView = (TextView)convertView.findViewById(R.id.textview_display_name);
			viewHolder.userNameView = (TextView)convertView.findViewById(R.id.textview_user_name);
			viewHolder.createdAtView = (TextView)convertView.findViewById(R.id.textview_created_at);
			viewHolder.textView = (TextView)convertView.findViewById(R.id.textview_content);
			viewHolder.textView.setMovementMethod(mMethod);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		Status status = getItem(position);

		// contentのセット
		//noinspection deprecation
		viewHolder.textView.setText(Html.fromHtml(status.content));

		// Display Nameのセット
		viewHolder.displayNameView.setText(status.account.displayName);

		// User Nameのセット
		viewHolder.userNameView.setText(String.format(Locale.JAPAN, "@%1$s", status.account.username));

		// トゥート日時(現在時刻からの相対時間)
		String previous = getRelativeTimeText(status.createdAt);
		if (previous != null) {
			viewHolder.createdAtView.setText(previous);
		}

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

	private String getRelativeTimeText(String dateText) {
		try {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
			format.setTimeZone(timeZone);

			Date date = format.parse(dateText);
			long second = (new Date().getTime() - date.getTime()) / 1000;
			if (second < 60) {
				return second + "秒前";
			}

			long minutes = second / 60;
			if (minutes < 60) {
				return minutes + "分前";
			}

			long hours = minutes / 60;
			if (hours < 24) {
				return hours + "時間前";
			}

			return (hours / 24) + "日前";
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return null;
	}
}
