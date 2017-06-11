package jp.onetake.prototypedon.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.fragment.TimelineFragment;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;

public class MainActivity extends AppCompatActivity implements TimelineFragment.TimelineEventListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_timeline, TimelineFragment.newInstance(InstanceHolder.getSingleton().get(0)));
		trans.commit();
	}

	@Override
	public void onLinkClick(Uri uri) {
		DebugLog.debug(getClass(), "URI = " + uri.toString());
	}
}
