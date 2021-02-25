package com.interapptive.plugins.health;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.getcapacitor.annotation.Permission;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

@CapacitorPlugin(name = "Health")
public class HealthPlugin extends Plugin {
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private Context context;
    private Health implementation;
    private FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_HEIGHT)
                .addDataType(DataType.TYPE_WEIGHT)
                .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .build();
    // Declare data types compatible with plugin
    public static Map<String, DataType> datatypes = new HashMap<String, DataType>();
    private final String tag = "---- IA HEALTH PLUGIN";
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
        super.load();
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
        try {
            final Boolean result = implementation.isAvailable();
            ret.put("result", result);
            if(result) {
                ret.put("message", "Google fit is available");
            } else {
                ret.put("message", "Google Services not installed or obsolete");
            }
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }

    /**
     * https://gist.github.com/dariosalvi78/66aa2635abd02f4aa4899628daf74cc7#file-mainactivity-java-L90
     * @param call
     */
    @PluginMethod
    public void requestAuth(PluginCall call) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this.getActivity(), gso);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(call, intent, "reqAuthCallBack");
    }

    @ActivityCallback
    private void reqAuthCallBack(PluginCall call, ActivityResult result) {
        if(result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                implementation.accessGoogleFit();
            } catch (ApiException e) {
                Log.w(tag, "signInResult:failed code=" + e.getLocalizedMessage());
            }
        } else if(result.getResultCode() == Activity.RESULT_CANCELED) {
            call.reject("User cancelled login flow");
        } else {
            call.reject("Unknown error - unable to utilise login");
        }
    }

    /**
     *
     * @param call
     * @throws JSONException
     * @throws ParseException
     */
    @PluginMethod
    public void query(PluginCall call) throws JSONException, ParseException {
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
        if(!data.has("dataType")) {
            ret.put("result", false);
            ret.put("message", "Missing argument dataType");
            call.resolve(ret);
        }
        if(!data.has("limit")) {
            ret.put("result", false);
            ret.put("message", "Missing argument limit");
            call.resolve(ret);
        }
        String dataType = call.getData().getString("dataType");
        DataType dt = datatypes.get(dataType);
        if(dt == null) {
            ret.put("result", false);
            ret.put("message", "DataType " + dataType + " not supported");
            call.resolve(ret);
        }
        try {
            final DataReadResponse result = implementation.query(data, dt);
            ret.put("result", result);
            ret.put("message", "Query data retrieved");
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }

    /**
     *
     * @param call
     */
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

        try {
            final Boolean result = implementation.store(data, dt);
            ret.put("result", result);
            ret.put("message", "Not implemented yet.");
            call.resolve(ret);
        } catch (Exception exception) {
            call.reject(exception.getMessage(),exception);
        }
    }
}
