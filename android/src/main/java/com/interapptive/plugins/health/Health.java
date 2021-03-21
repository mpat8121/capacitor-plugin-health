package com.interapptive.plugins.health;

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
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * Returns google fit data to plugin call
     * @param call The Current Capacitor Plugin Call to return data to
     */
    public final void accessGoogleFitData(PluginCall call) {

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
                                if (dataSource != null) {
                                    String sourceBundleId = dataSource.getAppPackageName();
                                    if (sourceBundleId != null) obj.put("sourceBundleId", sourceBundleId);
                                }

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
     * @return
     * @throws ParseException
     */
    public DataReadResponse query(JSObject data, DataType dt) throws ParseException {
        String st = data.getString("startDate");
        String et = data.getString("endDate");
        final String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

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
