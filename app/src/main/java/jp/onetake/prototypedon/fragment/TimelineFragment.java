package jp.onetake.prototypedon.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.TimelinesRequest;
import jp.onetake.prototypedon.api.TimelinesResponse;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.Status;
import jp.onetake.prototypedon.mastodon.TimelineType;
import jp.onetake.prototypedon.text.StatusLinkMovementMethod;
import jp.onetake.prototypedon.widget.TimelineAdapter;
import jp.onetake.prototypedon.widget.TimelineFooterView;

public class TimelineFragment extends BaseFragment
		implements ApiExecuteThread.ApiResultListener, StatusLinkMovementMethod.LinkClickListener {
	public interface TimelineEventListener {
		void onLinkClick(Uri uri);
		void onLoadFailure(ApiException exception);
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
	private TimelineFooterView mFooterView;
	private View mProgressView;
	private Instance mInstance;
	private TimelineType mType;
	private String mHashTag;
	private int mApiId;
	private Trigger mTrigger;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_timeline, container, false);

		mRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_refresh);
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				launchRequest(Trigger.Refresh);
			}
		});

		StatusLinkMovementMethod method = new StatusLinkMovementMethod();
		method.setListener(this);

		mFooterView = new TimelineFooterView(getContext());
		mFooterView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				launchRequest(Trigger.Add);
			}
		});

		mListView = (ListView)view.findViewById(R.id.listview_timeline);
		mListView.setAdapter(new TimelineAdapter(getContext(), method));
		mListView.addFooterView(mFooterView);

		mProgressView = view.findViewById(R.id.layout_progress);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mInstance = getArguments().getParcelable(KEY_INSTANCE);
		getAdapter().setInstance(mInstance);

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

		if (trigger == Trigger.Add) {
			TimelineAdapter adapter = getAdapter();

			Status status = adapter.getItem(adapter.getCount() - 1);
			if (status != null) {
				request.setMaxId(status.id);
			}
		}

		ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
		thread.setListener(this);
		thread.start();

		showProgress(trigger);
	}

	@Override
	public void onApiSuccess(int apiId, ApiResponse response) {
		TimelinesResponse res = (TimelinesResponse)response;

		TimelineAdapter adapter = getAdapter();

		if (mTrigger == Trigger.Refresh) {
			adapter.clear();
		}

		if (res.statusList.size() > 0) {
			adapter.add(res.statusList);
			adapter.notifyDataSetChanged();
		} else {
			mListView.removeFooterView(mFooterView);
		}

		dismissProgress();
	}

	@Override
	public void onApiFailure(int apiId, ApiException exception) {
		dismissProgress();

		if (getActivity() instanceof TimelineEventListener) {
			((TimelineEventListener)getActivity()).onLoadFailure(exception);
		}
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
				mFooterView.setProgress(true);
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
				mFooterView.setProgress(false);
				break;
		}
	}

	private TimelineAdapter getAdapter() {
		ListAdapter adapter = mListView.getAdapter();
		if (adapter instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter)adapter;
			return (TimelineAdapter)headerAdapter.getWrappedAdapter();
		}

		return (TimelineAdapter)adapter;
	}
}
