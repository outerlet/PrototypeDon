package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;

public class InstanceAdapter extends ArrayAdapter<Instance> {
	private class ViewHolder {
		TextView textView;
	}

	public InstanceAdapter(Context context) {
		super(context, -1);
	}

	public void updateAndNotify() {
		clear();

		InstanceHolder holder = InstanceHolder.getSingleton();
		for (int i = 0; i < holder.size(); i++) {
			add(holder.get(i));
		}

		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_listitem_instance, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView)convertView.findViewById(R.id.textview_instance);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		viewHolder.textView.setText(getItem(position).getHostName());

		return convertView;
	}
}
