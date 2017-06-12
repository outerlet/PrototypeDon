package jp.onetake.prototypedon.api;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyCredentialsResponse extends ApiResponse {
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

	@Override
	public void parse(String jsonText) throws ApiException {
		super.parse(jsonText);

		try {
			JSONObject json = new JSONObject(jsonText);

			id = json.getString("id");
			username = json.getString("username");
			acct = json.getString("acct");
			displayName = json.getString("display_name");
			locked = json.getString("locked");
			createdAt = json.getString("created_at");
			followersCount = json.getString("followers_count");
			followingCount = json.getString("following_count");
			statusesCount = json.getString("statuses_count");
			note = json.getString("note");
			url = json.getString("url");
			avatar = json.getString("avatar");
			avatarStatic = json.getString("avatar_static");
			header = json.getString("header");
			headerStatic = json.getString("header_static");
		} catch (JSONException jse) {
			throw new ApiException(jse);
		}
	}
}
