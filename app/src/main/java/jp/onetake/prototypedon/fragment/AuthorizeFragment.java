package jp.onetake.prototypedon.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.activity.OAuthActivity;
import jp.onetake.prototypedon.api.AccessTokenRequest;
import jp.onetake.prototypedon.api.AccessTokenResponse;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.RegisterClientRequest;
import jp.onetake.prototypedon.api.RegisterClientResponse;
import jp.onetake.prototypedon.fragment.dialog.AlertDialogFragment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;

public class AuthorizeFragment extends BaseFragment
		implements View.OnClickListener, ApiExecuteThread.ApiResultListener {
	public static final String DIALOG_TAG_SUCCESS				= "AuthorizeFragment.DIALOG_TAG_SUCCESS";
	public static final String DIALOG_TAG_SAVE_ERROR			= "AuthorizeFragment.DIALOG_TAG_SAVE_ERROR";
	public static final String DIALOG_TAG_ALREADY_REGISTERED	= "AuthorizeFragment.DIALOG_TAG_ALREADY_REGISTERED";

	private static final int REQUEST_CODE_OAUTH	= 10001;

	private EditText mHostNameView;
	private View mProgressView;
	private Instance mInstance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_authorize, container, false);

		mHostNameView = (EditText)view.findViewById(R.id.edittext_host_name);
		mProgressView = view.findViewById(R.id.layout_progress);

		view.findViewById(R.id.button_execute).setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_OAUTH) {
			if (resultCode == Activity.RESULT_OK) {
				String authCode = data.getStringExtra(OAuthActivity.KEY_AUTH_CODE);

				DebugLog.debug(getClass(), "Authorization-Code = " + authCode);

				AccessTokenRequest request =
						new AccessTokenRequest(getActivity(), getResources().getInteger(R.integer.api_id_access_token));
				request.addParameter("client_id", mInstance.getClientId());
				request.addParameter("client_secret", mInstance.getClientSecret());
				request.addParameter("code", authCode);

				ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
				thread.setListener(this);
				thread.start();
			} else {
				mProgressView.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View view) {
		String hostName = mHostNameView.getText().toString();

		InstanceHolder holder = InstanceHolder.getSingleton();
		for (int i = 0; i < holder.size(); i++) {
			if (holder.get(i).getHostName().equals(hostName)) {
				AlertDialogFragment dialog = AlertDialogFragment.newInstance(
						getString(R.string.message_instance_already_registered), getString(R.string.phrase_ok));
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_TAG_ALREADY_REGISTERED);

				return;
			}
		}

		mInstance = new Instance();
		mInstance.setHostName(hostName);

		RegisterClientRequest request = new RegisterClientRequest(
				getActivity(), getResources().getInteger(R.integer.api_id_register_client));

		mProgressView.setVisibility(View.VISIBLE);

		ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
		thread.setListener(this);
		thread.start();
	}

	@Override
	public void onApiSuccess(int apiId, ApiResponse response) {
		if (apiId == getResources().getInteger(R.integer.api_id_register_client)) {
			RegisterClientResponse res = (RegisterClientResponse) response;

			mInstance.setClientId(res.clientId);
			mInstance.setClientSecret(res.clientSecret);

			startActivityForResult(
					OAuthActivity.createLaunchIntent(getActivity(), mInstance), REQUEST_CODE_OAUTH);
		} else if (apiId == getResources().getInteger(R.integer.api_id_access_token)) {
			AccessTokenResponse res = (AccessTokenResponse)response;

			mInstance.setAccessToken(res.accessToken);

			DebugLog.debug(getClass(), "Access-Token = " + res.accessToken);

			try {
				InstanceHolder holder = InstanceHolder.getSingleton();
				holder.add(mInstance);
				holder.save(getActivity().getApplicationContext());

				showDialog(R.string.phrase_confirmation, R.string.message_instance_saved, DIALOG_TAG_SUCCESS);
			} catch (IOException ioe) {
				ioe.printStackTrace();

				showDialog(R.string.phrase_error, R.string.message_instance_save_error, DIALOG_TAG_SAVE_ERROR);
			}

			mProgressView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onApiFailure(int apiId, ApiException exception) {
		if (exception.getCause() != null) {
			exception.getCause().printStackTrace();
		} else {
			android.util.Log.e(getClass().getSimpleName(), exception.getMessage());
		}

		showDialog(R.string.phrase_error, R.string.message_instance_save_error, DIALOG_TAG_SAVE_ERROR);
	}
}
