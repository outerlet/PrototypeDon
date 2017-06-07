package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class VerifyCredentialsRequest extends ApiRequest {
	public VerifyCredentialsRequest(Context context, int identifier) {
		super(context, identifier);
	}

	@Override
	public String getPath() {
		return getContext().getString(R.string.api_path_verify_credentials);
	}

	@Override
	public Method getMethod() {
		return Method.Get;
	}

	@Override
	public ApiResponse createResponse() {
		return new VerifyCredentialsResponse();
	}
}
