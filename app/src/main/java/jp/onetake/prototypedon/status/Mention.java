package jp.onetake.prototypedon.status;

import org.json.JSONException;
import org.json.JSONObject;

public class Mention {
	public String url;
	public String username;
	public String acct;
	public String id;

	public Mention(JSONObject json) throws JSONException {
		if (json != null) {
			url = json.optString("url");
			username = json.optString("username");
			acct = json.optString("acct");
			id = json.optString("id");
		}
	}
}
