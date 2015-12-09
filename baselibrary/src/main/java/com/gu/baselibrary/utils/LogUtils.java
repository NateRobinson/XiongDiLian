package com.gu.baselibrary.utils;

import android.util.Log;

/**
 * Created by Nate on 2015/9/10. 日志工具类
 */
public class LogUtils {

    private LogUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isLogEnable = true;

    /**
     * Debug
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (isLogEnable) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.d(tag, rebuildMsg(stackTraceElement, msg));
        }
    }

    /**
     * Information
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (isLogEnable) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.i(tag, rebuildMsg(stackTraceElement, msg));
        }
    }

    /**
     * Verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (isLogEnable) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.v(tag, rebuildMsg(stackTraceElement, msg));
        }
    }

    public static void w(String tag, String msg) {
        if (isLogEnable) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.w(tag, rebuildMsg(stackTraceElement, msg));
        }
    }


    public static void e(String tag, String msg) {
        if (isLogEnable) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.e(tag, rebuildMsg(stackTraceElement, msg));
        }
    }

    /**
     * 格式化msg形式
     *
     * @param msg
     * @return
     */
    private static String rebuildMsg(StackTraceElement stackTraceElement, String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(stackTraceElement.getFileName());
        sb.append(" (");
        sb.append(stackTraceElement.getLineNumber());
        sb.append(") ");
        sb.append(stackTraceElement.getMethodName());
        sb.append(": ");
        sb.append(msg);
        return sb.toString();
    }
}
