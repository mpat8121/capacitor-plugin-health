package com.interapptive.plugins.health;

import android.content.Context;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResponse;

import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private Context context;
    private Health implementation;
    FitnessOptions fitnessOptions;

    // Declare data types compatible with plugin
    public static Map<String, DataType> datatypes = new HashMap<String, DataType>();
    private final String tag = "---- IA HEALTH PLUGIN";
    static {
        // dataTypes.put("height", DataType.TYPE_HEIGHT);
        datatypes.put("weight", DataType.TYPE_WEIGHT);
        datatypes.put("fat_percentage", DataType.TYPE_BODY_FAT_PERCENTAGE);
    }

    /**
     * Load Method
     * Load the context
     */
    public void load() {
        super.load();
        context = getContext();
        // Optional for future implementation
        // .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        // .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
                .build();

        implementation = new Health(context, fitnessOptions);
    }

    /**
     * Detects if:
     * a) Google APIs are available,
     * b) Google Fit is actually installed
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void isAvailable(PluginCall call) {
        JSObject ret = new JSObject();
        try {
            final Boolean result = implementation.isAvailable();
            ret.put("success", result);
            if(result) {
                ret.put("message", "Google fit is available");
            } else {
                ret.put("message", "Google Services not installed or obsolete");
            }
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(), exception);
        }
    }

    /**
     * Attempts to connect to Google Fit API, if:
     * a) Already connected, it will return data via accessGoogleFit()
     * b) Not connected, runs through authentication and permission then returns data
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void requestAuth(PluginCall call) {
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(
                context,
                fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            Util.setCall(call);
            GoogleSignIn.requestPermissions(
                    getActivity(),
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
            // Returns to MainActivity onActivityResult()
        } else {
            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Connected to Google Fit");
            call.resolve(ret);
        }
    }

    /**
     * Result of requestAuth function - called from MainActivity.java
     * @param requestCode i.e. GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
     */
    public void processActivityResult(int requestCode) {
        PluginCall savedCall = Util.getCall();
        JSObject ret = new JSObject();
        if(requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            ret.put("success", true);
            ret.put("message", "Connected to Google Fit");
        } else {
            ret.put("success", false);
            ret.put("message", "Unable to connect or user cancelled");
        }
        savedCall.resolve(ret);
    }

    /**
     * Return Google Fit data for one Data Type
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void query(PluginCall call) {
        JSObject ret = new JSObject();
        JSObject data = call.getData();
        if(!data.has("startDate")) {
            ret.put("success", false);
            ret.put("message", "Missing argument startDate");
            call.resolve(ret);
        }
        if(!data.has("endDate")) {
            ret.put("success", false);
            ret.put("message", "Missing argument endDate");
            call.resolve(ret);
        }
        if(!data.has("dataType")) {
            ret.put("success", false);
            ret.put("message", "Missing argument dataType");
            call.resolve(ret);
        }
        if(!data.has("limit")) {
            ret.put("success", false);
            ret.put("message", "Missing argument limit");
            call.resolve(ret);
        }
        String dataType = call.getData().getString("dataType");
        DataType dt = datatypes.get(dataType);
        if(dt == null) {
            ret.put("success", false);
            ret.put("message", "DataType " + dataType + " not supported");
            call.resolve(ret);
        }

        try {
            final DataReadResponse result = implementation.query(data, dt);
            if(result != null) {
                ret.put("success", true);
                ret.put("message", "Query data retrieved.");
            } else {
                ret.put("success", false);
                ret.put("message", "Failed to get data, null account.");
            }
            ret.put("data", result);
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }

    /**
     * Get all Google Fit data in single call
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void queryAll(PluginCall call) {
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(
                context,
                fitnessOptions);

        if (GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            implementation.accessGoogleFitData(call);
        } else {
            JSObject ret = new JSObject();
            ret.put("success", false);
            ret.put("message", "Unable to return query data");
            call.resolve(ret);
        }
    }

    /**
     * Store data in Google Fit
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void store(PluginCall call) {
        JSObject ret = new JSObject();
        JSObject data = call.getData();

        boolean valid = true;

        if(!data.has("startDate")) {
            valid = false;
            ret.put("success", false);
            ret.put("message", "Missing argument startDate");
            call.resolve(ret);
        }

        if(!data.has("endDate")) {
            valid = false;
            ret.put("success", false);
            ret.put("message", "Missing argument endDate");
            call.resolve(ret);
        }

        if(!data.has("value")) {
            valid = false;
            ret.put("success", false);
            ret.put("message", "Missing argument value");
            call.resolve(ret);
        }

        if(!data.has("dataType")) {
            valid = false;
            ret.put("success", false);
            ret.put("message", "Missing argument dataType");
            call.resolve(ret);
        }

        String dataType = call.getData().getString("dataType");
        DataType dt = datatypes.get(dataType);
        if(dt == null) {
            valid = false;
            ret.put("success", false);
            ret.put("message", "DataType " + dataType + " not supported");
            call.resolve(ret);
        }

        if(valid) {
            try {
                implementation.store(data, dt, call);
            } catch (Exception exception) {
                call.reject(exception.getMessage(), exception);
            }
        }
    }
}
