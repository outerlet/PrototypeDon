package jp.onetake.prototypedon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiExecuteThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.VerifyCredentialsRequest;
import jp.onetake.prototypedon.fragment.AuthorizeFragment;
import jp.onetake.prototypedon.fragment.dialog.AlertDialogFragment;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.mastodon.InstanceHolder;
import jp.onetake.prototypedon.util.DebugLog;

public class LaunchActivity extends BaseActivity
		implements ApiExecuteThread.ApiResultListener, AlertDialogFragment.OnConfirmListener {
	private static final String TAG_DIALOG_INVALID_INSTANCE		= "LaunchActivity.TAG_DIALOG_INVALID_INSTANCE";
	private static final String TAG_DIALOG_INSTANCE_NOT_EXIST	= "LaunchActivity.TAG_DIALOG_INSTANCE_EMPTY";

	private InstanceHolder mInstanceHolder;
	private List<Instance> mInvalidList;
	private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_launch);

		mInstanceHolder = InstanceHolder.getSingleton();
		mInvalidList = new ArrayList<>();
		mCurrentIndex = -1;

        try {
			if (mInstanceHolder.load(getApplicationContext()) && mInstanceHolder.size() > 0) {
				verifyCredentials();
			} else {
				authorize();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void onApiSuccess(int apiId, ApiResponse response) {
		verifyCredentials();
	}

	@Override
	public void onApiFailure(int apiId, ApiException exception) {
		if (exception.getCause() != null) {
			exception.getCause().printStackTrace();
		} else {
			DebugLog.error(getClass(), exception.getMessage());
		}

		mInvalidList.add(mInstanceHolder.get(mCurrentIndex));

		verifyCredentials();
	}

	@Override
	public void onConfirm(AlertDialogFragment dialog) {
		String tag = dialog.getTag();

		if (tag == null) {
			throw new RuntimeException("Dialog tag is empty.");
		} else switch (tag) {
			case TAG_DIALOG_INVALID_INSTANCE:
				for (Instance instance : mInvalidList) {
					mInstanceHolder.remove(instance);
				}

				try {
					mInstanceHolder.save(getApplicationContext());

					if (mInstanceHolder.size() > 0) {
						startActivity(TimelinesActivity.createLaunchIntent(this, 0));
					} else {
						AlertDialogFragment
								.newInstance(getString(R.string.phrase_confirmation), getString(R.string.message_instance_not_exists))
								.show(getSupportFragmentManager(), TAG_DIALOG_INSTANCE_NOT_EXIST);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				break;
			case TAG_DIALOG_INSTANCE_NOT_EXIST:
				authorize();

				break;
			case AuthorizeFragment.DIALOG_TAG_SUCCESS:
				Intent intent = TimelinesActivity.createLaunchIntent(this, 0);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

				break;
			case AuthorizeFragment.DIALOG_TAG_SAVE_ERROR:
				finish();

				break;
		}
	}

	private void verifyCredentials() {
		if (++mCurrentIndex < mInstanceHolder.size()) {
			VerifyCredentialsRequest request = new VerifyCredentialsRequest(
					getApplicationContext(), getResources().getInteger(R.integer.api_id_verify_credentials));

			Instance instance = mInstanceHolder.get(mCurrentIndex);

			DebugLog.debug(getClass(), instance.getHostName() + ": token = " + instance.getAccessToken());

			ApiExecuteThread t = ApiExecuteThread.newInstance(instance, request);
			t.setListener(this);
			t.start();
		} else {
			if (mInvalidList.size() > 0) {
				String text = String.format(
						Locale.JAPAN, getString(R.string.message_invalid_instance_error), mInvalidList.size());

				AlertDialogFragment
						.newInstance(getString(R.string.phrase_error), text)
						.show(getSupportFragmentManager(), TAG_DIALOG_INVALID_INSTANCE);
			} else {
				Intent intent = TimelinesActivity.createLaunchIntent(this, 0);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);

				// Activity遷移時にデフォルトアニメがつくと不恰好に見えるのでとりあえず無効に
				overridePendingTransition(0, 0);
			}
		}
	}

	private void authorize() {
		findViewById(R.id.layout_authorize).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_initialize).setVisibility(View.INVISIBLE);

		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_authorize, new AuthorizeFragment());
		trans.commit();
	}
}
