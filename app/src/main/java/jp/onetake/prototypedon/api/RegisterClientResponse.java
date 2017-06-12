package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterClientResponse extends ApiResponse {
	public String id;
	public String clientId;
	public String clientSecret;

	@Override
	public void parse(String jsonText) throws ApiException {
		super.parse(jsonText);

		try {
			JSONObject json = new JSONObject(jsonText);

			id = json.getString("id");
			clientId = json.getString("client_id");
			clientSecret = json.getString("client_secret");
		} catch (JSONException jse) {
			throw new ApiException(jse);
		}
	}
}
