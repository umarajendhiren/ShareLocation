package com.androidapps.sharelocation.view;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.BackgroundLocationUpdate.LocationUpdateBroadcastReceiver;
import com.androidapps.sharelocation.BackgroundLocationUpdate.LocationUpdateService;
import com.androidapps.sharelocation.IdlingResourceForTest;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.databinding.FragmentAddHomeBinding;
import com.androidapps.sharelocation.utilities.Utilities;
import com.androidapps.sharelocation.viewmodel.MainViewModel;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@AndroidEntryPoint
public class AddHomMapFragment extends Fragment implements OnMapReadyCallback, View.OnFocusChangeListener, View.OnClickListener, GoogleMap.OnCameraIdleListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int LOCATION_PERMIISION = 789;
    GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String address;
    double selectedAddressLatitude, selectedAddressLongitude;
    SupportMapFragment mapFragment;
    Utilities utilities;
    static MainViewModel viewModel;

    FragmentAddHomeBinding viewBinding;
    IdlingResourceForTest idlingResourceForTest;
    LocationUpdateBroadcastReceiver receiver;
    SharedPreferences sharedPrefLocation;
    String lastKnownLatitude, lastKnownLongitude;

    Location location;

    @Override
    public void onStart() {
        super.onStart();
        // receiver=new LocationUpdateBroadcastReceiver();
        sharedPrefLocation = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);
        sharedPrefLocation
                .registerOnSharedPreferenceChangeListener(this);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


        lastKnownLatitude = sharedPref.getString("com.androidapps.sharelocation.Latitude", "null");
        lastKnownLongitude = sharedPref.getString("com.androidapps.sharelocation.Longitude", "null");

    }

    @Override
    public void onResume() {
        super.onResume();
        // CheckLocationAccessPermission();
        //viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (viewModel.getIdlingResourceInstance() != null) {
            idlingResourceForTest = viewModel.getIdlingResourceInstance();
            idlingResourceForTest.setIdleState(false);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewBinding = FragmentAddHomeBinding.inflate(inflater, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        viewBinding.save.setOnClickListener(this);

        viewBinding.editTextAddress.setOnFocusChangeListener(this);
        mapFragment.getMapAsync(this);


        viewModel.abTitle.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                viewBinding.editTextHome.setText(s);
            }
        });

        utilities = new Utilities();


       /* viewModel.onReceiveLocation.observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                Log.d("onChanged:location ", String.valueOf(location));
                UpdateMap(location);
            }
        });*/

        View rootView = viewBinding.getRoot();


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("handleOnBackPressed: ", "called");
                // Handle the back button event
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    Log.d("backstack:", String.valueOf(i));
                    fm.popBackStack();


                }

                SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = selectedFragment.edit();
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 10);

                editor.apply();


                Toast.makeText(getActivity(), "Press again to exit!", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return rootView;


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);

        // CheckLocationAccessPermission();

        //if activity permission is not granted launch location update service

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            Log.d("activitypermission", "denied");
            boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), LocationUpdateService.class);

            if (isServiceRunning) {
                Log.d("CheckLocation", "service already running");
                initialUpdate();

            } else {
                Intent intent = new Intent(getActivity(), LocationUpdateService.class);
                getActivity().startService(intent);
                initialUpdate();
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void CheckLocationAccessPermission() {

        if (Build.VERSION.SDK_INT < 23) {


            boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), LocationUpdateService.class);

            if (isServiceRunning) {
                Log.d("CheckLocation", "service already running");

                initialUpdate();

            } else {
                Intent intent = new Intent(getActivity(), LocationUpdateService.class);
                getActivity().startService(intent);
            }


        } else {
            //if don't have location access permission

            //  ContextCompt,this will make our app backward compatible to the current version.
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMIISION);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMIISION);

                }

            } else {
                Log.d("already have: ", "permission");
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    Log.d("activitypermission", "denied");
                    boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), LocationUpdateService.class);

                    if (isServiceRunning) {
                        Log.d("CheckLocation", "service already running");
                        initialUpdate();

                    } else {
                        Intent intent = new Intent(getActivity(), LocationUpdateService.class);
                        getActivity().startService(intent);
                    }
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
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    Log.d("activitypermission", "denied");
                    {
                        boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), LocationUpdateService.class);
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                            Log.d("activitypermission", "denied");
                            if (isServiceRunning) {
                                Log.d("CheckLocation", "service already running");
                                initialUpdate();

                            } else {
                                Intent intent = new Intent(getActivity(), LocationUpdateService.class);
                                getActivity().startService(intent);
                            }

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Log.d("onSharedChanged: ", s);
        SharedPreferences.Editor editor = sharedPrefLocation.edit();
        location = new Location("sharedpreference");
        if (s.equals("com.androidapps.sharelocation.Latitude")) {

            String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

            Log.d("onChangedlatitude: ", changedLatitude);


            location.setLatitude(Double.parseDouble(changedLatitude));


            String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


            Log.d("onChangedlongitude: ", changedLongitude);
            location.setLongitude(Double.parseDouble(changedLongitude));
            if (changedLatitude.equals("null") || changedLongitude.equals("null")) {
                return;
            }
            UpdateMap(location);


        }


    }


    public void UpdateMap(Location location) {
        if (location != null) {
            Log.i("UpdateMap: ", "Called");

            LatLng userLatlng = new LatLng(location.getLatitude(), location.getLongitude());


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng, 15));
            address = Utilities.getAddressFromLocation(getActivity(), location.getLatitude(), location.getLongitude());

            viewBinding.editTextAddress.setText(address);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        .build(getActivity().getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save) {


            SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


            SharedPreferences.Editor editor = selectedFragment.edit();
            editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 11);

            editor.apply();

            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String addressTitle = viewBinding.editTextHome.getText().toString();
