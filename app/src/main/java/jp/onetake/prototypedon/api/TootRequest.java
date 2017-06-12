package jp.onetake.prototypedon.api;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.onetake.prototypedon.R;

public class TootRequest extends ApiRequest {
	// とりあえずテキストだけトゥート
	// TODO:画像のトゥートもできないし、CWとかNSFWなどにも対応してないので後々対応すること
	public String status;

	public TootRequest(Context context, int apiId) {
		super(context, apiId);
	}

	public void setEncodedStatus(String status) {
		try {
			this.status = URLEncoder.encode(status, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			this.status = status;
		}
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
