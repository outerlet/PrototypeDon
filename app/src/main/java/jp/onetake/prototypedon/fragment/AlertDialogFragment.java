package jp.onetake.prototypedon.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import jp.onetake.prototypedon.R;

public class AlertDialogFragment extends DialogFragment {
	public interface OnConfirmListener {
		void onConfirm(AlertDialogFragment dialog);
	}

	private static final String KEY_TITLE	= "AlertDialogFragment.KEY_TITLE";
	private static final String KEY_MESSAGE	= "AlertDialogFragment.KEY_MESSAGE";

	public static AlertDialogFragment newInstance(String title, String message) {
		Bundle params = new Bundle();
		params.putString(KEY_TITLE, title);
		params.putString(KEY_MESSAGE, message);

		AlertDialogFragment fragment = new AlertDialogFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		return new AlertDialog.Builder(getActivity())
				.setTitle(params.getString(KEY_TITLE))
				.setMessage(params.getString(KEY_MESSAGE))
				.setPositiveButton(R.string.phrase_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (getActivity() instanceof OnConfirmListener) {
							((OnConfirmListener)getActivity()).onConfirm(AlertDialogFragment.this);
						}
					}
				})
				.create();
	}
}
