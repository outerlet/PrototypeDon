package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class AccessTokenRequest extends ApiRequest {
	public AccessTokenRequest(Context context, int apiId) {
		super(context, apiId);

		addParameter("grant_type", context.getString(R.string.api_auth_authorization_code));
		addParameter("redirect_uri", context.getString(R.string.api_auth_redirect_uri));
	}

	@Override
	public String getPath() {
		return getContext().getString(R.string.api_path_access_token);
	}

	@Override
	public Method getMethod() {
		return Method.Post;
	}

	@Override
	public ApiResponse createResponse() {
		return new AccessTokenResponse();
	}
}
