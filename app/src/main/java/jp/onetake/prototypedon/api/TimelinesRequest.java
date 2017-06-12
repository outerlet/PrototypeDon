package jp.onetake.prototypedon.api;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.TimelineType;

public class TimelinesRequest extends ApiRequest {
	private TimelineType mType;
	private String mHashTag;

	public TimelinesRequest(Context context, TimelineType type) {
		super(context, type.getApiId());

		mType = type;
		mHashTag = null;
	}

	public TimelinesRequest(Context context, String hashTag, int apiId) {
		super(context, apiId);

		mType = null;
		mHashTag = hashTag;
	}

	@Override
	public String getPath() {
		String textURL;

		if (mType != null) {
			textURL = getContext().getString(mType.getUrlResourceId());

			if (mType == TimelineType.Local) {
				return textURL + "?local=true";
			}

			return textURL;
		}

		String hashTag;

		try {
			hashTag = URLEncoder.encode(mHashTag, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
			hashTag = mHashTag;
		}

		return getContext().getString(R.string.api_path_timelines_hashtag) + "/" + hashTag;
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
