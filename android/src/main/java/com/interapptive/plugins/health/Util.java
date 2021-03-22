package com.interapptive.plugins.health;

import android.content.Context;

import com.getcapacitor.PluginCall;

public class Util {
    private static PluginCall utilCall;
    private static Context utilContext;

    public static void setCall(PluginCall call) {
        utilCall = call;
    }

    public static PluginCall getCall() {
        return utilCall;
    }

//    public static void setContext(Context context) {
//        utilContext = context;
//    }
//
//    public static Context getContext() {
//        return utilContext;
//    }
}
