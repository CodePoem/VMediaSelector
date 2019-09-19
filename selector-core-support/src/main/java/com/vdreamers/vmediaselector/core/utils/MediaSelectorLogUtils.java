package com.vdreamers.vmediaselector.core.utils;

import android.util.Log;

import com.vdreamers.vmediaselector.core.BuildConfig;

/**
 * 日志工具类
 * <p>
 * date 2019-09-18 20:25:51
 *
 * @author <a href="mailto:codepoetdream@gmail.com">Mr.D</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MediaSelectorLogUtils {
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    private static int level = BuildConfig.DEBUG ? DEBUG : NOTHING;

    public static final String TAG = MediaSelectorLogUtils.class.getSimpleName();

    private MediaSelectorLogUtils() {
        // cannot be instantiated
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void setLevel(int level) {
        MediaSelectorLogUtils.level = level;
    }

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void v(String msg) {
        if (level <= VERBOSE) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (level <= DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (level <= INFO) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (level <= WARN) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (level <= ERROR) {
            Log.e(TAG, msg);
        }
    }

    public static void v(Exception e) {
        if (level <= VERBOSE) {
            Log.v(TAG, e.getLocalizedMessage());
        }
    }

    public static void d(Exception e) {
        if (level <= DEBUG) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public static void i(Exception e) {
        if (level <= INFO) {
            Log.i(TAG, e.getLocalizedMessage());
        }
    }

    public static void w(Exception e) {
        if (level <= WARN) {
            Log.w(TAG, e.getLocalizedMessage());
        }
    }

    public static void e(Exception e) {
        if (level <= ERROR) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
