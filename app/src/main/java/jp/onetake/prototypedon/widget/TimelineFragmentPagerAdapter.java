package jp.onetake.prototypedon.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.prototypedon.fragment.TimelineFragment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.TimelineType;

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
	private class Page {
		TimelineFragment fragment;
		String title;
	}

	private List<Page> mPageList;

	public TimelineFragmentPagerAdapter(Context context, FragmentManager manager, Instance instance) {
		super(manager);

		mPageList = new ArrayList<>();

		for (TimelineType type : TimelineType.values()) {
			Page page = new Page();
			page.fragment = TimelineFragment.newInstance(instance, type);
			page.title = context.getString(type.getTitleResourceId());

			mPageList.add(page);
		}
	}

	@Override
	public Fragment getItem(int position) {
		return mPageList.get(position).fragment;
	}

	public void destroyAllItem(FragmentManager manager) {
		FragmentTransaction trans = manager.beginTransaction();

		for (Page page : mPageList) {
			trans.remove(page.fragment);
		}

		trans.commit();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return mPageList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mPageList.get(position).title;
	}
}
