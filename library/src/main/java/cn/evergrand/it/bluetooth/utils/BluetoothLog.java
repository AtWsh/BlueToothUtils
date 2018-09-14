package cn.evergrand.it.bluetooth.utils;


import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import cn.evergrand.it.bluetooth.BlueToothConfig;

public class BluetoothLog {

    private static final String LOG_TAG = "miio-bluetooth";


    public static void i(String msg) {
        if (BlueToothConfig.sOpenLog) {
            Log.i(LOG_TAG, msg);
        }
    }

    public static void e(String msg) {
        if (BlueToothConfig.sOpenLog) {
            Log.e(LOG_TAG, msg);
        }
    }

    public static void v(String msg) {
        if (BlueToothConfig.sOpenLog) {
            Log.v(LOG_TAG, msg);
        }
    }

    public static void d(String msg) {
        if (BlueToothConfig.sOpenLog) {
            Log.d(LOG_TAG, msg);
        }
    }

    public static void w(String msg) {
        if (BlueToothConfig.sOpenLog) {
            Log.w(LOG_TAG, msg);
        }
    }

    public static void e(Throwable e) {
        if (BlueToothConfig.sOpenLog) {
            e(getThrowableString(e));
        }
    }

    public static void w(Throwable e) {
        if (BlueToothConfig.sOpenLog) {
            w(getThrowableString(e));
        }
    }

    private static String getThrowableString(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        while (e != null) {
            e.printStackTrace(printWriter);
            e = e.getCause();
        }

        String text = writer.toString();

        printWriter.close();

        return text;
    }

}
