package com.androidapps.sharelocation.BackgroundLocationUpdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkConnectivityChangeBroadcastReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.d("onReceive: ","connectivity");

          /*  final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable() || mobile.isAvailable()) {
                // Do something

                Log.d("Network Available ", "Flag No 1");
            }
*/

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
           boolean connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

           if(connected){

               Log.d("onReceive: ","connected");

               SharedPreferences sharedPref = context.getSharedPreferences("Network", Context.MODE_PRIVATE);
              // String lastKnownLocation = sharedPref.getString("com.androidapps.sharelocation.network", "null");


                   SharedPreferences.Editor editor = sharedPref.edit();
                   editor.putString("com.androidapps.sharelocation.network", "connected");

                   editor.apply();

               }
           else if(!connected){
               Log.d("onReceive: ","notconnected");
               SharedPreferences sharedPref = context.getSharedPreferences("Network", Context.MODE_PRIVATE);
               // String lastKnownLocation = sharedPref.getString("com.androidapps.sharelocation.network", "null");


               SharedPreferences.Editor editor = sharedPref.edit();
               editor.putString("com.androidapps.sharelocation.network", "notconnected");

               editor.apply();
           }
        }
    }

