package com.interapptive.plugins.health;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getcapacitor.JSObject;

import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

public class Health {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private final Context context;
    private final FitnessOptions fitnessOptions;
    private final String tag = "---- IA HEALTH PLUGIN";

    // Google account to access the API
//    GoogleSignInAccount account;

    public Health(Context context, FitnessOptions fitnessOptions) {
        this.context = context;
        this.fitnessOptions = fitnessOptions;
    }

    /**
     * Detects if:
     * a) Google Play Services APIs are available,
     * b) Google Fit is installed
     * @return true if Google Fit is installed
     */
    public Boolean isAvailable() {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int apiResult = gApi.isGooglePlayServicesAvailable(context);
        if(apiResult == ConnectionResult.SUCCESS) {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo("com.google.android.apps.fitness", PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager .NameNotFoundException e) {
                Log.e(tag, "Google Fit not installed");
                return false;
            }
        } else {
            String errorCode = "";
            if(gApi.isUserResolvableError(apiResult)) {
                errorCode = gApi.getErrorString(apiResult);
            }
            Log.e(tag, "Google Services not installed or obsolete, code: " + errorCode);
            return false;
        }
    }

    /**
     *   5. After the user has authorized access to the data requested, create a fitness client (for
     *      example, a HistoryClient to read and/or write historic fitness data) based on your app's
     *      purpose and needs:
     */
    public final void accessGoogleFit(PluginCall call) {

        Calendar cal = Calendar.getInstance ();
        cal.setTime (new Date ());
        long endTime = cal.getTimeInMillis ();
        cal.add (Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis ();
        DataReadRequest readRequest = (new DataReadRequest.Builder())
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        final GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    // Use response data here
                    Log.d(tag, "OnSuccess()");

                    if(!call.getCallbackId().isEmpty()) {
                        JSObject ret = new JSObject();
                        ret.put("result", true);
                        ret.put("message", "OnSuccess()");
                        call.resolve(ret);
                    }
                })
                .addOnFailureListener(e->{
                    Log.d(tag, "OnFailure()", e);
                    if(!call.getCallbackId().isEmpty()) {
                        JSObject ret = new JSObject();
                        ret.put("result", false);
                        ret.put("message", e.getMessage());
                        call.resolve(ret);
                    }
                });
    }

    /**
     *
     * @param data
     * @param dt
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    public DataReadResponse query(JSObject data, DataType dt) throws JSONException, ParseException {
        String st = data.getString("startDate");
        String et = data.getString("endDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date startDate = sdf.parse(st);
        long sDate = startDate.getTime();
        Date endDate = sdf.parse(et);
        long eDate = endDate.getTime();

        String sourceBundleId = context.getApplicationContext().getPackageName();
        if (data.has("sourceBundleId")) {
            sourceBundleId = data.getString("sourceBundleId");
        }
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(dt)
                .setTimeRange(sDate, eDate, TimeUnit.MILLISECONDS)
                .build();
        Task<DataReadResponse> dataReadResponse = Fitness.getHistoryClient(context, account)
                .readData(readRequest);

        return dataReadResponse.getResult();
    }

    /**
     *
     * @param data
     * @param dt
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    public Boolean store(JSObject data, DataType dt) throws JSONException, ParseException {

        String st = data.getString("startDate");
        String et = data.getString("endDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date startDate = sdf.parse(st);
        long sDate = startDate.getTime();
        Date endDate = sdf.parse(et);
        long eDate = endDate.getTime();

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
        dataPointBuilder.setTimeInterval(sDate, eDate, TimeUnit.MILLISECONDS);

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

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        Task<Void> insertStatus = Fitness.getHistoryClient(context, account)
                .insertData(dataSetBuilder.build());
        return insertStatus.isComplete();

    }
}
