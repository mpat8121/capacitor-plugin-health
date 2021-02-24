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

    public String echo(String value) {
        return value;
    }

    public Boolean isAvailable(Context context) {
        GoogleApiAvailability gapi = GoogleApiAvailability.getInstance();
        int apiresult = gapi.isGooglePlayServicesAvailable(context);
        if(apiresult != ConnectionResult.SUCCESS){
            Log.e("Error", "Google Services not installed or obsolete");
            return false;
        } else {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo("com.google.android.apps.fitness", PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager .NameNotFoundException e) {
                Log.e("Exception", "Google Fit not installed");
                return false;
            }
        }
    }

    public Boolean requestAuth() {
        return true;
    }
}
