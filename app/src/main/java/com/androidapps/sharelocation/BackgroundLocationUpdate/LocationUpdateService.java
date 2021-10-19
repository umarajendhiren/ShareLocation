package com.androidapps.sharelocation.BackgroundLocationUpdate;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.utilities.Utilities;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

/*This service class gets location all the time even if the app is in background
and set updated location in main server to share updated location with other circle member.*/

public class LocationUpdateService extends Service {
    MyLocationListener locationListener;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    private static final String CHANNEL_ID = "channel_01";
    Intent intent;
    private NotificationManager mNotificationManager;
    static SharedPreferences sharedPref;
    static String lastKnownLatitude, lasstKnownLongitude;

    /*If the user click on notification, onStartCommand method will be called .
     * app intention is if user click on notification ,need to close all notification under this app.
     * so we can do that inside this onStartCommand method. */


    //if user allow activity permission ,no need to launch this service .
    // when user moves ,timer autimatically adjust based on user activity.
    //if they denied activity permission we need to launch this service to get location using timer.

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
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);
        intent = new Intent();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*To send notification,notification manager requires notification channel after oreo version.[Android O]*/


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


    }


    public void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationListener = new MyLocationListener();


        SharedPreferences isInBackground = getSharedPreferences("isInBackground", MODE_PRIVATE);

        String InBackground = isInBackground.getString("Background", "null");
        Log.d("InBackground", InBackground);


       /* SharedPreferences sharedPref = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


        String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

        Log.d("onSharedPreferencee", useractivity);*/

        /* If the app is foreground but the forground location permission is off(fine and course location),
        need to set location permission off in server to let other people know.  */

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


            }


       /* If the app is in foreground but foreground location permission is ON(fine and course location) and background location permission is OFF
        need to set location permission off in server to let other people know. because app can get location till user using app.once they exit app can not get location */

            else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ) {

                setLocationPermission("OFF");
                Log.d("fn_getlocation: ", "foreground on,back off");


                /*if user not moving around when using app,App should not get location because it will consume more battery power .so App only get user's location when user activity not STILL*/

               /* if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }*/


                /*If user location is keep changing when using app,App need to set updated location in server
                 *requestLocationUpdates()  will get location using wifi(GPS_PROVIDER) or network with interval of 1 second*/

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

             /* If the app is in foreground and if foreground location permission is ON(fine and course location) and background location permission is ON
        need to set location permission ON in server to let other people know.  */


            else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ) {

                setLocationPermission("ON");
                Log.d("fn_getlocation: ", "foreground on,back on");

                /*if user not moving around when using app or in background,App should not get location because it will consume more battery power*/
             /*   if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }*/
                /*If user location is keep changing when using app,App need to set updated location in server
                 *requestLocationUpdates()  will get location using wifi(GPS_PROVIDER) or network with interval of 1 second*/


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

            /* If the app is in background  but if background location permission is OFF
        need to set location permission off in server to let other people know.
         And need to send "Enable Background Location Permission notification" to user. */


        else if (InBackground.equals("true")) {
            //if app is in  background ask permission
            Log.d("appInBackground", "true");
            if
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("appInBackground", "Ask Permission");
                setLocationPermission("OFF");
                sendLocationRequestNotification();

            }

             /* If the app is in background  and also background location permission is ON
        need to set location permission ON in server to let other people know. */

            else if
            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Log.d("appInBackground", "have Permission");
                setLocationPermission("ON");


               /* if (useractivity.equals("STILL")) {
                    Log.d("onSharedPreferencee", "still");
                    return;
                }*/
                /*If user location is keep changing when app is in background,App need to set updated location in server
                 *requestLocationUpdates()  will get location using wifi(GPS_PROVIDER) or network with interval of 1 second*/

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


    }


    /*Set changed location permission in Server*/
    private void setLocationPermission(String locationPermission) {

        Log.d("location", locationPermission);

        SharedPreferences sharedPref = getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);
        String lastKnownLocationPermission = sharedPref.getString("com.androidapps.sharelocation.locationpermission", "null");
        if (lastKnownLocationPermission.equals(locationPermission)) {

            Log.d("same", "already in prefernce, dont update");
        } else {

            Log.d("different", "update preference");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.locationpermission", locationPermission);
            editor.apply();
            updateLocationPermission(locationPermission);


        }


    }

    public void updateLocationPermission(String changedPermission) {


        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (object.get("LocationPermission") != null) {


                    if (object.get("LocationPermission").equals(changedPermission)) {

                        Log.d("locationpermission", "same dont save");
                    } else {

                        object.put("LocationPermission", changedPermission);
                        object.saveInBackground();
                    }

                } else if (object.get("LocationPermission") == null) {
                    object.put("LocationPermission", changedPermission);
                    object.saveInBackground();
                }

            }
        });


    }


    /*This timer task will keep calling   fn_getlocation to know about location in interval of 5 seconds */
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPref = getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


                    String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

                    Log.d("onSharedPreferencee", useractivity);


                    fn_getlocation();
                }
            });

        }
    }
    /*
     */


    /*This method will set changed location latitude,longitude in sharedPreference.
     * To do all server related work in one place and to get clean code using SharedPreference.
     *  set changed location permission in SharedPreference will  notify HomePageActivity to do server related code.*/
    public void fn_update(Location location) {
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

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.Latitude", String.valueOf(location.getLatitude()));
            editor.putString("com.androidapps.sharelocation.Longitude", String.valueOf(location.getLongitude()));
            editor.apply();

        } else {
            Log.d("locationSame: ", "Dont Save  In Server!");

        }


    }


    /*MyLlocationListener will get location details when location changed*/
    public static class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("Location changed", String.valueOf(loc));


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


    /*This method will send "Enable location service" notification to user using notification manager,if the location permission is OFF*/
    private void sendLocationRequestNotification() {


        Intent activity = new Intent(this, LocationUpdateService.class);
        activity.putExtra("isStartedFromNotification", true);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getService(this, 0,
                activity, 0);


        Intent intentSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intentSettings.setData(uri);

// Create the PendingIntent
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                LocationUpdateService.this, 0, intentSettings, PendingIntent.FLAG_UPDATE_CURRENT
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_2222, getString(R.string.change),
                        settingsPendingIntent)

                .addAction(R.drawable.ic_2222, getString(R.string.close), activityPendingIntent)


                .setContentTitle("Allow all the time in location permission")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.ic_2222)
                .setContentIntent(settingsPendingIntent)
                .setAutoCancel(true)
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