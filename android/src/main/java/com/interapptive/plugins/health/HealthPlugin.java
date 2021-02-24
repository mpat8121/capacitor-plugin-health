package com.interapptive.plugins.health;

import android.content.Context;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataSource;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {

    private Context context;
    private Health implementation;

    // Declare data types compatible with plugin
    public static Map<String, DataType> datatypes = new HashMap<String, DataType>();

    static {
        datatypes.put("height", DataType.TYPE_HEIGHT);
        datatypes.put("weight", DataType.TYPE_WEIGHT);
        datatypes.put("fat_percentage", DataType.TYPE_BODY_FAT_PERCENTAGE);
    }

    /**
     * Load Method
     * Load the context
     */
    public void load() {
        context = getContext();
        implementation = new Health(context);
    }

    /**
     * Echo Method
     * test the plugin
     * @param call
     */
    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    /**
     * detects if a) Google APIs are available, b) Google Fit is actually installed
     * @param call
     */
    @PluginMethod
    public void isAvailable(PluginCall call) {
        // Validation of call object here
        JSObject ret = new JSObject();
        final Boolean result = implementation.isAvailable(context);
        ret.put("result", result);
        if(result) {
            ret.put("message", "Google fit is available");
        } else {
            ret.put("message", "Google Services not installed or obsolete");
        }
        call.resolve(ret);
    }

    // https://gist.github.com/dariosalvi78/66aa2635abd02f4aa4899628daf74cc7#file-mainactivity-java-L90
    @PluginMethod
    public void requestAuth(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", implementation.requestAuth());
        ret.put("message", "Not implemented yet.");
        call.resolve(ret);
    }

    @PluginMethod
    public void query(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", implementation.query());
        ret.put("message", "Not implemented yet.");
        call.resolve(ret);
    }

    @PluginMethod
    public void store(PluginCall call) {
        JSObject ret = new JSObject();
        JSObject data = call.getData();

        if(!data.has("startDate")) {
            ret.put("result", false);
            ret.put("message", "Missing argument startDate");
            call.resolve(ret);
        }

        if(!data.has("endDate")) {
            ret.put("result", false);
            ret.put("message", "Missing argument endDate");
            call.resolve(ret);
        }

        if(!data.has("value")) {
            ret.put("result", false);
            ret.put("message", "Missing argument value");
            call.resolve(ret);
        }

        if(!data.has("sourceName")) {
            ret.put("result", false);
            ret.put("message", "Missing argument sourceName");
            call.resolve(ret);
        }

        if(!data.has("dataType")) {
            ret.put("result", false);
            ret.put("message", "Missing argument dataType");
            call.resolve(ret);
        }
        String dataType = call.getData().getString("dataType");
        DataType dt = datatypes.get(dataType);
        if(dt == null) {
            ret.put("result", false);
            ret.put("message", "DataType " + dataType + " not supported");
            call.resolve(ret);
        }

        // --- UP TO HERE ---
        try {
            final Boolean result = implementation.store(data, dt);
            ret.put("result", result);
            ret.put("message", "Not implemented yet.");
            call.resolve(ret);
        } catch (Exception exception) {

        }
    }
}
