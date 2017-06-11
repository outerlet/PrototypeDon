package jp.onetake.prototypedon.mastodon;

import jp.onetake.prototypedon.R;

public enum TimelineType {
	Home(R.integer.api_id_home_timeline, R.string.api_path_timelines_home, R.string.title_timeline_home),
	Local(R.integer.api_id_local_timeline, R.string.api_path_timelines_public, R.string.title_timeline_local),
	Public(R.integer.api_id_public_timeline, R.string.api_path_timelines_public, R.string.title_timeline_public);

	private final int mApiId;
	private final int mUrlResId;
	private final int mTitleResId;

	TimelineType(int apiId, int resId, int titleResId) {
		mApiId = apiId;
		mUrlResId = resId;
		mTitleResId = titleResId;
	}

	public int getUrlResourceId() {
		return mUrlResId;
	}

	public int getApiId() {
		return mApiId;
	}

	public int getTitleResourceId() {
		return mTitleResId;
	}

	public static TimelineType getByString(String string) {
		for (TimelineType type : values()) {
			if (type.toString().equals(string)) {
				return type;
			}
		}
		return null;
	}
}
