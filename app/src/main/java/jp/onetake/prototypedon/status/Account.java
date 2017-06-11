package jp.onetake.prototypedon.status;

import org.json.JSONException;
import org.json.JSONObject;

public class Account {
	public String id;
	public String username;
	public String acct;
	public String displayName;
	public String locked;
	public String createdAt;
	public String followersCount;
	public String followingCount;
	public String statusesCount;
	public String note;
	public String url;
	public String avatar;
	public String avatarStatic;
	public String header;
	public String headerStatic;

	public Account(JSONObject json) throws JSONException {
		if (json != null) {
			id = json.optString("id");
			username = json.optString("username");
			acct = json.optString("acct");
			displayName = json.optString("display_name");
			locked = json.optString("locked");
			createdAt = json.optString("created_at");
			followersCount = json.optString("followers_count");
			followingCount = json.optString("following_count");
			statusesCount = json.optString("statuses_count");
			note = json.optString("note");
			url = json.optString("url");
			avatar = json.optString("avatar");
			avatarStatic = json.optString("avatar_static");
			header = json.optString("header");
			headerStatic = json.optString("header_static");
		}
	}
}
