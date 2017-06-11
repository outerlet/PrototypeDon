package jp.onetake.prototypedon.status;

import org.json.JSONException;
import org.json.JSONObject;

public class Tag {
	public String name;
	public String url;

	public Tag(JSONObject json) throws JSONException {
		if (json != null) {
			name = json.getString("name");
			url = json.getString("url");
		}
	}
}
