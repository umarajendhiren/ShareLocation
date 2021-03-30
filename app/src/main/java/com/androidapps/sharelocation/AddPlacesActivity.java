package com.androidapps.sharelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.BackgroundLocationUpdate.GoogleService;
import com.androidapps.sharelocation.databinding.ActivityAddPlacesBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

import static com.androidapps.sharelocation.MainViewModel.idlingResourceForTest;

@AndroidEntryPoint
public class AddPlacesActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnFocusChangeListener, GoogleMap.OnCameraIdleListener, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int LOCATION_PERMIISION = 789;
    String lastKnownLongitude,lastKnownLatitude;
    SharedPreferences sharedPrefLocation;


    AddPlacesViewModel viewModel;
    private SupportMapFragment mapFragment;

    GoogleMap mMap;
    String postionToChange;
    String PositionToUpdateBustop;
    private LocationManager locationManager;
    LocationListener locationListener;
    private double selectedAddressLatitude;
    private double selectedAddressLongitude;
Utilities utilities;

    ActivityAddPlacesBinding viewBinding;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddPlacesViewModel.class);

        viewBinding = ActivityAddPlacesBinding.inflate(getLayoutInflater());
        View view = viewBinding.getRoot();
        setContentView(view);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);

        if (getIntent().getExtras().get("Title") != null) {

            ab.setTitle(getIntent().getExtras().get("Title").toString());

            viewModel.abTitle.setValue(ab.getTitle().toString());
        }

        if (getIntent().getExtras().get("Latitude") != null && getIntent().getExtras().get("Longitude") != null) {

            viewModel.latitude.setValue(getIntent().getExtras().get("Latitude").toString());
            viewModel.longitude.setValue(getIntent().getExtras().get("Longitude").toString());

            selectedAddressLatitude = Double.parseDouble(getIntent().getExtras().get("Latitude").toString());
            selectedAddressLongitude = Double.parseDouble(getIntent().getExtras().get("Longitude").toString());

            Log.d("selectedAddressLatitu", selectedAddressLatitude + "  " + selectedAddressLongitude);
        }

        if (getIntent().getExtras().get("Position") != null) {


            postionToChange = getIntent().getExtras().get("Position").toString();
            Log.d( "postionToChange ",postionToChange);

        }


        if (getIntent().getExtras().get("PositionToUpdateBustop") != null) {


            PositionToUpdateBustop= getIntent().getExtras().get("PositionToUpdateBustop").toString();
            Log.d( "PositionToUpdateBustop ",PositionToUpdateBustop);

        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (!Places.isInitialized()) {
            Places.initialize(this.getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        mapFragment.getMapAsync(this);

        viewBinding.editTextAddress.setOnFocusChangeListener(this);
        viewBinding.save.setOnClickListener(this);

        if (getIntent().getExtras().get("Title").toString().equals("Add Place")) {
            viewBinding.editTextHome.setHint("Enter Place Name");
        } else {
            viewBinding.editTextHome.setText(getIntent().getExtras().get("Title").toString());
        }
/*
        if (getIntent().getExtras().get("IsBusStop").toString().equals("true")) {
            viewBinding.save.setVisibility(View.GONE);
          //  mMap.getUiSettings().setScrollGesturesEnabled(false);

        } else {
            viewBinding.save.setVisibility(View.VISIBLE);
           // mMap.getUiSettings().setScrollGesturesEnabled(true);
        }*/

        utilities=new Utilities();
        CheckLocationAccessPermission();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);








        if (getIntent().getExtras().get("IsBusStop").toString().equals("true")) {
            viewBinding.save.setVisibility(View.GONE);
              mMap.getUiSettings().setScrollGesturesEnabled(false);

        } else {
            viewBinding.save.setVisibility(View.VISIBLE);
             mMap.getUiSettings().setScrollGesturesEnabled(true);
        }




        if (getIntent().getExtras().get("Latitude") != null && getIntent().getExtras().get("Longitude") != null) {
            Log.d("onMapReady:lat ",getIntent().getExtras().get("Latitude").toString());
            Log.d("onMapReady:lat ",getIntent().getExtras().get("Longitude").toString());
            LatLng latLng = new LatLng(selectedAddressLatitude, selectedAddressLongitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        } else {
            Location location = new Location("gps");
            location.setLongitude(Double.parseDouble(lastKnownLongitude));
            location.setLatitude(Double.parseDouble(lastKnownLatitude));
            UpdateMap(location);





        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("onstart", "onStart: shared");
        sharedPrefLocation = this.getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);
        sharedPrefLocation
                .registerOnSharedPreferenceChangeListener(this);

        SharedPreferences sharedPref = this.getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


        lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        lastKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");


    }


    @SuppressLint("MissingPermission")
    private void CheckLocationAccessPermission() {

        if (Build.VERSION.SDK_INT < 23) {


            boolean isServiceRunning = utilities.isMyServiceRunning(this, GoogleService.class);

            if (isServiceRunning) {
                Log.d("CheckLocation", "service already running");
                return;
            } else {
                Intent intent = new Intent(this, GoogleService.class);
                this.startService(intent);
            }


        } else {
            //if don't have location access permission

            //  ContextCompt,this will make our app backward compatible to the current version.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMIISION);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMIISION);

                }

            } else {
                Log.d("already have: ", "permission");
                boolean isServiceRunning = utilities.isMyServiceRunning(this,GoogleService.class);

                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");
                    return;
                } else {
                    Intent intent = new Intent(this, GoogleService.class);
                    this.startService(intent);
                }

            }


        }
    }


    //this callback called after the user tap allow or deny permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMIISION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted");
                boolean isServiceRunning = utilities.isMyServiceRunning(this,GoogleService.class);

                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");
                    return;
                } else {
                    Intent intent = new Intent(this, GoogleService.class);
                    this.startService(intent);
                }

            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Log.d("onSharedChanged: ", s);
        SharedPreferences.Editor editor = sharedPrefLocation.edit();
        Location location = new Location("sharedpreference");
        if (s.equals("com.androidapps.sharelocation.Latitude")) {

            String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

            Log.d("onChangedlatitude: ", changedLatitude);


            location.setLatitude(Double.parseDouble(changedLatitude));


            String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


            Log.d("onChangedlongitude: ", changedLongitude);
            location.setLongitude(Double.parseDouble(changedLongitude));


            UpdateMap(location);


        }


    }


    public void UpdateMap(Location location) {

        Log.i("UpdateMap: ", "Called");

        LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 15));
        address = Utilities.getAddressFromLocation(this, location.getLatitude(), location.getLongitude());

        viewBinding.editTextAddress.setText(address);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            viewBinding.editTextAddress.clearFocus();
            viewBinding.editTextHome.requestFocus(View.FOCUS_LEFT);
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.i("Place: ", place.getName() + ", " + place.getLatLng() + ", " + place.getAddress());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                String selectedPlace = String.valueOf(place.getAddress());

                viewBinding.editTextAddress.setText(selectedPlace);


                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.hasFocus()) {


            if (view.getId() == R.id.editText_address) {


                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this.getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save) {

           /* Log.d( "onClick:posiUpdate",postionToChange);
            Log.d( "posiUpdatebustop",PositionToUpdateBustop);*/

            Log.d( "onClick:posiUpdate",viewModel.abTitle.getValue());
            Log.d( "onClick:latit", String.valueOf(selectedAddressLatitude));
            Log.d( "onClick:longi", String.valueOf(selectedAddressLongitude));


            if (view.getId() == R.id.save) {
                String addressTitle = viewBinding.editTextHome.getText().toString();
                
                

                if(addressTitle.isEmpty()) {

                    Toast.makeText(this, "Please enter place name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (postionToChange == null  && PositionToUpdateBustop==null  && !viewModel.abTitle.getValue().equals("Add Bus Stop") ) {


                    Log.d("onClick: save",selectedAddressLatitude+"  "+selectedAddressLongitude);

                    //to save  new address for circle
                    viewModel.savePlaceInServer( addressTitle, selectedAddressLatitude, selectedAddressLongitude);
                    Intent intent = new Intent(this, HomaPageActivity.class);
                    startActivity(intent);


                } else if(postionToChange != null && PositionToUpdateBustop==null )  {

                    //to save edited,already existing address for circle
                    Log.d("onClick: saveee",selectedAddressLatitude+"  "+selectedAddressLongitude);
                    viewModel.updateAddress( addressTitle, selectedAddressLatitude, selectedAddressLongitude, postionToChange);
                    Intent intent = new Intent(this, HomaPageActivity.class);
                    startActivity(intent);


                }

                else if(PositionToUpdateBustop==null  && viewModel.abTitle.getValue().equals("Add Bus Stop")){

                    //add bus stop for driver

                    Log.d("onClick: Add bus",selectedAddressLatitude+" "+selectedAddressLongitude);

                    viewModel.addBusStop( addressTitle, selectedAddressLatitude, selectedAddressLongitude);
                    finish();
                }
                else if(postionToChange == null && PositionToUpdateBustop!=null){

                    //add bus stop for driver

                    Log.d("onClick: Add","updatebusStop");

                    viewModel.updateBusStopAddress( addressTitle, selectedAddressLatitude, selectedAddressLongitude, Integer.parseInt(PositionToUpdateBustop));
                    finish();
                }


            }

        }
    }


    /*this callback is triggered after camera move stop*/
    @Override
    public void onCameraIdle() {
       /* mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {*/
                Log.i("centerLat", String.valueOf(mMap.getCameraPosition().target.latitude));
                Log.i("centerLong", String.valueOf(mMap.getCameraPosition().target.longitude));
                selectedAddressLatitude = mMap.getCameraPosition().target.latitude;
                selectedAddressLongitude = mMap.getCameraPosition().target.longitude;
                address = Utilities.getAddressFromLocation(getApplicationContext(), mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);

                viewBinding.editTextAddress.setText(address);
                if (idlingResourceForTest != null) {
                    idlingResourceForTest.setIdleState(true);
                }



    }
}
