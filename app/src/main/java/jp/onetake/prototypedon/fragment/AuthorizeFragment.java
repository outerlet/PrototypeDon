package jp.onetake.prototypedon.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.AccessTokenRequest;
import jp.onetake.prototypedon.api.AccessTokenResponse;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.ApiThread;
import jp.onetake.prototypedon.api.RegisterClientRequest;
import jp.onetake.prototypedon.api.RegisterClientResponse;
import jp.onetake.prototypedon.common.MastodonInstance;
import jp.onetake.prototypedon.common.MastodonInstanceHolder;
import jp.onetake.prototypedon.fragment.dialog.AlertDialogFragment;
import jp.onetake.prototypedon.util.DebugLog;

public class AuthorizeFragment extends Fragment implements View.OnClickListener, ApiThread.ApiResultListener {
	public static final String DIALOG_TAG_SUCCESS		= "AuthorizeFragment.DIALOG_TAG_SUCCESS";
	public static final String DIALOG_TAG_SAVE_ERROR	= "AuthorizeFragment.DIALOG_TAG_SAVE_ERROR";

	private static final int API_ID_REGISTER_CLIENT	= 10001;
	private static final int API_ID_ACCESS_TOKEN	= 10002;

	private EditText mHostNameView;
	private EditText mMailAddressView;
	private EditText mPasswordView;
	private View mProgressView;
	private MastodonInstance mInstance;

	public AuthorizeFragment() {
	}

	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_authorize, container, false);

		mHostNameView = (EditText)view.findViewById(R.id.edit_text_host_name);
		mMailAddressView = (EditText)view.findViewById(R.id.edit_text_mail_address);
		mPasswordView = (EditText)view.findViewById(R.id.edit_text_password);
		mProgressView = view.findViewById(R.id.layout_progress);

		view.findViewById(R.id.button_execute).setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		String hostName = mHostNameView.getText().toString();

		mInstance = new MastodonInstance();
		mInstance.setHostName(hostName);

		RegisterClientRequest request = new RegisterClientRequest(getActivity(), API_ID_REGISTER_CLIENT);

		mProgressView.setVisibility(View.VISIBLE);

		ApiThread thread = ApiThread.newInstance(getActivity(), mInstance, request);
		thread.setListener(this);
		thread.start();
	}

	@Override
	public void onApiSuccess(int identifier, ApiResponse response) {
		if (identifier == API_ID_REGISTER_CLIENT) {
			RegisterClientResponse res = (RegisterClientResponse) response;

			mInstance.setClientId(res.clientId);
			mInstance.setClientSecret(res.clientSecret);

			DebugLog.debug(getClass(), "clientId = " + res.clientId);
			DebugLog.debug(getClass(), "clientSecret = " + res.clientSecret);

			// FIXME:パスワードでOAuth認証はまずいので、以下の『FROM』〜『TO』はいずれ修正すること
			// FROM
			String mailAddress = mMailAddressView.getText().toString();
			String password = mPasswordView.getText().toString();

			AccessTokenRequest request = new AccessTokenRequest(getActivity(), API_ID_ACCESS_TOKEN);
			request.client_id = res.clientId;
			request.client_secret = res.clientSecret;
			request.grant_type = "password";
			request.username = mailAddress;
			request.password = password;
			request.scope = "read%20write%20follow";
			// TO

			ApiThread thread = ApiThread.newInstance(getActivity(), mInstance, request);
			thread.setListener(this);
			thread.start();
		} else if (identifier == API_ID_ACCESS_TOKEN) {
			AccessTokenResponse res = (AccessTokenResponse)response;

			mInstance.setAccessToken(res.accessToken);

			DebugLog.debug(getClass(), "accessToken = " + res.accessToken);

			try {
				MastodonInstanceHolder holder = MastodonInstanceHolder.getSingleton();
				holder.add(mInstance);
				holder.save(getActivity().getApplicationContext());

				showDialog(R.string.phrase_confirmation, R.string.message_instance_save_success, DIALOG_TAG_SUCCESS);
			} catch (IOException ioe) {
				ioe.printStackTrace();

				showDialog(R.string.phrase_error, R.string.message_instance_save_error, DIALOG_TAG_SAVE_ERROR);
			}

			mProgressView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onApiFailure(int identifier, ApiException exception) {
		if (exception.getCause() != null) {
			exception.getCause().printStackTrace();
		} else {
			android.util.Log.e(getClass().getSimpleName(), exception.getMessage());
		}

		showDialog(R.string.phrase_error, R.string.message_instance_save_error, DIALOG_TAG_SAVE_ERROR);
	}

	private void showDialog(int titleResId, int messageResId, String tag) {
		AlertDialogFragment
				.newInstance(getString(titleResId), getString(messageResId))
				.show(getActivity().getSupportFragmentManager(), tag);
	}
}
