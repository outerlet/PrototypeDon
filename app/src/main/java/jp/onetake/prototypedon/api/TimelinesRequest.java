package jp.onetake.prototypedon.api;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.TimelineType;

public class TimelinesRequest extends ApiRequest {
	private TimelineType mType;
	private String mHashTag;
	private long mMaxId;

	public TimelinesRequest(Context context, TimelineType type) {
		super(context, type.getApiId());

		mType = type;
		mHashTag = null;
		mMaxId = -1;
	}

	public TimelinesRequest(Context context, String hashTag, int apiId) {
		super(context, apiId);

		mType = null;
		mHashTag = hashTag;
		mMaxId = -1;
	}

	public void setMaxId(long maxId) {
		mMaxId = maxId;
	}

	@Override
	public String getPath() {
		StringBuilder path;
		boolean hasParam = false;

		// ハッシュタグ以外(ホーム、ローカル、連合)
		if (mType != null) {
			path = new StringBuilder(getContext().getString(mType.getUrlResourceId()));

			// ローカルの場合は連合用のリクエストにクエリパラメータをつける
			if (mType == TimelineType.Local) {
				path.append("?local=true");
				hasParam = true;
			}
		// ハッシュタグ
		} else {
			path = new StringBuilder(getContext().getString(R.string.api_path_timelines_hashtag));
			try {
				String hashTag = URLEncoder.encode(mHashTag, "UTF-8");
				path.append("/").append(hashTag);
			} catch (UnsupportedEncodingException ignore) {
				path.append("/").append(mHashTag);
			}
		}

		if (mMaxId != -1) {
			String query = String.format(Locale.US, "max_id=%1$s", Long.toString(mMaxId));
			path.append(hasParam ? "&" : "?").append(query);
		}

		return path.toString();
	}

	@Override
	public Method getMethod() {
		return Method.Get;
	}

	@Override
	public ApiResponse createResponse() {
		return new TimelinesResponse();
	}
}
