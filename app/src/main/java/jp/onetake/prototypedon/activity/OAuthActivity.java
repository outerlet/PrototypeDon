package jp.onetake.prototypedon.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.onetake.prototypedon.R;
import jp.onetake.prototypedon.mastodon.Instance;
import jp.onetake.prototypedon.util.DebugLog;

public class OAuthActivity extends BaseActivity {
	private class OAuthWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView webView, String url, Bitmap favicon) {
			String regex = String.format(Locale.US, "https://%1$s/oauth/authorize/(.+)", mInstance.getHostName());

			// TODO:承認を拒否したときのフォローがないので実装すること
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(url);
			if (matcher.matches()) {
				String authCode = matcher.group(1);

				Intent data = new Intent();
				data.putExtra(KEY_AUTH_CODE, authCode);

				setResult(RESULT_OK, data);

				finish();
			} else {
				DebugLog.debug(getClass(), "Not matches.");
			}
		}
	}

	public static final String KEY_AUTH_CODE = "OAuthActivity.KEY_AUTH_CODE";

	private static final String KEY_INSTANCE = "OAuthActivity.KEY_INSTANCE";

	private Instance mInstance;

	public static Intent createLaunchIntent(Context context, Instance instance) {
		Intent intent = new Intent(context, OAuthActivity.class);
		intent.putExtra(KEY_INSTANCE, (Parcelable)instance);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oauth);

		mInstance = getIntent().getParcelableExtra(KEY_INSTANCE);

		WebView webView = (WebView)findViewById(R.id.webview_oauth);
		webView.setWebViewClient(new OAuthWebViewClient());
		webView.loadUrl(createAuthURL());
	}

	private String createAuthURL() {
		try {
			String clientId = URLEncoder.encode(mInstance.getClientId(), "UTF-8");
			String redirectUri = URLEncoder.encode(getString(R.string.api_auth_redirect_uri), "UTF-8");
			String scope = URLEncoder.encode(getString(R.string.api_auth_scope), "UTF-8");

			return String.format(
					Locale.US,
					"https://%1$s/oauth/authorize?client_id=%2$s&response_type=code&redirect_uri=%3$s&scope=%4$s",
					mInstance.getHostName(), clientId, redirectUri, scope);
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		}

		return null;
	}
}
