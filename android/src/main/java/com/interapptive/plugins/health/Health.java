package com.interapptive.plugins.health;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

public class Health {

    private Context context;
    private final String tag = "---- IA HEALTH PLUGIN";

    public Health(Context context) {
        this.context = context;
    }

    /**
     * Echo test
     * @param value
     * @return
     */
    public String echo(String value) {
        return value;
    }

    /**
     * detects if a) Google APIs are available, b) Google Fit is actually installed
     * @param context
     * @return
     */
    public Boolean isAvailable(Context context) {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int apiResult = gApi.isGooglePlayServicesAvailable(context);
        if(apiResult != ConnectionResult.SUCCESS) {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo("com.google.android.apps.fitness", PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager .NameNotFoundException e) {
                Log.e(tag, "Google Fit not installed");
                return false;
            }
        } else {
            Log.e(tag, "Google Services not installed or obsolete");
            return false;
        }
    }

    public Boolean requestAuth() {
        return true;
    }
}
