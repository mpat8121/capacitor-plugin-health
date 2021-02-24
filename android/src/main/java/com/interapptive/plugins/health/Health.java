package com.interapptive.plugins.health;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getcapacitor.JSObject;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataSet;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

public class Health {

    private Context context;
    private final String tag = "---- IA HEALTH PLUGIN";

    // Google account to access the API
    GoogleSignInAccount account;

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
        return false;
    }

    public Boolean query() {
        return false;
    }

    public Boolean store(JSObject data, DataType dt) throws JSONException {

        long st = data.getLong("startDate");
        long et = data.getLong("endDate");

        String sourceBundleId = context.getApplicationContext().getPackageName();
        if (data.has("sourceBundleId")) {
            sourceBundleId = data.getString("sourceBundleId");
        }

        DataSource dataSrc = new DataSource.Builder()
                .setAppPackageName(sourceBundleId)
                .setDataType(dt)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet.Builder dataSetBuilder = DataSet.builder(dataSrc);
        DataPoint.Builder dataPointBuilder = DataPoint.builder(dataSrc);
        dataPointBuilder.setTimeInterval(st, et, TimeUnit.MILLISECONDS);

        if (dt.equals(DataType.TYPE_HEIGHT)) {
            String value = data.getString("value");
            float height = Float.parseFloat(value);
            dataPointBuilder.setField(Field.FIELD_HEIGHT, height);
        } else if (dt.equals(DataType.TYPE_WEIGHT)) {
            String value = data.getString("value");
            float weight = Float.parseFloat(value);
            dataPointBuilder.setField(Field.FIELD_WEIGHT, weight);
        } else if (dt.equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
            String value = data.getString("value");
            float perc = Float.parseFloat(value);
            dataPointBuilder.setField(Field.FIELD_PERCENTAGE, perc);
        }
        dataSetBuilder.add(dataPointBuilder.build());

        // --- UP TO HERE ---
        Fitness.getHistoryClient(context, this.account)
                .insertData(dataSetBuilder.build())
                .addOnSuccessListener(r -> {
                    callbackContext.success();
                })
                .addOnFailureListener(err -> {
                    err.getCause().printStackTrace();
                    callbackContext.error(err.getMessage());
                });
    }
}
