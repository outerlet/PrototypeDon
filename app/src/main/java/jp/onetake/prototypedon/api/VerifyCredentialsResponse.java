package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyCredentialsResponse extends ApiResponse {
	public String id;
	public String username;
	public String acct;
	public String display_name;
	public String locked;
	public String created_at;
	public String followers_count;
	public String following_count;
	public String statuses_count;
	public String note;
	public String url;
	public String avatar;
	public String avatar_static;
	public String header;
	public String header_static;

	@Override
	public void parse(JSONObject json) throws JSONException {
		id = json.getString("id");
		username = json.getString("username");
		acct = json.getString("acct");
		display_name = json.getString("display_name");
		locked = json.getString("locked");
		created_at = json.getString("created_at");
		followers_count = json.getString("followers_count");
		following_count = json.getString("following_count");
		statuses_count = json.getString("statuses_count");
		note = json.getString("note");
		url = json.getString("url");
		avatar = json.getString("avatar");
		avatar_static = json.getString("avatar_static");
		header = json.getString("header");
		header_static = json.getString("header_static");
	}
}
