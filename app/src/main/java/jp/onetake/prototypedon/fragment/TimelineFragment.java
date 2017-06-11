package jp.onetake.prototypedon.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.TimelinesRequest;
import jp.onetake.prototypedon.api.TimelinesResponse;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.TimelineType;
import jp.onetake.prototypedon.text.StatusLinkMovementMethod;
import jp.onetake.prototypedon.widget.TimelineAdapter;

public class TimelineFragment extends BasicFragment implements ApiExecuteThread.ApiResultListener, StatusLinkMovementMethod.LinkClickListener {
	public interface TimelineEventListener {
		void onLinkClick(Uri uri);
	}

	private static final String KEY_INSTANCE	= "TimelineFragment.KEY_INSTANCE";
	private static final String KEY_TYPE		= "TimelineFragment.KEY_TYPE";

	public static TimelineFragment newInstance(Instance instance, TimelineType type) {
		Bundle args = new Bundle();
		args.putParcelable(KEY_INSTANCE, instance);
		args.putString(KEY_TYPE, type.toString());

		TimelineFragment fragment = new TimelineFragment();
		fragment.setArguments(args);
		return fragment;
	}

	private ListView mListView;
	private View mProgressView;
	private Instance mInstance;
	private TimelineType mType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_timeline, container, false);

		StatusLinkMovementMethod method = new StatusLinkMovementMethod();
		method.setListener(this);

		mListView = (ListView)view.findViewById(R.id.listview_timeline);
		mListView.setAdapter(new TimelineAdapter(getContext(), method));

		mProgressView = view.findViewById(R.id.layout_progress);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mInstance = getArguments().getParcelable(KEY_INSTANCE);

		mType = TimelineType.getByString(getArguments().getString(KEY_TYPE));
		TimelinesRequest request = new TimelinesRequest(getContext(), mType);

		mProgressView.setVisibility(View.VISIBLE);

		ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
		thread.setListener(this);
		thread.start();
	}

	@Override
	public void onApiSuccess(int identifier, ApiResponse response) {
		TimelinesResponse res = (TimelinesResponse)response;

		TimelineAdapter adapter = (TimelineAdapter)mListView.getAdapter();
		adapter.add(res.statusList);
		adapter.notifyDataSetChanged();

		mProgressView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onApiFailure(int identifier, ApiException exception) {
		if (identifier == getResources().getInteger(R.integer.api_id_public_timeline)) {
			mProgressView.setVisibility(View.INVISIBLE);

			Toast.makeText(getContext(), R.string.message_get_timeline_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(TextView textView, Uri uri) {
		if (getActivity() instanceof TimelineEventListener) {
			((TimelineEventListener)getActivity()).onLinkClick(uri);
		}
	}
}
