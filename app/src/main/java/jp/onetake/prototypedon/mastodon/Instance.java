package jp.onetake.prototypedon.mastodon;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Instance implements Serializable, Parcelable {
	private static final long serialVersionUID = 351066163958773477L;

	private String mHostName;
	private String mAccessToken;
	private String mId;
	private String mClientId;
	private String mClientSecret;
	private transient Bitmap mAvatar;
	private transient Bitmap mHeader;

	public Instance() {
		// 特に何もしない
	}

	protected Instance(Parcel in) {
		mHostName = in.readString();
		mAccessToken = in.readString();
		mId = in.readString();
		mClientId = in.readString();
		mClientSecret = in.readString();

		ClassLoader loader = getClass().getClassLoader();
		mAvatar = in.readParcelable(loader);
		mHeader = in.readParcelable(loader);
	}

	public static final Creator<Instance> CREATOR = new Creator<Instance>() {
		@Override
		public Instance createFromParcel(Parcel in) {
			return new Instance(in);
		}

		@Override
		public Instance[] newArray(int size) {
			return new Instance[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mHostName);
		dest.writeString(mAccessToken);
		dest.writeString(mId);
		dest.writeString(mClientId);
		dest.writeString(mClientSecret);
		dest.writeParcelable(mAvatar, flags);
		dest.writeParcelable(mHeader, flags);
	}

	public void setHostName(String hostName) {
		mHostName = hostName;
	}

	public String getHostName() {
		return mHostName;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getId() {
		return mId;
	}

	public void setClientId(String clientId) {
		mClientId = clientId;
	}

	public String getClientId() {
		return mClientId;
	}

	public void setClientSecret(String clientSecret) {
		mClientSecret = clientSecret;
	}

	public String getClientSecret() {
		return mClientSecret;
	}

	public void setAccessToken(String accessToken) {
		mAccessToken = accessToken;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAvatar(Bitmap avatar) {
		mAvatar = avatar;
	}

	public Bitmap getAvatar() {
		return mAvatar;
	}

	public void setHeader(Bitmap header) {
		mHeader = header;
	}

	public Bitmap getHeader() {
		return mHeader;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Instance && ((Instance)obj).getHostName().equals(mHostName);
	}
}
