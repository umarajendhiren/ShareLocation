package com.androidapps.sharelocation.BackgroundLocationUpdate;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.Utilities;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleService extends Service {
    MyLocationListener locationListener;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Handler mHandlerLocation = new Handler();
    private Timer mTimer = null;
    static String lasstKnownLongitude;
    private static Timer mTimerLocation = null;
    long notify_interval = 10000;
    private static final String CHANNEL_ID = "channel_01";
    Intent intent;
    SharedPreferences sharedPrfActivity;
    private NotificationManager mNotificationManager;
    List<Boolean> isWatchingList = new ArrayList<>();

    static SharedPreferences sharedPref;
    static String lastKnownLatitude;

    JSONArray circleMemberArray;
    ArrayList<String> circleMembers = new ArrayList<>();

    public GoogleService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            boolean startedFromNotification = intent.getBooleanExtra("isStartedFromNotification",
                    false);

            Log.d("onStartCommand: ", String.valueOf(startedFromNotification));
            if (startedFromNotification) {

                mNotificationManager.cancel(1);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //mTimerLocation = new Timer();
        /*sharedPref   = getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);

        lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        lasstKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");*/
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);


        intent = new Intent();


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.location_permission);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mChannel.setSound(null, null);
            mChannel.setShowBadge(false);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        /* sharedPrfActivity = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);
        sharedPrfActivity
                .registerOnSharedPreferenceChangeListener(this);*/

    }


    public void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationListener = new MyLocationListener();


        SharedPreferences isInBackground = getSharedPreferences("isInBackground", MODE_PRIVATE);

        String InBackground = isInBackground.getString("Background", "null");
        Log.d("InBackground", InBackground);


        SharedPreferences sharedPref = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


        String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

        Log.d("onSharedPreferencee", useractivity);


        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (InBackground.equals("false")) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {
                //Log.d("appInBackground", "Ask Permission");  // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Log.d("fn_getlocation: ", "foreground off");
                setLocationPermission("OFF");


            } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {

                setLocationPermission("OFF");
                Log.d("fn_getlocation: ", "foreground on,back off");


                if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }

                if (isGPSEnable) {
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.e("latitudeGps", location.getLatitude() + "");
                            Log.e("longitudeGps", location.getLongitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }
                } else if (isNetworkEnable) {
                    location = null;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            Log.e("latitudenetwork", location.getLatitude() + "");
                            Log.e("longitudenetwork", location.getLongitude() + "");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }

                }
            } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ) {

                setLocationPermission("ON");
                Log.d("fn_getlocation: ", "foreground on,back on");


                if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }


                if (isGPSEnable) {
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.e("latitudeGps", location.getLatitude() + "");
                            Log.e("longitudeGps", location.getLongitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }
                } else if (isNetworkEnable) {
                    location = null;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            Log.e("latitudenetwork", location.getLatitude() + "");
                            Log.e("longitudenetwork", location.getLongitude() + "");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }

                }
            }
        }



       /* if (!isGPSEnable && !isNetworkEnable) {

        }*/ //if {


        else if (InBackground.equals("true")) {
            //if app is in  background ask permission
            Log.d("appInBackground", "true");
            if /*(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||*/
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("appInBackground", "Ask Permission");
                setLocationPermission("OFF");
                sendLocationRequestNotification();

            } else if /*(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&*/
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Log.d("appInBackground", "have Permission");
                setLocationPermission("ON");


                if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }

                if (isGPSEnable) {
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.e("latitudeGps", location.getLatitude() + "");
                            Log.e("longitudeGps", location.getLongitude() + "");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }
                } else if (isNetworkEnable) {
                    location = null;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {

                            Log.e("latitudenetwork", location.getLatitude() + "");
                            Log.e("longitudenetwork", location.getLongitude() + "");

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            fn_update(location);
                        }
                    }

                }


            }

            // ((StarterApplication)this.getApplicationContext()).onAppBackgrounded();


          /*  else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                Log.d("appInBackground", "Ask background Permission");
                setLocationPermission("OFF");
                sendLocationRequestNotification();

            }*/
        }


    }


    private void setLocationPermission(String locationPermission) {

        Log.d("location", locationPermission);

        SharedPreferences sharedPref = getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);
        String lastKnownLocationPermission = sharedPref.getString("com.androidapps.sharelocation.locationpermission", "null");
        Log.d("locationpermission", locationPermission);
        Log.d("lastKnownLocationPerm", lastKnownLocationPermission);
        if (lastKnownLocationPermission.equals(locationPermission)) {

            Log.d("location", "same dont update");
        } else {

            Log.d("location", "update");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.locationpermission", locationPermission);
            editor.apply();
            updateLocationPermission(locationPermission);


        }


    /*    ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                Log.d( "done:per",locationPermission);
                if(object.get("LocationPermission")!=null){

                    Log.d( "location","not null");

                    if(object.get("LocationPermission").equals(locationPermission))
                    {

                        Log.d( "locationpermission","same dont save");
                    }
                    else {

                        object.put("LocationPermission",locationPermission);
                        object.saveInBackground();
                    }

                }

                else if(object.get("LocationPermission")==null){
                    Log.d( "location"," null");
                    object.put("LocationPermission",locationPermission);
                    object.saveInBackground();
                }

            }
        });*/
    }

    public void updateLocationPermission(String changedPermission) {


        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                Log.d("done:per", changedPermission);
                if (object.get("LocationPermission") != null) {

                    Log.d("location", "not null");

                    if (object.get("LocationPermission").equals(changedPermission)) {

                        Log.d("locationpermission", "same dont save");
                    } else {

                        object.put("LocationPermission", changedPermission);
                        object.saveInBackground();
                    }

                } else if (object.get("LocationPermission") == null) {
                    Log.d("location", " null");
                    object.put("LocationPermission", changedPermission);
                    object.saveInBackground();
                }

            }
        });


    }

   /* @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("onSharedPrefer:asc ", s);


        sharedPrfActivity = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


        String useractivity = sharedPrfActivity.getString("com.androidapps.sharelocation.useractivity", "null");

        if (useractivity.equals("STILL")) {

            Log.d("onSharedPreference", "still");
            return;
        } else {

            Log.d("onSharedPreference", "getting location");
            fn_getlocation();
        }

    }*/

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPref = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


                    String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

                    Log.d("onSharedPreferencee", useractivity);

                     /*   if (useractivity.equals("STILL")) {
                            Log.d("onSharedPreferencee", "still");

                            mTimer.
                            return;
                        }*/


                    Log.d("onSharedPreferencee", "getting location");

                    // isGroupMemberWatching();
                    fn_getlocation();
                }
            });

        }
    }

    private class TimerTaskToUpdateLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("TimerUpdateLocation", "called");

                    fn_getlocation();

                }
            });
        }
    }


    public void isGroupMemberWatching() {

        ParseQuery<ParseObject> circleNameQuery = new ParseQuery<ParseObject>("CircleName");
        List<String> currentUserId = new ArrayList<>();
        currentUserId.add(ParseUser.getCurrentUser().getObjectId());
        circleNameQuery.whereContainedIn("circleMember", currentUserId);
        circleNameQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> circleNameObjects, ParseException e) {
                if (e == null && circleNameObjects.size() > 0) {
                    Log.d("currentUserInCircle", String.valueOf(circleNameObjects.size()));
                    circleMembers.clear();
                    isWatchingList.clear();
                    for (int i = 0; i < circleNameObjects.size(); i++) {

                        circleMemberArray = circleNameObjects.get(i).getJSONArray("circleMember");

                        for (int j = 0; j < circleMemberArray.length(); j++) {
                            try {
                                Log.d("done:circleMemberid", String.valueOf(circleMemberArray.get(j)));

                                if (!circleMembers.contains(String.valueOf(circleMemberArray.get(j)))) {
                                    circleMembers.add(String.valueOf(circleMemberArray.get(j)));

                                    isSomeOneWatching(String.valueOf(circleMemberArray.get(j)));
                                } else {
                                    Log.d("done:already ", String.valueOf(circleMemberArray.get(j)));
                                }


                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private void isSomeOneWatching(String parseUserId) {

        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.getInBackground(parseUserId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {

                boolean isWatching = user.getBoolean("isWatching");

                //   if(isWatching){
                Log.d("done:watcing", parseUserId);
                //fn_getlocation();
                isWatchingList.add(isWatching);
                // }

                Log.d("done:lenghth", String.valueOf(circleMembers.size()));
                Log.d("done:isWatchingList", String.valueOf(isWatchingList.size()));

                if (circleMembers.size() == isWatchingList.size()) {
                    Log.d("done:lenghthequal", String.valueOf(circleMembers.size()));
                    if (isWatchingList.contains(true)) {

                        fn_getlocation();

/*if(mTimerLocation==null){
    Log.d( "done:null","called");
    mTimerLocation = new Timer();
    mTimerLocation.schedule(new TimerTaskToUpdateLocation(), 2, notify_interval);
    Log.d( "done:contains",mTimerLocation.toString());
}
else{

   *//* mTimerLocation = new Timer();
    mTimerLocation.schedule(new TimerTaskToUpdateLocation(), 2, notify_interval);*//*
    Log.d("done:alrady",mTimerLocation.toString());

       // fn_getlocation();
    }}
    else {


        if(mTimerLocation!=null){
            Log.d( "done:stop",mTimerLocation.toString());
        mTimerLocation.cancel();}
    }*/
                    }

                }
            }
        });
    }


    public void fn_update(Location location) {
        Log.d("fn_update", "called");


        sharedPref = Utilities.getLastKnownLocationShared(this);
        lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        lasstKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");

        if (lastKnownLatitude.equals("null") || lasstKnownLongitude.equals("null")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.Latitude", String.valueOf(location.getLatitude()));
            editor.putString("com.androidapps.sharelocation.Longitude", String.valueOf(location.getLongitude()));
            editor.apply();
            return;
        }

        double lastKnownlatitude = Double.parseDouble(lastKnownLatitude);
        double lastKnownLongitude = Double.parseDouble(lasstKnownLongitude);
        ParseGeoPoint lastknownGeoPoint = new ParseGeoPoint(lastKnownlatitude, lastKnownLongitude);

        ParseGeoPoint changedLatitude = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        double distance = changedLatitude.distanceInMilesTo(lastknownGeoPoint);
        Log.d("distance", String.valueOf(distance));
        //0.1 mile 528 feet
        if (changedLatitude.distanceInMilesTo(lastknownGeoPoint) > 0.01) {
            Log.d("fn_updateedit", "called");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.Latitude", String.valueOf(location.getLatitude()));
            editor.putString("com.androidapps.sharelocation.Longitude", String.valueOf(location.getLongitude()));
            editor.apply();

        } else {
            Log.d("locationSame: ", "Dont Save  In Server!");

        }

    /*    Log.d("lastKnownLatitude ", lastKnownLatitude);
        Log.d("lasstKnownLongitude ", lasstKnownLongitude);
        Log.d("changedlatitude: ", String.valueOf(location.getLatitude()));
        Log.d("changedlongitude: ", String.valueOf(location.getLongitude()));


        if (lastKnownLatitude.equals(String.valueOf(location.getLatitude())) && lasstKnownLongitude.equals(String.valueOf(location.getLongitude()))) {
            Log.d("locationSame: ", "Dont Save  In Server!");
        } else {
            Log.d("locationdifferent: ", " Save  In Server!");

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.Latitude", String.valueOf(location.getLatitude()));
            editor.putString("com.androidapps.sharelocation.Longitude", String.valueOf(location.getLongitude()));
            editor.apply();


        }*/
    }


    public static class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("Location changed", String.valueOf(loc));


          /*  SharedPreferences sharedPref = getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


            String lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
            String lasstKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");

            Log.d("lastKnownLatitude ", lastKnownLatitude);
            Log.d("lasstKnownLongitude ", lasstKnownLongitude);
            Log.d("changedlatitude: ", String.valueOf(latitude));
            Log.d("changedlongitude: ", String.valueOf(longitude));


            if (lastKnownLatitude.equals(String.valueOf(latitude)) && lasstKnownLongitude.equals(String.valueOf(longitude))) {
                Log.d("locationSame: ", "Dont Save  In Server!");
            } else {
                Log.d("locationdifferent: ", " Save  In Server!");

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("com.androidapps.sharelocation.Latitude", String.valueOf(latitude));
                editor.putString("com.androidapps.sharelocation.Longitude", String.valueOf(longitude));
                editor.apply();


            }*/

        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

    }

    private void ShowRequestPermissionRationale(String title, String message) {



/*You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them.

                        "Since background location access has not been granted, this app will not be able to discover beacons in the background.
                        Please go to Settings -> Applications -> Permissions and grant background location access to this app.*/

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setNeutralButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //from version 29 we need to ask backround locaion access permission

                            if (ContextCompat.checkSelfPermission(GoogleService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.d("versionGreaterThan29", String.valueOf(Build.VERSION.SDK_INT));
                                ActivityCompat.requestPermissions(GoogleService.this, permission,requestCode);
                            }
                        }*/
                    }
                })

                .setCancelable(false)
                .create()
                .show();
    }


    private void sendLocationRequestNotification() {
       /* Intent intent = new Intent(this, MyService.class);

        CharSequence text = Utils.getLocationText(mLocation);
        Log.d("getNotification: ", String.valueOf(mLocation));

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);*/

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activity = new Intent(this, GoogleService.class);
        activity.putExtra("isStartedFromNotification", true);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getService(this, 0,
                activity, 0);


        Intent intentSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intentSettings.setData(uri);
        // Set the Activity to start in a new, empty task
        /*intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                );*/
// Create the PendingIntent
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                GoogleService.this, 0, intentSettings, PendingIntent.FLAG_UPDATE_CURRENT
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_2222, getString(R.string.change),
                        settingsPendingIntent)
                //.setTimeoutAfter(1000)
                .addAction(R.drawable.ic_2222, getString(R.string.close), activityPendingIntent)


                /*    .addAction(R.id.change_name, "Change",
                            servicePendingIntent)*/
                //.setContentText("ShareLocation only works correctly if it can access your location all the time /n To update please follow these steps:/n 1.In settings,go to ShareLocation /n 2.Tap into locations permissions /n 3.Select Allow all the time")
                .setContentTitle("Allow all the time in location permission")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_2222)
                .setContentIntent(settingsPendingIntent)
                .setAutoCancel(true)
                // .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.permission_notification)));


        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID

        }

        mNotificationManager.notify(1, builder.build());


    }


}