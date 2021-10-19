package com.androidapps.sharelocation.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidapps.sharelocation.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Utilities {
    static String uniqueId;
    static WindowManager windowManager;
    public static SharedPreferences sharedPrefLastKnownLocation;

    public static String getUniqueId() {
        uniqueId = UUID.randomUUID().toString();
        return uniqueId;
    }

    public static String getAddressFromLocation(Context context, double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Log.i("listAddress: ", addressList.get(0).toString());

                if (addressList.get(0).getSubThoroughfare() != null) {
                    address += addressList.get(0).getSubThoroughfare() + " ";
                }

                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare() + ",";
                }

                if (addressList.get(0).getLocality() != null) {
                    address += addressList.get(0).getLocality() + ",";
                }

                if (addressList.get(0).getPostalCode() != null) {
                    address += addressList.get(0).getCountryCode() + ",";
                }

                if (addressList.get(0).getPostalCode() != null) {
                    address += addressList.get(0).getPostalCode();
                }

                Log.d("getAddressFromLatLong: ", address);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }


    public static Bitmap getDrawableDpWithText(Context context, String firstLetter) {

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        View userDpWithTextLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.user_dp_with_text, null);

        TextView userFirstLetter = userDpWithTextLayout.findViewById(R.id.first_letter);

        userFirstLetter.setText(firstLetter);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        userDpWithTextLayout.setLayoutParams(new ViewGroup.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT));
        userDpWithTextLayout.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        userDpWithTextLayout.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        userDpWithTextLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(userDpWithTextLayout.getMeasuredWidth(), userDpWithTextLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        userDpWithTextLayout.draw(canvas);

        return bitmap;


    }

    public static Bitmap getDrawableDpWithBus(Context context) {

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        View userDpWithTextLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.driver_dp_with_bus, null);


        DisplayMetrics displayMetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        userDpWithTextLayout.setLayoutParams(new ViewGroup.LayoutParams(60, ViewGroup.LayoutParams.WRAP_CONTENT));
        userDpWithTextLayout.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        userDpWithTextLayout.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        userDpWithTextLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(userDpWithTextLayout.getMeasuredWidth(), userDpWithTextLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        userDpWithTextLayout.draw(canvas);

        return bitmap;


    }

    public static String getFirstName(String fullName) {
        if (fullName != null) {
            String[] nameArray = fullName.split("  ");

            return nameArray[0];
        } else return null;
    }

    public static String getLastName(String fullName) {
        if (fullName != null) {
        String[] nameArray = fullName.split("  ");

        return nameArray[1];
        } else return null;
    }


    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static SharedPreferences getLastKnownLocationShared(Context context) {

        sharedPrefLastKnownLocation = context.getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);
        return sharedPrefLastKnownLocation;
       /* lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        lasstKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");*/
    }
}
