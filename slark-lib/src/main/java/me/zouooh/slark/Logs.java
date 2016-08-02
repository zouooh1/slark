package me.zouooh.slark;

import android.util.Log;

import java.util.Locale;

public class Logs {
    public static String TAG = "Slark";

    public static void d(String format, Object... args) {
        if (Slark.DEBUG){
            Log.d(TAG, buildMessage(format, args));
        }
    }

    public static void dd(String datas) {
        if (Slark.DEBUG_DATAS){
            Log.d(TAG, buildMessage(datas));
        }
    }

    private static String buildMessage(String format, Object... args) {
        String msg = (args == null) ? format : String.format(Locale.getDefault(), format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Logs.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.getDefault(), "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }
}
