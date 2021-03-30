package com.androidapps.sharelocation.BackgroundLocationUpdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationUpdateBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Broadcast Listened", "called");
        if (intent.getAction().equals("location")) {
            double latitude = Double.valueOf(intent.getStringExtra("latutide"));
            double longitude = Double.valueOf(intent.getStringExtra("longitude"));
            Log.d("onReceive: ", String.valueOf(latitude));


         /*   public  static  class LocationUpdateBroadcastReceiver extends BroadcastReceiver {
                // The BroadcastReceiver used to listen from broadcasts from the service.

                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.i("Broadcast Listened", "called");
           *//* if (intent.getAction().equals("location")) {
                double latitude = Double.valueOf(intent.getStringExtra("latutide"));
                double longitude = Double.valueOf(intent.getStringExtra("longitude"));
                Location location = new Location("gps");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                Log.d("onReceive: ", String.valueOf(latitude));
                SharedPreferences sharedPref = context.getSharedPreferences("CurrentLocation", Context.MODE_PRIVATE);
                String lastKnownLocation = sharedPref.getString("com.androidapps.sharelocation.currentlocation", "null");

                if (lastKnownLocation.equals(String.valueOf(location))) {

                    Log.d("onReceive: ", "sameLocation");
                } else {
                    Log.d("onReceive: ", "different");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("com.androidapps.sharelocation.currentlocation", String.valueOf(location));

                    editor.apply();

                    viewModel.onReceiveLocation.setValue(location);

                }
            }*//*

                }

          *//*  SharedPreferences sharedPref = context.getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


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

               // storeLocationInServer();
            }

        }*//*
        }*/
        }
    }
}
