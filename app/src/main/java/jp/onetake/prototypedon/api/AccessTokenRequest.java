package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class AccessTokenRequest extends ApiRequest {
	public String client_id;
	public String client_secret;
	public String grant_type;
	public String username;
	public String password;
	public String scope;

	public AccessTokenRequest(Context context, int identifier) {
		super(context, identifier);
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
