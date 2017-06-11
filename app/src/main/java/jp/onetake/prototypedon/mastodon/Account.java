package jp.onetake.prototypedon.mastodon;

import org.json.JSONException;
import org.json.JSONObject;

public class Account {
	public int id;
	public String username;
	public String acct;
	public String displayName;
	public String locked;
	public String createdAt;
	public int followersCount;
	public int followingCount;
	public int statusesCount;
	public String note;
	public String url;
	public String avatar;
	public String avatarStatic;
	public String header;
	public String headerStatic;

	public Account(JSONObject json) throws JSONException {
		if (json != null) {
			id = json.optInt("id");
			username = json.optString("username");
			acct = json.optString("acct");
			displayName = json.optString("display_name");
			locked = json.optString("locked");
			createdAt = json.optString("created_at");
			followersCount = json.optInt("followers_count");
			followingCount = json.optInt("following_count");
			statusesCount = json.optInt("statuses_count");
			note = json.optString("note");
			url = json.optString("url");
			avatar = json.optString("avatar");
			avatarStatic = json.optString("avatar_static");
			header = json.optString("header");
			headerStatic = json.optString("header_static");
		}
	}
}
