package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApiResponse {
	public abstract void parse(JSONObject json) throws JSONException;
}
