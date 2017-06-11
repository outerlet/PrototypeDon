package jp.onetake.prototypedon.mastodon;

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

public class InstanceHolder implements Serializable {
	private static final long serialVersionUID = -5436433305647206164L;

	private static InstanceHolder mSingleton;

	private ArrayList<Instance> mInstanceList;

	public static InstanceHolder getSingleton() {
		if (mSingleton == null) {
			mSingleton = new InstanceHolder();
		}
		return mSingleton;
	}

	private InstanceHolder() {
		mInstanceList = new ArrayList<>();
	}

	public int size() {
		return mInstanceList.size();
	}

	public Instance get(int index) {
		return mInstanceList.get(index);
	}

	public void add(Instance instance) {
		for (Instance inst : mInstanceList) {
			if (inst.equals(instance)) {
				return;
			}
		}

		mInstanceList.add(instance);
	}

	public void remove(Instance instance) {
		for (Instance inst : mInstanceList) {
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
			InstanceHolder holder = (InstanceHolder) ois.readObject();

			mInstanceList.clear();

			for (Instance instance : holder.mInstanceList) {
				mInstanceList.add(instance);
			}

			return true;
		} catch (FileNotFoundException ignore) {
			// ファイルが無い(=セーブされたことがない)だけなので、例外にはしない
		}

		return false;
	}
}
