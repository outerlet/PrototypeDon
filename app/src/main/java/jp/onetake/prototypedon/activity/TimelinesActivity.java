package jp.onetake.prototypedon.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelinesActivity extends TimelineBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelines);

		Instance instance = InstanceHolder.getSingleton().get(0);
		setCurrentInstance(instance);

		ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager_tab);
		viewPager.setAdapter(new TimelineFragmentPagerAdapter(this, getSupportFragmentManager(), instance));
		viewPager.setOffscreenPageLimit(Attachment.Type.values().length);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tabs);
		tabLayout.setupWithViewPager(viewPager);
	}
}
