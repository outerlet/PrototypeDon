package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import jp.onetake.prototypedon.R;

public class TimelineFooterView extends FrameLayout {
	private View mStartView;
	private ProgressBar mProgressBar;

	public TimelineFooterView(Context context) {
		this(context, null);
	}

	public TimelineFooterView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(R.layout.view_listfooter_timeline, this, true);

		mStartView = view.findViewById(R.id.layout_start_loading);
		mProgressBar = (ProgressBar)view.findViewById(R.id.progress_loading);
	}

	public void setProgress(boolean progress) {
		mStartView.setVisibility(progress ? View.INVISIBLE : View.VISIBLE);
		mProgressBar.setVisibility(progress ? View.VISIBLE : View.INVISIBLE);
	}
}
