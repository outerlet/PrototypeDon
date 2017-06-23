package jp.onetake.prototypedon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.fragment.TimelineFragment;
import jp.onetake.prototypedon.mastodon.Instance;

public class SingleTimelineActivity extends TimelineBaseActivity {
	private static final String KEY_DATA		= "SingleTimelineActivity.KEY_DATA";
	private static final String KEY_INSTANCE	= "SingleTimelineActivity.KEY_INSTANCE";
	private static final String KEY_TITLE		= "SingleTimelineActivity.KEY_TITLE";
	private static final String KEY_HASHTAG		= "SingleTimelineActivity.KEY_HASHTAG";

	public static Intent newLauncherIntent(Context context, Instance instance, String title, String hashTag) {
		Bundle data = new Bundle();
		data.putParcelable(KEY_INSTANCE, instance);
		data.putString(KEY_TITLE, title);
		data.putString(KEY_HASHTAG, hashTag);

		Intent intent = new Intent(context, SingleTimelineActivity.class);
		intent.putExtra(KEY_DATA, data);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_single_timeline);

		Bundle data = getIntent().getBundleExtra(KEY_DATA);

		Toolbar toolbar = getToolbar();
		toolbar.setTitle(data.getString(KEY_TITLE));
		toolbar.setNavigationIcon(R.mipmap.ic_close_white);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		Instance instance = data.getParcelable(KEY_INSTANCE);
		setCurrentInstance(instance);

		String hashTag = data.getString(KEY_HASHTAG);

		TimelineFragment fragment = TimelineFragment.newInstance(
				instance, hashTag, getResources().getInteger(R.integer.api_id_hashtag_timeline));

		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_timeline, fragment);
		trans.commit();
	}

	@Override
	public void onLoadFailure(ApiException exception) {
		Snackbar.make(findViewById(R.id.layout_timeline), R.string.message_get_timeline_error, Snackbar.LENGTH_SHORT).show();
	}
}
