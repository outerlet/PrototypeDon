package jp.onetake.prototypedon.text;

import android.net.Uri;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * TextViewに埋め込まれたURLをクリックしたときデフォルトブラウザに飛ばさないためのLinkMovementMethod
 */
public class StatusLinkMovementMethod extends LinkMovementMethod {
	public interface LinkClickListener {
		void onClick(TextView textView, Uri uri);
	}

	private LinkClickListener mListener;

	public void setListener(LinkClickListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(TextView textView, Spannable buffer, MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= textView.getTotalPaddingLeft();
			y -= textView.getTotalPaddingTop();

			x += textView.getScrollX();
			y += textView.getScrollY();

			Layout layout = textView.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);

			ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					// リスナがセットされている場合はそちらをキックする
					if (mListener != null) {
						String textURL = ((URLSpan)link[0]).getURL();
						Uri uri = Uri.parse(textURL);

						mListener.onClick(textView, uri);
					} else {
						link[0].onClick(textView);
					}
				} else {
					Selection.setSelection(buffer,
							buffer.getSpanStart(link[0]),
							buffer.getSpanEnd(link[0]));
				}

				return true;
			} else {
				Selection.removeSelection(buffer);
			}
		}

		return super.onTouchEvent(textView, buffer, event);
	}
}
