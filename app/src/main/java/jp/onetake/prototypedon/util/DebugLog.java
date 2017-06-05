package jp.onetake.prototypedon.util;

import jp.onetake.prototypedon.BuildConfig;

public class DebugLog {
	private static final boolean IS_DEBUG = BuildConfig.BUILD_TYPE.toLowerCase().contains("debug");

	public static void debug(Class cls, String text) {
		if (IS_DEBUG) {
			android.util.Log.d(cls.getSimpleName(), text);
		}
	}

	public static void error(Class cls, String text) {
		if (IS_DEBUG) {
			android.util.Log.e(cls.getSimpleName(), text);
		}
	}

	public static void info(Class cls, String text) {
		if (IS_DEBUG) {
			android.util.Log.i(cls.getSimpleName(), text);
		}
	}

	public static void verbose(Class cls, String text) {
		if (IS_DEBUG) {
			android.util.Log.v(cls.getSimpleName(), text);
		}
	}

	public static void warn(Class cls, String text) {
		if (IS_DEBUG) {
			android.util.Log.w(cls.getSimpleName(), text);
		}
	}
}
