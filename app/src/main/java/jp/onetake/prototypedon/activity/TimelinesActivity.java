package jp.onetake.prototypedon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.widget.InstanceAdapter;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelinesActivity extends TimelineBaseActivity implements View.OnClickListener, ListView.OnItemClickListener {
	private static final int REQCODE_ADD_INSTANCE = 10001;
	private static final String KEY_INSTANCE_INDEX	= "TimelinesActivity.KEY_INSTANCE_INDEX";

	private DrawerLayout mDrawerLayout;
	private ViewPager mViewPager;
	private ListView mInstanceListView;

	public static Intent createLaunchIntent(Context context, int instanceIndex) {
		Intent intent = new Intent(context, TimelinesActivity.class);
		intent.putExtra(KEY_INSTANCE_INDEX, instanceIndex);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelines);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.mipmap.ic_menu_white);

		mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_container);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
		mDrawerLayout.addDrawerListener(toggle);

		mInstanceListView = (ListView)findViewById(R.id.listview_instances);
		InstanceAdapter adapter = new InstanceAdapter(this);
		adapter.updateAndNotify();
		mInstanceListView.setAdapter(adapter);
		mInstanceListView.setOnItemClickListener(this);

		findViewById(R.id.layout_add_instance).setOnClickListener(this);

		int instanceIndex = getIntent().getIntExtra(KEY_INSTANCE_INDEX, 0);
		Instance instance = InstanceHolder.getSingleton().get(instanceIndex);
		setCurrentInstance(instance);

		String title = getString(R.string.title_timeline) + " - " + instance.getHostName();
		toolbar.setTitle(title);

		mViewPager = (ViewPager)findViewById(R.id.viewpager_tab);
		mViewPager.setAdapter(new TimelineFragmentPagerAdapter(this, getSupportFragmentManager(), instance));
		mViewPager.setOffscreenPageLimit(Attachment.Type.values().length);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tabs);
		tabLayout.setupWithViewPager(mViewPager);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQCODE_ADD_INSTANCE && resultCode == RESULT_OK) {
			InstanceAdapter adapter = (InstanceAdapter)mInstanceListView.getAdapter();
			adapter.updateAndNotify();
		}
	}

	@Override
	public void onLoadFailure(ApiException exception) {
		Snackbar.make(findViewById(R.id.layout_container), R.string.message_get_timeline_error, Snackbar.LENGTH_SHORT).show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		InstanceAdapter adapter = (InstanceAdapter)mInstanceListView.getAdapter();

		Instance instance = adapter.getItem(position);
		if (instance != null && !instance.getHostName().equals(getCurrentInstance().getHostName())) {
			// インスタンスを変更するためにアクティビティを一旦破棄して再作成
			// PagerAdapterを使ってうまくやるには時間がかかりそうなので、進捗を優先してとりあえずこの実装
			// FIXME:できればFragmentPagerAdapterをクリアしてデータを入れ直したい
			startActivity(createLaunchIntent(this, position));
			overridePendingTransition(0, 0);

			finish();

			return;
		}

		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		}
	}

	@Override
	public void onClick(View v) {
		startActivityForResult(
				new Intent(this, AddInstanceActivity.class), REQCODE_ADD_INSTANCE);
	}
}