if(addressTitle.isEmpty()) {
    Toast.makeText(getActivity(), "Please enter place name!", Toast.LENGTH_LONG).show();
    return;
}

            viewModel.savePlaceInServer(addressTitle, selectedAddressLatitude, selectedAddressLongitude);

            /*Intent startactivityIntent = new Intent(getActivity(), ActivityRecoganisation.class);
            startActivity(startactivityIntent);*/
            ((MainActivity) getActivity()).setFragmentForViewPager(11);
           /* Navigation.findNavController(view)
                    .navigate(R.id.action_addHomMapFragment_to_activityRecoganisation);*/



/*
            Intent startHomePageIntent = new Intent(getActivity(), HomaPageActivity.class);
            startActivity(startHomePageIntent);*/

        }
    }


    /*this callback is triggered after camera move stop*/
    @Override
    public void onCameraIdle() {
        Log.i("centerLat", String.valueOf(mMap.getCameraPosition().target.latitude));
        Log.i("centerLong", String.valueOf(mMap.getCameraPosition().target.longitude));
        selectedAddressLatitude = mMap.getCameraPosition().target.latitude;
        selectedAddressLongitude = mMap.getCameraPosition().target.longitude;
        address = Utilities.getAddressFromLocation(getActivity(), mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);

        viewBinding.editTextAddress.setText(address);
        if (idlingResourceForTest != null) {
            idlingResourceForTest.setIdleState(true);
        }

    }


    public void initialUpdate() {


        Location location = new Location("sharedpreference");
        String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

        Log.d("onChangedlatitude: ", changedLatitude);


        String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


        Log.d("onChangedlongitude: ", changedLongitude);


        if (changedLatitude.equals("null") || changedLongitude.equals("null")) {
            return;
        } else {
            location.setLatitude(Double.parseDouble(changedLatitude));
            location.setLongitude(Double.parseDouble(changedLongitude));
            UpdateMap(location);
        }
    }
}
