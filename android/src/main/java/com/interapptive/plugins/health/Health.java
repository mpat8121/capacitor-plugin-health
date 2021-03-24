package com.interapptive.plugins.health;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import com.getcapacitor.PluginCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class Health {

    private final Context context;
    private final FitnessOptions fitnessOptions;
    private final String tag = "---- IA HEALTH PLUGIN";

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
    public boolean isAvailable() {
        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int apiResult = gApi.isGooglePlayServicesAvailable(context);
        if(apiResult == ConnectionResult.SUCCESS) {
            PackageManager pm = context.getPackageManager();
            try {
                // try get packageInfo - throw exception if not found
               pm.getPackageInfo(
                        "com.google.android.apps.fitness",
                        PackageManager.GET_ACTIVITIES
                );
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
     * Returns google fit data to plugin call
     * @param call The Current Capacitor Plugin Call to return data to
     */
    public void accessGoogleFitData(PluginCall call) {
        int limit = 1000;
        if(call.getData().has("limit")) {
            limit = 75;
        }

        Calendar cal = Calendar.getInstance ();
        cal.setTime (new Date ());
        long endTime = cal.getTimeInMillis ();
        cal.add (Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis ();
        // Option for future implementation
        // .read(DataType.TYPE_HEIGHT)
        DataReadRequest readRequest = (new DataReadRequest.Builder())
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(limit)
                .build();

        final GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(
                context,
                fitnessOptions
        );

        Fitness.getHistoryClient(context, account)
                .readData(readRequest)
                .addOnSuccessListener(response -> {

                    Log.d(tag, "Successfully connected to Google Fit API");

                    if(!call.getCallbackId().isEmpty()) {
                        // Use response data here
                        JSObject resultSets = new JSObject();
                        List<DataSet> dataSets = response.getDataSets();

                        for (DataSet dataset : dataSets) {
                            JSArray resultSet = new JSArray();

                            final DataType dt = dataset.getDataType();

                            for (DataPoint datapoint : dataset.getDataPoints()) {
                                JSObject obj = new JSObject();

                                long start = datapoint.getStartTime(TimeUnit.MILLISECONDS);
                                long end = datapoint.getEndTime(TimeUnit.MILLISECONDS);
                                obj.put("startDate", start);
                                obj.put("endDate", end);

                                DataSource dataSource = datapoint.getOriginalDataSource();
                                String sourceBundleId = dataSource.getAppPackageName();
                                if (sourceBundleId != null) obj.put("sourceBundleId", sourceBundleId);

                                //reference for fields:
                                // https://developers.google.com/android/reference/com/google/android/gms/fitness/data/Field.html
                                if (dt.equals(DataType.TYPE_HEIGHT)) {
                                    float height = datapoint.getValue(Field.FIELD_HEIGHT).asFloat();
                                    obj.put("value", height);
                                    obj.put("unit", "m");
                                } else if (dt.equals(DataType.TYPE_WEIGHT)) {
                                    float weight = datapoint.getValue(Field.FIELD_WEIGHT).asFloat();
                                    obj.put("value", weight);
                                    obj.put("unit", "kg");
                                } else if (dt.equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                                    float weight = datapoint.getValue(Field.FIELD_PERCENTAGE).asFloat();
                                    obj.put("value", weight);
                                    obj.put("unit", "percent");
                                }
                                resultSet.put(obj);
                            }
                            if (dt.equals(DataType.TYPE_HEIGHT)) {
                                resultSets.put("height", resultSet);
                            } else if (dt.equals(DataType.TYPE_WEIGHT)) {
                                resultSets.put("weight", resultSet);
                            } else if (dt.equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                                resultSets.put("fat_percentage", resultSet);
                            }
                        }

                        JSObject ret = new JSObject();
                        ret.put("success", true);
                        ret.put("message", "Successfully connected to Google Fit API");
                        ret.put("data", resultSets);
                        call.resolve(ret);
                    }
                })
                .addOnFailureListener(e->{
                    Log.d(tag, "OnFailure()", e);
                    if(!call.getCallbackId().isEmpty()) {
                        JSObject ret = new JSObject();
                        ret.put("success", false);
                        ret.put("message", e.getMessage());
                        call.resolve(ret);
                    }
                });
    }

    /**
     * Read Google Fit Data
     * @param data Data read params
     * @param dt Data Type
     * @return Result of dataReadResponse
     * @throws ParseException exception from date parse
     */
    public DataReadResponse query(JSObject data, DataType dt) throws ParseException {
        String st = data.getString("startDate");
        if(st == null) st = "";
        String et = data.getString("endDate");
        if(et == null) et = "";
        final String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Date startDate = sdf.parse(st);
        assert startDate != null;
        long sDate = startDate.getTime();
        Date endDate = sdf.parse(et);
        assert endDate != null;
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

        if(account != null) {
            Task<DataReadResponse> dataReadResponse = Fitness.getHistoryClient(context, account)
                    .readData(readRequest);
            return dataReadResponse.getResult();
        } else {
            return null;
        }
    }

    /**
     * Sends data to Google Fit
     * @param data data to store
     * @param dt DataType
     * @throws ParseException exception from date parse
     */
    public void store(JSObject data, DataType dt, PluginCall call) throws ParseException {

        String st = data.getString("startDate");
        if(st == null) st = "";
        String et = data.getString("endDate");
        if(et == null) et = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date startDate = sdf.parse(st);
        assert startDate != null;
        long sDate = startDate.getTime();
        Date endDate = sdf.parse(et);
        assert endDate != null;
        long eDate = endDate.getTime();

        String sourceBundleId = context.getApplicationContext().getPackageName();

        DataSource dataSrc = new DataSource.Builder()
                .setAppPackageName(sourceBundleId)
                .setDataType(dt)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet.Builder dataSetBuilder = DataSet.builder(dataSrc);
        DataPoint.Builder dataPointBuilder = DataPoint.builder(dataSrc);
        dataPointBuilder.setTimeInterval(sDate, eDate, TimeUnit.MILLISECONDS);

        String value = data.getString("value");
        if(value != null) {
            if (dt.equals(DataType.TYPE_HEIGHT)) {
                float height = Float.parseFloat(value);
                dataPointBuilder.setField(Field.FIELD_HEIGHT, height);
            } else if (dt.equals(DataType.TYPE_WEIGHT)) {
                float weight = Float.parseFloat(value);
                dataPointBuilder.setField(Field.FIELD_WEIGHT, weight);
            } else if (dt.equals(DataType.TYPE_BODY_FAT_PERCENTAGE)) {
                float percentage = Float.parseFloat(value);
                dataPointBuilder.setField(Field.FIELD_PERCENTAGE, percentage);
            }
            dataSetBuilder.add(dataPointBuilder.build());

            final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

            if(account != null) {
                Fitness.getHistoryClient(context, account)
                        .insertData(dataSetBuilder.build())
                        .addOnCompleteListener(task -> {
                            JSObject ret = new JSObject();
                            if(task.isSuccessful()) {
                                ret.put("message", "Data sent successfully");
                            } else {
                                ret.put("message", task.getException());
                            }
                            ret.put("success", task.isSuccessful());
                            call.resolve(ret);
                        });
            }
        }
    }
}
