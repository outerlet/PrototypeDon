package jp.onetake.prototypedon.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.AccessTokenRequest;
import jp.onetake.prototypedon.api.AccessTokenResponse;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.RegisterClientRequest;
import jp.onetake.prototypedon.api.RegisterClientResponse;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;

public class AuthorizeFragment extends BaseFragment
		implements View.OnClickListener, ApiExecuteThread.ApiResultListener {
	public static final String DIALOG_TAG_SUCCESS		= "AuthorizeFragment.DIALOG_TAG_SUCCESS";
	public static final String DIALOG_TAG_SAVE_ERROR	= "AuthorizeFragment.DIALOG_TAG_SAVE_ERROR";

	private EditText mHostNameView;
	private EditText mMailAddressView;
	private EditText mPasswordView;
	private View mProgressView;
	private Instance mInstance;

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

			DebugLog.debug(getClass(), "clientId = " + res.clientId);
			DebugLog.debug(getClass(), "clientSecret = " + res.clientSecret);

			// FIXME:パスワードでOAuth認証はまずいので、以下の『FROM』〜『TO』はいずれ修正すること
			// FROM
			String mailAddress = mMailAddressView.getText().toString();
			String password = mPasswordView.getText().toString();

			AccessTokenRequest request =
					new AccessTokenRequest(getActivity(), getResources().getInteger(R.integer.api_id_access_token));
			request.client_id = res.clientId;
			request.client_secret = res.clientSecret;
			request.grant_type = "password";
			request.username = mailAddress;
			request.password = password;
			request.scope = "read%20write%20follow";
			// TO

			ApiExecuteThread thread = ApiExecuteThread.newInstance(mInstance, request);
			thread.setListener(this);
			thread.start();
		} else if (apiId == getResources().getInteger(R.integer.api_id_access_token)) {
			AccessTokenResponse res = (AccessTokenResponse)response;

			mInstance.setAccessToken(res.accessToken);

			DebugLog.debug(getClass(), "access_token = " + res.accessToken);

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
