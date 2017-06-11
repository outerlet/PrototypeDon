package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class AccessTokenResponse extends ApiResponse {
	public String accessToken;

	@Override
	public void parse(String jsonText) throws ApiException {
		try {
			accessToken = new JSONObject(jsonText).getString("access_token");
		} catch (JSONException jse) {
			throw new ApiException(jse);
		}
	}
}
