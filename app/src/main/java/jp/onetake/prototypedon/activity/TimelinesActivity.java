package jp.onetake.prototypedon.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.fragment.TimelineFragment;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelinesActivity extends AppCompatActivity implements TimelineFragment.TimelineEventListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelines);

		ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager_tab);
		viewPager.setAdapter(new TimelineFragmentPagerAdapter(this, getSupportFragmentManager(), 0));
		viewPager.setOffscreenPageLimit(Attachment.Type.values().length);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tabs);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public void onLinkClick(Uri uri) {
		DebugLog.debug(getClass(), "URI = " + uri.toString());
	}
}
