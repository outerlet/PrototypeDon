package jp.onetake.prototypedon.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import jp.onetake.prototypedon.R;

public class BaseActivity extends AppCompatActivity {
	private Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 縦固定
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Toolbarを全てのActivityに埋め込むのが面倒なので、共通のレイアウトをセットし
	 * その下にサブクラスのレイアウトをaddするためのオーバーライドメソッド<br />
	 * これを呼ばない限りgetToolbarでツールバーオブジェクトを得ることはできない(nullが返ってくる)
	 * @param layoutResID	サブクラスのレイアウトリソースID
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(R.layout.activity_base);

		LinearLayout container = (LinearLayout) findViewById(R.id.layout_container);

		View layout = LayoutInflater.from(this).inflate(layoutResID, container, false);
		container.addView(layout);

		// ツールバーの保持
		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
	}

	/**
	 * 本来のsetContentViewを呼び出してレイアウトを設定する<br />
	 * setContentViewでレイアウトを設定するとツールバーありきのレイアウトになってしまうので
	 * もしレイアウト全てを自分の思う通りにしたければこちらを使うこと
	 * @param layoutResId	レイアウトリソースID
	 */
	@SuppressWarnings("unused")
	public void setCustomContentView(int layoutResId) {
		super.setContentView(layoutResId);
	}

	/**
	 * ツールバーを取得する<br />
	 * BaseActivity#setContentViewでインスタンス化するので、これを呼んでいないとnullが返ってくる
	 * @return	ツールバー
	 */
	protected Toolbar getToolbar() {
		return mToolbar;
	}
}
