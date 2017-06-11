package jp.onetake.prototypedon.status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.prototypedon.util.DebugLog;

public class Status {
	public Account account;
	public Status reblog;
	public List<Attachment> mediaAttachments;
	public List<Mention> mentions;
	public List<Tag> tags;
	public Application application;

	public String id;
	public String uri;
	public String url;
	public String inReplyToId;
	public String inReplyToAccountId;
	public String content;
	public String createdAt;
	public String reblogsCount;
	public String favouritesCount;
	public String reblogged;
	public String favourited;
	public String sensitive;
	public String spoilerText;
	public String visibility;
	public String language;

	public Status(JSONObject json) throws JSONException {
		DebugLog.debug(getClass(), json.toString());

		id = json.optString("id");
		uri = json.optString("uri");
		url = json.optString("url");
		inReplyToId = json.optString("in_reply_to_id");
		inReplyToAccountId = json.optString("in_reply_to_account_id");
		content = json.optString("content");
		createdAt = json.optString("created_at");
		reblogsCount = json.optString("reblogs_count");
		favouritesCount = json.optString("favourites_count");
		reblogged = json.optString("reblogged");
		favourited = json.optString("favourited");
		sensitive = json.optString("sensitive");
		spoilerText = json.optString("spoiler_text");
		visibility = json.optString("visibility");
		language = json.optString("language");

		JSONObject objAccount = json.optJSONObject("account");
		if (objAccount != null) {
			account = new Account(objAccount);
		}

		JSONObject objReblog = json.optJSONObject("reblog");
		if (objReblog != null) {
			reblog = new Status(objReblog);
		}

		JSONArray aryAttachments = json.optJSONArray("media_attachments");
		if (aryAttachments != null) {
			mediaAttachments = new ArrayList<>();
			for (int i = 0; i < aryAttachments.length(); i++) {
				mediaAttachments.add(new Attachment(aryAttachments.getJSONObject(i)));
			}
		}

		JSONArray aryMentions = json.getJSONArray("mentions");
		if (aryMentions != null) {
			mentions = new ArrayList<>();
			for (int i = 0; i < aryMentions.length(); i++) {
				mentions.add(new Mention(aryMentions.getJSONObject(i)));
			}
		}

		JSONArray aryTags = json.getJSONArray("tags");
		if (aryTags != null) {
			tags = new ArrayList<>();
			for (int i = 0; i < aryTags.length(); i++) {
				tags.add(new Tag(aryTags.getJSONObject(i)));
			}
		}

		application = new Application(json.optJSONObject("application"));
	}
}
