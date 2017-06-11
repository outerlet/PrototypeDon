package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponse {
	public void parse(String jsonText) throws ApiException {
		try {
			// エラーの場合、"error"だけが入ったJSONObjectが返ってくるはず
			// JSONExceptionが発生した場合、statusなどそもそものJSON文字列がObjectではなくArrayだった場合など
			JSONObject json = new JSONObject(jsonText);
			if (json.has("error")) {
				throw new ApiException(json.getString("error"));
			}
		} catch (JSONException ignore) {}
	}
}
