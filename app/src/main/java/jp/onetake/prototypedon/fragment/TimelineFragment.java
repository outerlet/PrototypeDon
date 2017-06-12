package jp.onetake.prototypedon.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

public class TimelineFragment extends BaseFragment
		implements ApiExecuteThread.ApiResultListener, StatusLinkMovementMethod.LinkClickListener {
	public interface TimelineEventListener {
		void onLinkClick(Uri uri);
	}

	private enum Trigger {
		Initial,
		Refresh,
		Add,
	}

	private static final String KEY_INSTANCE	= "TimelineFragment.KEY_INSTANCE";
	private static final String KEY_TYPE		= "TimelineFragment.KEY_TYPE";
	private static final String KEY_HASHTAG 	= "TimelineFragment.KEY_HASHTAG";
	private static final String KEY_API_ID		= "TimelineFragment.KEY_API_ID";

	public static TimelineFragment newInstance(Instance instance, TimelineType type) {
		Bundle params = new Bundle();
		params.putParcelable(KEY_INSTANCE, instance);
		params.putString(KEY_TYPE, type.toString());

		TimelineFragment fragment = new TimelineFragment();
		fragment.setArguments(params);
		return fragment;
	}

	public static TimelineFragment newInstance(Instance instance, String hashTag, int apiId) {
		Bundle params = new Bundle();
		params.putParcelable(KEY_INSTANCE, instance);
		params.putString(KEY_HASHTAG, hashTag);
		params.putInt(KEY_API_ID, apiId);

		TimelineFragment fragment = new TimelineFragment();
		fragment.setArguments(params);
		return fragment;
	}

	private SwipeRefreshLayout mRefreshLayout;
	private ListView mListView;
	private View mProgressView;
	private Instance mInstance;
	private TimelineType mType;
	private String mHashTag;
	private int mApiId;
	private Trigger mTrigger;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_timeline, container, false);

		mRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_refresh);
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				launchRequest(Trigger.Refresh);
			}
		});

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
		((TimelineAdapter)mListView.getAdapter()).setInstance(mInstance);

		String textType = getArguments().getString(KEY_TYPE);
		if (!TextUtils.isEmpty(textType)) {
			mType = TimelineType.getByString(textType);
		} else {
			mHashTag = getArguments().getString(KEY_HASHTAG);
			mApiId = getArguments().getInt(KEY_API_ID);
		}

		launchRequest(Trigger.Initial);
	}

	private void launchRequest(Trigger trigger) {
		TimelinesRequest request;

		if (mType != null) {
			request = new TimelinesRequest(getContext(), mType);
		} else {
			request = new TimelinesRequest(getContext(), mHashTag, mApiId);
		}

		ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
		thread.setListener(this);
		thread.start();

		showProgress(trigger);
	}

	@Override
	public void onApiSuccess(int apiId, ApiResponse response) {
		TimelinesResponse res = (TimelinesResponse)response;

		TimelineAdapter adapter = (TimelineAdapter)mListView.getAdapter();

		if (mTrigger == Trigger.Refresh) {
			adapter.clear();
		}

		adapter.add(res.statusList);
		adapter.notifyDataSetChanged();

		dismissProgress();
	}

	@Override
	public void onApiFailure(int apiId, ApiException exception) {
		dismissProgress();

		Toast.makeText(getContext(), R.string.message_get_timeline_error, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(TextView textView, Uri uri) {
		if (getActivity() instanceof TimelineEventListener) {
			((TimelineEventListener)getActivity()).onLinkClick(uri);
		}
	}

	private void showProgress(Trigger trigger) {
		switch (trigger) {
			case Initial:
				mProgressView.setVisibility(View.VISIBLE);
				break;
			case Add:
				// TODO:追加読み込みのために出すぐるぐるを表示する
				break;
		}

		mTrigger = trigger;
	}

	private void dismissProgress() {
		switch (mTrigger) {
			case Initial:
				mProgressView.setVisibility(View.INVISIBLE);
				break;
			case Refresh:
				mRefreshLayout.setRefreshing(false);
				break;
			case Add:
				// TODO:追加読み込みのために出すぐるぐるを消す
				break;
		}
	}
}
