package jp.onetake.prototypedon.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.fragment.TimelineFragment;
import jp.onetake.prototypedon.mastodon.Attachment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;
import jp.onetake.prototypedon.widget.TimelineFragmentPagerAdapter;

public class TimelineBaseActivity extends BaseActivity implements TimelineFragment.TimelineEventListener {
	private Instance mCurrentInstance;

	@Override
	public void onLinkClick(Uri uri) {
		DebugLog.debug(getClass(), "URI = " + uri.toString());

		String path = uri.getPath();

		// タグの判定
		if (path.startsWith("/tags/")) {
			String hashTag = path.substring(6);
			Intent intent = SingleTimelineActivity.newLauncherIntent(this, getCurrentInstance(), "#" + hashTag, hashTag);
			startActivity(intent);
			return;
		}

		// ユーザー名の判定
		if (path.startsWith("/@")) {
			String userName = path.substring(2);
			DebugLog.debug(getClass(), "username = " + userName);
			return;
		}

		// 通常のURLであれば、そのままChromeCustomTabでページを開く
		CustomTabsIntent intent = new CustomTabsIntent.Builder()
				.setShowTitle(true)
				.setToolbarColor(ContextCompat.getColor(this, R.color.primary))
				.addDefaultShareMenuItem()
				.build();

		intent.launchUrl(this, uri);
	}

	protected void setCurrentInstance(Instance currentInstance) {
		mCurrentInstance = currentInstance;
	}

	protected Instance getCurrentInstance() {
		return mCurrentInstance;
	}
}
