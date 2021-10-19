package com.androidapps.sharelocation.BackgroundLocationUpdate;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.androidapps.sharelocation.R;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class ActivityBroadCastReceiver extends BroadcastReceiver {




    MyLocationListener locationListener;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Handler mHandlerLocation = new Handler();
    private Timer mTimer = null;
    private static Timer mTimerLocation = null;
    long notify_interval = 10000;
    private static final String CHANNEL_ID = "channel_01";
    Intent intent;
    SharedPreferences sharedPrfActivity;
    private NotificationManager mNotificationManager;
    List<Boolean> isWatchingList=new ArrayList<>();

    JSONArray circleMemberArray;
    ArrayList<String> circleMembers=new ArrayList<>();
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive: ","called");
        mContext=context;


        if(intent.getAction().equals("TRANSITION_ACTION_RECEIVER")){

            Log.d( "onRec ","co");
        }

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // chronological sequence of events....

                Log.d("onReceive: ", String.valueOf(event.getActivityType()));

if(event.getActivityType()==DetectedActivity.STILL){
    Log.d( "Activity","Still");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","STILL");

//1000 milisecond is 1 sec.
    //when activity is still,need to get location one hour  once .else need to get location 10 sec once.
    //1000ms is 1 sec
    //60000ms is 1 min
    //3600000ms is 1 hour
    editor.apply();
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(),3600000);

}


else if(event.getActivityType()==DetectedActivity.IN_VEHICLE){

    Log.d( "Activity","vehicle");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","VEHICLE");


    editor.apply();
//if user is in vehicle need to update 10 sec once
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(), 10000);

}

else if(event.getActivityType()==DetectedActivity.ON_BICYCLE){

    Log.d( "Activity","vehicle");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","BICYCLE");

    editor.apply();
//if user is in bicycle need to update 1min once
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(), 60000);

}

else if(event.getActivityType()==DetectedActivity.ON_FOOT){
    Log.d( "Activity","vehicle");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","ONFOOT");

    editor.apply();
//if user is in on foot need to update 1 min once
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(), 60000);
}

else if(event.getActivityType()==DetectedActivity.RUNNING){

    Log.d( "Activity","vehicle");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","RUNNING");

    editor.apply();
//if user is running need to update 1 min once
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(), 60000);

}

else if(event.getActivityType()==DetectedActivity.WALKING)
{

    Log.d( "Activity","vehicle");
    SharedPreferences sharedPref = context.getSharedPreferences("UserActivity", MODE_PRIVATE);

    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("com.androidapps.sharelocation.useractivity","WALKING");

    editor.apply();
    //if user is walking need to update 1 min once
    mTimer = new Timer();
    mTimer.schedule(new TimerTaskToGetLocation(),60000);

}


                //updateUserActivity();
            }
        }


    }




   public  class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences sharedPref = mContext.getSharedPreferences("UserActivity", Context.MODE_PRIVATE);


                    String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

                    Log.d("userActivity", useractivity);

                    Toast.makeText(mContext, useractivity, Toast.LENGTH_LONG).show();



                    SharedPreferences network = mContext.getSharedPreferences("Network", Context.MODE_PRIVATE);
                    String networkStatus = network.getString("com.androidapps.sharelocation.network", "null");

                     if(networkStatus.equals("connected"))


                    // isGroupMemberWatching();
                    // fn_getlocation();
                    fn_getlocation();
                }
            });

        }
    }




    public  void fn_getlocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationListener = new MyLocationListener();


        SharedPreferences isInBackground = mContext.getSharedPreferences("isInBackground", MODE_PRIVATE);

        String InBackground = isInBackground.getString("Background", "null");
        Log.d("InBackground", InBackground);




        SharedPreferences sharedPref = mContext.getSharedPreferences("UserActivity", MODE_PRIVATE);


        String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

        Log.d("onSharedPreferencee", useractivity);







        // if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (InBackground.equals("false")) {

            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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


            } else if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
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
            }


            else
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            ){

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


        else  if (InBackground.equals("true")) {
            //if app is in  background ask permission
            Log.d("appInBackground", "true");
            if /*(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||*/
            (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.d("appInBackground", "Ask Permission");
                setLocationPermission("OFF");
                sendLocationRequestNotification();

            }
            else if /*(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&*/
            (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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



    public void fn_update (Location location){
        Log.d("fn_update", "called");
        SharedPreferences sharedPref = mContext.getSharedPreferences("LastKnownLocation", MODE_PRIVATE);


        String lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        String lasstKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");

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




    private void setLocationPermission(String locationPermission) {

        Log.d( "location",locationPermission);

        SharedPreferences sharedPref = mContext.getSharedPreferences("LocationPermission", MODE_PRIVATE);
        String lastKnownLocationPermission= sharedPref.getString("com.androidapps.sharelocation.locationpermission", "null");
        Log.d( "locationpermission",locationPermission);
        Log.d( "lastKnownLocationPerm",lastKnownLocationPermission);
        if(lastKnownLocationPermission.equals(locationPermission)){

            Log.d( "location","same dont update");
        }

        else{

            Log.d( "location","update");
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






    private void sendLocationRequestNotification () {
       /* Intent intent = new Intent(this, MyService.class);

        CharSequence text = Utils.getLocationText(mLocation);
        Log.d("getNotification: ", String.valueOf(mLocation));

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);*/

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activity = new Intent(mContext, ActivityBroadCastReceiver.class);
        activity.putExtra("isStartedFromNotification", true);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getService(mContext, 0,
                activity, 0);


        Intent intentSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",mContext.getPackageName(), null);
        intentSettings.setData(uri);
        // Set the Activity to start in a new, empty task
        /*intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                );*/
// Create the PendingIntent
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(
                mContext, 0, intentSettings, PendingIntent.FLAG_UPDATE_CURRENT
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .addAction(R.drawable.ic_2222,mContext.getString(R.string.change),
                        settingsPendingIntent)
                //.setTimeoutAfter(1000)
                .addAction(R.drawable.ic_2222, mContext.getString(R.string.close), activityPendingIntent)


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
                        .bigText(mContext.getString(R.string.permission_notification)));


        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID

        }

        mNotificationManager.notify(1, builder.build());


    }



    public void updateLocationPermission(String changedPermission){


        ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
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


    public void updateUserActivity(){


        ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {


                SharedPreferences sharedPref = mContext.getSharedPreferences("UserActivity", MODE_PRIVATE);


                String useractivity = sharedPref.getString("com.androidapps.sharelocation.useractivity", "null");

                Log.d("onSharedPreferencee", useractivity);



                if (object.get("UserActivity") != null) {

                    Log.d("UserActivity", "not null");

                    if (object.get("UserActivity").equals(useractivity)) {

                        Log.d("UserActivity", "same dont save");
                    } else {

                        object.put("UserActivity", useractivity);
                        object.saveInBackground();
                    }

                } else if (object.get("UserActivity") == null) {
                    Log.d("UserActivity", " null");
                    object.put("UserActivity", useractivity);
                    object.saveInBackground();
                }

            }
        });


    }
}
