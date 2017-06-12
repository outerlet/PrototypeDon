package jp.onetake.prototypedon.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import jp.onetake.prototypedon.R;

public class PromptDialogFragment extends DialogFragment {
	public interface PromptInputListener {
		void onInput(DialogFragment dialog, String inputText);
		void onCancel(DialogFragment dialog, String inputText);
	}

	private static final String KEY_TITLE			= "PromptDialogFragment.KEY_TITLE";
	private static final String KEY_OK_LABEL		= "PromptDialogFragment.KEY_OK_LABEL";
	private static final String KEY_CANCEL_LABEL	= "PromptDialogFragment.KEY_CANCEL_LABEL";
	private static final String KEY_MESSAGE			= "PromptDialogFragment.KEY_MESSAGE";

	public static PromptDialogFragment newInstance(String title, String okLabel, String cancelLabel, String message) {
		Bundle params = new Bundle();
		params.putString(KEY_TITLE, title);
		params.putString(KEY_OK_LABEL, okLabel);
		params.putString(KEY_CANCEL_LABEL, cancelLabel);
		params.putString(KEY_MESSAGE, message);

		PromptDialogFragment fragment = new PromptDialogFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle params = getArguments();

		View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_prompt_text, null, false);
		final EditText editText = (EditText)view.findViewById(R.id.edittext_toot_message);

		String message = params.getString(KEY_MESSAGE);
		if (!TextUtils.isEmpty(message)) {
			editText.setText(message);
		}

		final DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (getActivity() instanceof PromptInputListener) {
					PromptInputListener listener = (PromptInputListener)getActivity();
					String text = editText.getText().toString();

					if (which == DialogInterface.BUTTON_POSITIVE) {
						listener.onInput(PromptDialogFragment.this, text);
					} else {
						listener.onCancel(PromptDialogFragment.this, text);
					}
				}
			}
		};

		return new AlertDialog.Builder(getActivity())
				.setTitle(params.getString(KEY_TITLE))
				.setView(editText)
				.setPositiveButton(params.getString(KEY_OK_LABEL), clickListener)
				.setNegativeButton(params.getString(KEY_CANCEL_LABEL), clickListener)
				.create();
	}
}
