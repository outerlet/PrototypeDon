package jp.onetake.prototypedon.common;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import jp.onetake.prototypedon.R;

public class MastodonInstanceHolder implements Serializable {
	private static final long serialVersionUID = -5436433305647206164L;

	private static MastodonInstanceHolder mSingleton;

	private ArrayList<MastodonInstance> mInstanceList;

	public static MastodonInstanceHolder getSingleton() {
		if (mSingleton == null) {
			mSingleton = new MastodonInstanceHolder();
		}
		return mSingleton;
	}

	private MastodonInstanceHolder() {
		mInstanceList = new ArrayList<>();
	}

	public int size() {
		return mInstanceList.size();
	}

	public MastodonInstance get(int index) {
		return mInstanceList.get(index);
	}

	public void add(MastodonInstance instance) {
		for (MastodonInstance inst : mInstanceList) {
			if (inst.equals(instance)) {
				return;
			}
		}

		mInstanceList.add(instance);
	}

	public void remove(MastodonInstance instance) {
		for (MastodonInstance inst : mInstanceList) {
			if (inst.equals(instance)) {
				mInstanceList.remove(inst);
			}
		}
	}

	public void save(Context context) throws IOException {
		String fileName = context.getString(R.string.file_name_app_values);

		try (FileOutputStream fos = context.getApplicationContext().openFileOutput(fileName, Activity.MODE_PRIVATE);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(this);
		}
	}

	public boolean load(Context context) throws IOException, ClassNotFoundException {
		String fileName = context.getString(R.string.file_name_app_values);

		try (FileInputStream fis = context.getApplicationContext().openFileInput(fileName);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			MastodonInstanceHolder holder = (MastodonInstanceHolder) ois.readObject();

			mInstanceList.clear();

			for (MastodonInstance instance : holder.mInstanceList) {
				mInstanceList.add(instance);
			}

			return true;
		} catch (FileNotFoundException ignore) {
			// ファイルが無い(=セーブされたことがない)だけなので、例外にはしない
		}

		return false;
	}
}
