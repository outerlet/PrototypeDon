package jp.onetake.prototypedon.status;

import org.json.JSONException;
import org.json.JSONObject;

public class Attachment {
	public enum Type {
		Image,
		Video,
		Gifv;

		public static Type getByString(String text) {
			for (Type type : values()) {
				if (type.toString().equalsIgnoreCase(text)) {
					return type;
				}
			}
			return null;
		}
	}

	public String id;
	public Type type;
	public String url;
	public String remoteUrl;
	public String previewUrl;
	public String textUrl;
	public String meta;

	public Attachment(JSONObject json) throws JSONException {
		if (json != null) {
			id = json.optString("id");
			type = Type.getByString(json.optString("type"));
			url = json.optString("url");
			remoteUrl = json.optString("remote_url");
			previewUrl = json.optString("preview_url");
			textUrl = json.optString("text_url");
			meta = json.optString("meta");
		}
	}
}
