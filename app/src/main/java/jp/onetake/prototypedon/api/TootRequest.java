package jp.onetake.prototypedon.api;

import android.content.Context;

import jp.onetake.prototypedon.R;

/**
 * とりあえずテキストだけトゥート
 * TODO:画像のトゥートもできないし、CWとかNSFWなどにも対応してないので後々対応すること
 */
public class TootRequest extends ApiRequest {
	public TootRequest(Context context, int apiId) {
		super(context, apiId);
	}

	@Override
	public String getPath() {
		return getContext().getString(R.string.api_path_toot);
	}

	@Override
	public Method getMethod() {
		return Method.Post;
	}

	@Override
	public ApiResponse createResponse() {
		return new TootResponse();
	}
}
