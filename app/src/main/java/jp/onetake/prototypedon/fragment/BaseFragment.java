package jp.onetake.prototypedon.fragment;

import android.support.v4.app.Fragment;

import jp.onetake.prototypedon.fragment.dialog.AlertDialogFragment;

public class BaseFragment extends Fragment {
	protected void showDialog(int titleResId, int messageResId, String tag) {
		AlertDialogFragment
				.newInstance(getString(titleResId), getString(messageResId))
				.show(getActivity().getSupportFragmentManager(), tag);
	}
}
