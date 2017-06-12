package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

public class RegisterClientRequest extends ApiRequest {
	public String client_name;
	public String redirect_uris;
	public String scopes;

	public RegisterClientRequest(Context context, int apiId) {
		super(context, apiId);

		client_name = context.getString(R.string.app_name);
		redirect_uris = context.getString(R.string.api_uri_register_client);
		scopes = context.getString(R.string.api_scope_register_client);
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
