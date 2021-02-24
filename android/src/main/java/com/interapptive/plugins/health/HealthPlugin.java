package com.interapptive.plugins.health;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {

    private Health implementation = new Health();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void isAvailable(PluginCall call) {
        // Validation of call object here
        JSObject ret = new JSObject();
        ret.put("result", implementation.isAvailable());
        call.resolve(ret);
    }
}
