package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class RegisterClientRequest extends ApiRequest {
	public RegisterClientRequest(Context context, int apiId) {
		super(context, apiId);

		addParameter("client_name", context.getString(R.string.app_name));
		addParameter("redirect_uris", context.getString(R.string.api_auth_redirect_uri));
		addParameter("scopes", context.getString(R.string.api_auth_scope));
	}

	@Override
	public String getPath() {
		return getContext().getString(R.string.api_path_register_client);
	}

	@Override
	public Method getMethod() {
		return Method.Post;
	}

	@Override
	public ApiResponse createResponse() {
		return new RegisterClientResponse();
	}
}
