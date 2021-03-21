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

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private Context context;
    private Health implementation;
    PluginCall call; // for passing between reqAuth() and processResponse()
    FitnessOptions fitnessOptions;

    // Declare data types compatible with plugin
    public static Map<String, DataType> datatypes = new HashMap<String, DataType>();
    private final String tag = "---- IA HEALTH PLUGIN";
    static {
//        datatypes.put("height", DataType.TYPE_HEIGHT);
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
        // 1. Create a FitnessOptions instance, declaring the data types and access type

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
            this.call = call;
            Util.setCall(call);
            Util.setContext(context);
            GoogleSignIn.requestPermissions(
                    getActivity(),
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
            // Returns to MainActivity onActivityResult()
        } else {
            implementation.accessGoogleFitData(call);
        }
    }

    /**
     * Result of requestAuth function - called from MainActivity.java
     * @param requestCode request code, i.e. GOOGLE_FIT_PERMISSIONS_REQUEST_CODE
     */
    public void processActivityResult(int requestCode) {
        PluginCall savedCall = Util.getCall();
        Context savedContext = Util.getContext();

        Health newHealth = new Health(
                savedContext,
                fitnessOptions
        );
        if(requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            newHealth.accessGoogleFitData(savedCall);
        } else {
            JSObject ret = new JSObject();
            ret.put("success", false);
            ret.put("message", "Missing argument dataType");
            this.call.resolve(ret);
        }
    }

    /**
     *
     * @param call Capacitor Plugin Call
     * @throws JSONException Explosion
     * @throws ParseException Parsed Explosion
     */
    @PluginMethod
    public void query(PluginCall call) throws JSONException, ParseException {
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
            ret.put("success", result);
            ret.put("message", "Query data retrieved");
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }

    /**
     *
     * @param call Capacitor Plugin Call
     */
    @PluginMethod
    public void store(PluginCall call) {
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

        if(!data.has("value")) {
            ret.put("success", false);
            ret.put("message", "Missing argument value");
            call.resolve(ret);
        }

        if(!data.has("sourceName")) {
            ret.put("success", false);
            ret.put("message", "Missing argument sourceName");
            call.resolve(ret);
        }

        if(!data.has("dataType")) {
            ret.put("success", false);
            ret.put("message", "Missing argument dataType");
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
            final Boolean result = implementation.store(data, dt);
            ret.put("success", result);
            ret.put("message", "Not implemented yet.");
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }
}
