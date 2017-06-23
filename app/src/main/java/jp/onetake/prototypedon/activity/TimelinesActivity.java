package jp.onetake.prototypedon.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.TootRequest;
import jp.onetake.prototypedon.fragment.dialog.PromptDialogFragment;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.widget.InstanceAdapter;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelinesActivity extends TimelineBaseActivity
		implements View.OnClickListener, ListView.OnItemClickListener, PromptDialogFragment.PromptInputListener, ApiExecuteThread.ApiResultListener {
	private static final int REQCODE_ADD_INSTANCE = 10001;

	private static final String KEY_INSTANCE_INDEX	= "TimelinesActivity.KEY_INSTANCE_INDEX";
	private static final String TAG_DIALOG_TOOT		= "TimelinesActivity.TAG_DIALOG_TOOT";

	private DrawerLayout mDrawerLayout;
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

		Toolbar toolbar = getToolbar();

		mDrawerLayout = (DrawerLayout)findViewById(R.id.layout_navigation_drawer);

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

		toolbar.setNavigationIcon(R.mipmap.ic_menu_white);

		String title = getString(R.string.title_timeline) + " - " + instance.getHostName();
		toolbar.setTitle(title);

		ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager_tab);
		viewPager.setAdapter(new TimelineFragmentPagerAdapter(this, getSupportFragmentManager(), instance));
		viewPager.setOffscreenPageLimit(Attachment.Type.values().length);

		TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tabs);
		tabLayout.setupWithViewPager(viewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_timelines, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_toot) {
			PromptDialogFragment dialog = PromptDialogFragment.newInstance(
					getString(R.string.title_toot), getString(R.string.phrase_toot), getString(R.string.phrase_cancel), null);
			dialog.show(getSupportFragmentManager(), TAG_DIALOG_TOOT);

			return true;
		}

		return super.onOptionsItemSelected(item);
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
		showSnackbar(R.string.message_get_timeline_error);
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

	@Override
	public void onInput(DialogFragment dialog, String inputText) {
		TootRequest request = new TootRequest(this, getResources().getInteger(R.integer.api_id_toot));
		request.addParameter("status", inputText);

		ApiExecuteThread t = ApiExecuteThread.newInstance(getCurrentInstance(), request);
		t.setListener(this);
		t.start();
	}

	@Override
	public void onCancel(DialogFragment dialog, String inputText) {
		// キャンセルしたときトゥートのために入力した値を保持するなどで使い勝手がよくなるかも？
		// でもとりあえずは様子見で何もしない
	}

	@Override
	public void onApiSuccess(int apiId, ApiResponse response) {
		if (apiId == getResources().getInteger(R.integer.api_id_toot)) {
			showSnackbar(R.string.message_tooted);
		}
	}

	@Override
	public void onApiFailure(int apiId, ApiException exception) {
		if (apiId == getResources().getInteger(R.integer.api_id_toot)) {
			showSnackbar(R.string.message_toot_error);
		}
	}

	// Snackbarでメッセージを表示
	private void showSnackbar(int msgResId) {
		Snackbar.make(findViewById(R.id.layout_navigation_drawer), msgResId, Snackbar.LENGTH_SHORT).show();
	}
}
