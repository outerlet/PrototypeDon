package jp.onetake.prototypedon.status;

import org.json.JSONException;
import org.json.JSONObject;

public class Application {
	public String name;
	public String website;

	public Application(JSONObject json) throws JSONException {
		if (json != null) {
			name = json.optString("name");
			website = json.optString("website");
		}
	}
}
