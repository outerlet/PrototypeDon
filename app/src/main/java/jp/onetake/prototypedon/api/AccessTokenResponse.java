package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class AccessTokenResponse extends ApiResponse {
	public String accessToken;

	@Override
	public void parse(JSONObject json) throws JSONException {
		accessToken = json.getString("access_token");
	}
}
