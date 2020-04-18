package com.baidu.histogram.utils;

import android.util.Log;

public class LogUtil {
    public static final boolean LOGGABLE = true;

    public static void e(String tag, String content) {
        Log.e(tag, content);
    }
}
