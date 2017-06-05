package jp.onetake.prototypedon.common;

import java.io.Serializable;

@SuppressWarnings("unused")
public class MastodonInstance implements Serializable {
	private static final long serialVersionUID = 351066163958773477L;

	private String mHostName;
	private String mAccessToken;
	private String mId;
	private String mClientId;
	private String mClientSecret;

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

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MastodonInstance && ((MastodonInstance)obj).getHostName().equals(mHostName);
	}
}
