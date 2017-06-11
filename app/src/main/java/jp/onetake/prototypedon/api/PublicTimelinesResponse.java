package jp.onetake.prototypedon.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.prototypedon.status.Status;

public class PublicTimelinesResponse extends ApiResponse {
	public List<Status> statusList;

	public PublicTimelinesResponse() {
		statusList = new ArrayList<>();
	}

	@Override
	public void parse(String jsonText) throws ApiException {
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
