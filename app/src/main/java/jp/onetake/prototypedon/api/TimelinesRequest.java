package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.mastodon.TimelineType;

public class TimelinesRequest extends ApiRequest {
	private TimelineType mType;

	public TimelinesRequest(Context context, TimelineType type) {
		super(context, type.getApiId());

		mType = type;
	}

	@Override
	public String getPath() {
		String textURL = getContext().getString(mType.getUrlResourceId());

		if (mType == TimelineType.Local) {
			return textURL + "?local=true";
		}

		return textURL;
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
