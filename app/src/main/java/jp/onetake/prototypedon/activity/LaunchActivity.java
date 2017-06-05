package jp.onetake.prototypedon.activity;

import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.api.ApiException;
import jp.onetake.prototypedon.api.ApiThread;
import jp.onetake.prototypedon.api.ApiResponse;
import jp.onetake.prototypedon.api.VerifyCredentialsRequest;
import jp.onetake.prototypedon.common.MastodonInstance;
import jp.onetake.prototypedon.common.MastodonInstanceHolder;
import jp.onetake.prototypedon.fragment.AlertDialogFragment;
import jp.onetake.prototypedon.util.DebugLog;

public class LaunchActivity extends BasicActivity
		implements ApiThread.ApiResultListener, AlertDialogFragment.OnConfirmListener {
	private static final int API_ID_VERIFY_CREDENTIALS			= 10001;
	private static final String TAG_DIALOG_INVALID_INSTANCE		= "LaunchActivity.TAG_DIALOG_INVALID_INSTANCE";
	private static final String TAG_DIALOG_INSTANCE_NOT_EXIST	= "LaunchActivity.TAG_DIALOG_INSTANCE_EMPTY";

	private MastodonInstanceHolder mInstanceHolder;
	private List<MastodonInstance> mInvalidList;
	private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		mInstanceHolder = MastodonInstanceHolder.getSingleton();
		mInvalidList = new ArrayList<>();
		mCurrentIndex = -1;

        try {
			if (mInstanceHolder.load(getApplicationContext()) && mInstanceHolder.size() > 0) {
				executeVerification();
			} else {
				startActivity(new Intent(this, AuthorizationActivity.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void executeVerification() {
		if (++mCurrentIndex < mInstanceHolder.size()) {
			VerifyCredentialsRequest request =
					new VerifyCredentialsRequest(getApplicationContext(), API_ID_VERIFY_CREDENTIALS);

			ApiThread t = ApiThread.newInstance(this, mInstanceHolder.get(mCurrentIndex), request);
			t.setListener(this);
			t.start();
		} else {
			if (mInvalidList.size() > 0) {
				String text = String.format(Locale.JAPAN, getString(R.string.message_invalid_instance_error), mInvalidList.size());

				AlertDialogFragment
						.newInstance(getString(R.string.phrase_error), text)
						.show(getSupportFragmentManager(), TAG_DIALOG_INVALID_INSTANCE);
			} else {
				startActivity(new Intent(this, MainActivity.class));
			}
		}
	}

	@Override
	public void onApiSuccess(int identifier, ApiResponse response) {
		executeVerification();
	}

	@Override
	public void onApiFailure(int identifier, ApiException exception) {
		if (exception.getCause() != null) {
			exception.getCause().printStackTrace();
		} else {
			DebugLog.error(getClass(), exception.getMessage());
		}

		mInvalidList.add(mInstanceHolder.get(mCurrentIndex));

		executeVerification();
	}

	@Override
	public void onConfirm(AlertDialogFragment dialog) {
		if (dialog.getTag().equals(TAG_DIALOG_INVALID_INSTANCE)) {
			for (MastodonInstance instance : mInvalidList) {
				mInstanceHolder.remove(instance);
			}

			try {
				mInstanceHolder.save(getApplicationContext());

				if (mInstanceHolder.size() > 0) {
					startActivity(new Intent(this, MainActivity.class));
				} else {
					AlertDialogFragment
							.newInstance(getString(R.string.phrase_confirmation), getString(R.string.message_instance_not_exists))
							.show(getSupportFragmentManager(), TAG_DIALOG_INSTANCE_NOT_EXIST);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else if (dialog.getTag().equals(TAG_DIALOG_INSTANCE_NOT_EXIST)) {
			startActivity(new Intent(this, AuthorizationActivity.class));
		}
	}
}
