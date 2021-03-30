package com.androidapps.sharelocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

public class StreetView extends AppCompatActivity {
    double latitude,longitude;
    LatLng userCurrentLocation;
String userName;
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomaPageActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

       /* SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetview_container);
        if (streetViewPanoramaFragment != null) {
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        }*/


        if (getIntent().getExtras() != null) {

          /* latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
             longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));*/

          Bundle bundle= getIntent().getExtras().getBundle("position");
          latitude=bundle.getDouble("latitude");
          longitude=bundle.getDouble("longitude");
          userName=bundle.getString("userName");
            Log.d( "streetView: ",latitude+" "+longitude);
             userCurrentLocation = new LatLng(latitude, longitude);




    StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            userCurrentLocation);

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                           getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.streetview_container,
                                            streetViewFragment)
                                    .addToBackStack(null).commit();


            Toast.makeText(this, userName+" is at this place!", Toast.LENGTH_SHORT).show();



        }


    }

 /*   @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(userCurrentLocation);
        // Set position with LaLng, radius and source.
       // streetViewPanorama.setPosition(userCurrentLocation, 20, StreetViewSource.OUTDOOR);
        // Keeping the zoom and tilt. Animate bearing by 60 degrees in 1000 milliseconds.
      *//*  long duration = 1000;
        StreetViewPanoramaCamera camera =
                new StreetViewPanoramaCamera.Builder()
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                        .bearing(streetViewPanorama.getPanoramaCamera().bearing - 60)
                        .build();
        streetViewPanorama.animateTo(camera, duration);*//*
    }*/
}