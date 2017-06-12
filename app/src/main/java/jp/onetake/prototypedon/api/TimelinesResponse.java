package jp.onetake.prototypedon.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.prototypedon.mastodon.Status;

public class TimelinesResponse extends ApiResponse {
	public List<Status> statusList;

	public TimelinesResponse() {
		statusList = new ArrayList<>();
	}

	@Override
	public void parse(String jsonText) throws ApiException {
		super.parse(jsonText);

		try {
			JSONArray array = new JSONArray(jsonText);

			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				Status status = new Status(json);

				statusList.add(status);
			}
		} catch (JSONException jse) {
			throw new ApiException(jse);
		}
	}
}
