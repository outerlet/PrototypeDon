package jp.onetake.prototypedon.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelinesActivity extends TimelineBaseActivity implements ListView.OnItemClickListener {
	private DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelines);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_timeline);
		toolbar.setNavigationIcon(R.mipmap.ic_menu_white);

		mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_container);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
		mDrawerLayout.addDrawerListener(toggle);

		ListView listView = (ListView) mDrawerLayout.findViewById(R.id.listview_drawer);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
		adapter.add("Selection1");
		adapter.add("Selection2");
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		Instance instance = InstanceHolder.getSingleton().get(0);
		setCurrentInstance(instance);

		ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager_tab);
		viewPager.setAdapter(new TimelineFragmentPagerAdapter(this, getSupportFragmentManager(), instance));
		viewPager.setOffscreenPageLimit(Attachment.Type.values().length);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tabs);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DebugLog.debug(getClass(), "position = " + position);
	}

	@Override
	public void onLoadFailure(ApiException exception) {
		Snackbar.make(findViewById(R.id.layout_container), R.string.message_get_timeline_error, Snackbar.LENGTH_SHORT).show();
	}
}
