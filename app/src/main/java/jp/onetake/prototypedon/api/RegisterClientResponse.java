package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterClientResponse extends ApiResponse {
	public String id;
	public String clientId;
	public String clientSecret;

	@Override
	public void parse(JSONObject json) throws JSONException {
		id = json.getString("id");
		clientId = json.getString("client_id");
		clientSecret = json.getString("client_secret");
	}
}
