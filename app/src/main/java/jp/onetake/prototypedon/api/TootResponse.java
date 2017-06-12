package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

import jp.onetake.prototypedon.mastodon.Status;

public class TootResponse extends ApiResponse {
	public Status status;

	@Override
	public void parse(String jsonText) throws ApiException {
		super.parse(jsonText);

		try {
			status = new Status(new JSONObject(jsonText));
		} catch (JSONException jse) {
			throw new ApiException(jse);
		}
	}
}
