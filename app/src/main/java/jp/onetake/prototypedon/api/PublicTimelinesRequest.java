package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class PublicTimelinesRequest extends ApiRequest {
	private boolean mIsLocal;

	public PublicTimelinesRequest(Context context, int identifier, boolean isLocal) {
		super(context, identifier);

		mIsLocal = isLocal;
	}

	@Override
	public String getPath() {
		String textURL = getContext().getString(R.string.api_path_timelines_public);

		if (mIsLocal) {
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
		return new PublicTimelinesResponse();
	}
}
