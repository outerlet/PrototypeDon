package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.status.Status;
import jp.onetake.prototypedon.text.StatusLinkMovementMethod;

public class TimelineAdapter extends ArrayAdapter<Status> {
	private class ViewHolder {
		TextView textView;
	}

	private StatusLinkMovementMethod mMethod;

	public TimelineAdapter(Context context, StatusLinkMovementMethod method) {
		super(context, -1);

		mMethod = method;
	}

	public void add(List<Status> statusList) {
		for (Status status : statusList) {
			add(status);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();

			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timeline, parent, false);
			holder.textView = (TextView)convertView.findViewById(R.id.textview_content);
			holder.textView.setMovementMethod(mMethod);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		Status status = getItem(position);
		holder.textView.setText(Html.fromHtml(status.content));

		return convertView;
	}
}
