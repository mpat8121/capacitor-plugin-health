package com.interapptive.plugins.health;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {

    private Context context;
    private Health implementation;

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
}
