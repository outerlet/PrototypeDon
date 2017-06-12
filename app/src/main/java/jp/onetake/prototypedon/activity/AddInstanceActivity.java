package jp.onetake.prototypedon.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.fragment.AuthorizeFragment;
import jp.onetake.prototypedon.fragment.dialog.AlertDialogFragment;

public class AddInstanceActivity extends AppCompatActivity implements AlertDialogFragment.OnConfirmListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_instance);

		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.layout_container, new AuthorizeFragment());
		trans.commit();
	}

	@Override
	public void onConfirm(AlertDialogFragment dialog) {
		switch (dialog.getTag()) {
			case AuthorizeFragment.DIALOG_TAG_SUCCESS:
				setResult(RESULT_OK);
				break;
			case AuthorizeFragment.DIALOG_TAG_SAVE_ERROR:
				setResult(RESULT_CANCELED);
				break;
		}

		finish();
	}
}
