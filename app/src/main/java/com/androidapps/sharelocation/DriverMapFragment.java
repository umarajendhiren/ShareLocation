package com.androidapps.sharelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.BackgroundLocationUpdate.GoogleService;
import com.androidapps.sharelocation.databinding.DriverMapFragmentBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.Context.MODE_PRIVATE;

@AndroidEntryPoint
public class DriverMapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {
    private DriverListBottomSheetAdapter mAdapter;
    private RecyclerView recyclerView;
    private boolean isInfoWindowShown;
    public static MutableLiveData<String> userIdToCall = new MutableLiveData<>();
    public static MutableLiveData<Double> latitideLive = new MutableLiveData<>();
    public static MutableLiveData<Double> longitudeLive = new MutableLiveData<>();
    // onPhoneCallListener onPhoneCallListener;

    @Override
    public void onResume() {
        super.onResume();

        Log.d("onResume", "called");
        // updateMap(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude));
    }

    private static final int LOCATION_PERMIISION = 1;
    private static final int CALL_PERMIISION = 2;
    MainViewModel mainViewModel;
    Utilities utilities;
    RiderViewModel viewModel;

    Button doneSharing;
    TextView driverCode;
    String driverId;
    GoogleMap map;
    private SupportMapFragment mapFragment;
    private SharedPreferences sharedPrefLocation;
    String lastKnownLatitude, lastKnownLongitude;
    Location location;
    private MarkerOptions markerOption;
    private Marker marker;
    private BottomSheetBehavior bottomSheet;
    DriverMapFragmentBinding binding;
    ImageView addDriver;
    ImageView driverMessage,addBusStop;
    ImageView nearByTaxi;
    String isUserDriver;

    @Override
    public void onStart() {
        super.onStart();
        Log.d("driverMap", "start");
        sharedPrefLocation = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);
        sharedPrefLocation
                .registerOnSharedPreferenceChangeListener(this);

        //SharedPreferences sharedPref = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


        lastKnownLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");
        lastKnownLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");

        //  requestActivityPermission();


        viewModel.fragmentManager.setValue(getChildFragmentManager());

        viewModel.isSubsribeDForUserLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUser", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForUserLiveQuery();
                }

            }
        });
        //homePageViewModel.subscribeForCircleNameLiveQuery();
        viewModel.isSubsribeDForCircleNameLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCircle", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RiderViewModel.class);
        // Inflate the layout for this fragment
        //  View view = inflater.inflate(R.layout.driver_map_fragment, container, false);


        binding = DriverMapFragmentBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);

        SharedPreferences isDriver = getActivity().getSharedPreferences("isDriver", MODE_PRIVATE);
        isUserDriver = isDriver.getString("isUserDriver", "null");


//onPhoneCallListener=this::onPhoneCallClick;

        // viewModel.isDriverAvailable();


        if (isUserDriver.equals("true")) {

            //  binding.addDriver.setVisibility(View.GONE);

            binding.checkIn.setVisibility(View.VISIBLE);
            //binding.checkIn.setText("gg");
            binding.checkIn.setOnClickListener(this);
        }


        viewModel.isDriverAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDriverAvailable) {

                Log.d("onChanged:avai", String.valueOf(isDriverAvailable));

                if (isDriverAvailable) {
                    binding.checkIn.setText("Check out");

                }
                if (!isDriverAvailable) {
                    binding.checkIn.setText("Check in");

                }
            }
        });


        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.driver_map_fragment);

        mapFragment.getMapAsync(this);


        addDriver = binding.addDriver;
        addDriver.setOnClickListener(this);

        driverMessage=binding.message;
        driverMessage.setOnClickListener(this);


        nearByTaxi = binding.taxi;
        nearByTaxi.setOnClickListener(this);


        addBusStop=binding.AddBusStop;
        addBusStop.setOnClickListener(this);



        utilities = new Utilities();


        View BootomSheetLayout = binding.bottomNavigationContainer;

        bottomSheet = BottomSheetBehavior.from(BootomSheetLayout);

        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);


        recyclerView = binding.recyclerView;
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        // getLiveDriverList();


       /* viewModel.isSubsribeDForUserLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUser", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForUserLiveQuery();
                }

            }
        });
        //homePageViewModel.subscribeForCircleNameLiveQuery();
        viewModel.isSubsribeDForCircleNameLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCircle", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });*/

        View view = binding.getRoot();
        return view;
    }

    private void CheckIn() {

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {


                    boolean isDriverAvailable = object.getBoolean("isDriverAvailable");

                    Log.d("IsDriverAvailable", String.valueOf(isDriverAvailable));

                    if (isDriverAvailable) {

                        object.put("isDriverAvailable", false);
                        object.saveInBackground();
                        binding.checkIn.setText("Check in");
                    } else if (!isDriverAvailable) {
                        object.put("isDriverAvailable", true);
                        object.saveInBackground();
                        binding.checkIn.setText("Check out");

                    }
                }
            }
        });

    }

    private void getLiveDriverList() {

        viewModel.getDriverList().observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
            @Override
            public void onChanged(List<UserDetailsPojo> userDetailsPojoList) {

                if (userDetailsPojoList.size() > 0) {
                    Log.d("onChanged:reDriver", String.valueOf(userDetailsPojoList.size()));
                    Log.d("onChanged:username", userDetailsPojoList.get(0).getUserName());
                    Log.d("onChanged:userdis", String.valueOf(userDetailsPojoList.get(0).getDistanceInMiles()));

                    mAdapter = new DriverListBottomSheetAdapter(R.layout.driver_list, viewModel, new OnClickCallListener() {
                        @Override
                        public void onCall(String userId, double latitide, double longitude) {
                            Log.d("onCall", userId);

                            userIdToCall.setValue(userId);
                            latitideLive.setValue(latitide);
                            longitudeLive.setValue(longitude);

                            if (viewModel.startOrCallDriver.getValue().equals("StartDriving")) {
                                viewModel.startDriving(userIdToCall.getValue(), latitideLive.getValue(), longitudeLive.getValue());
                            }

                            if (viewModel.startOrCallDriver.getValue().equals("CallDriver")) {


                                checkCallPermissiionGranted();
                            }
                        }

                        @Override
                        public void onDisconnect(String userId) {

                            Log.d("onDisconnect: ", userId);
                            userIdToCall.setValue(userId);


                            if (viewModel.disConnectOrCallRider.getValue().equals("DisConnect")) {
                                Log.d("discoone: ", "called");
                                viewModel.disConnect(userIdToCall.getValue());

                            } else if (viewModel.disConnectOrCallRider.getValue().equals("CallRider")) {
                                checkCallPermissiionGranted();

                            }
                        }

                        @Override
                        public void onClickRouteName(StringToJsonSerialization routeDetails,int ObjectPosition) {

                        }
                    });
                    mAdapter.setDriverList(userDetailsPojoList);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.add_driver) {

            if (isUserDriver.equals("true")) {

                Log.d("onClick:", "show driver code");
                Intent startRiderActivity = new Intent(getActivity(), DriverActivity.class);
                startRiderActivity.putExtra("setFragment",1);
                startActivity(startRiderActivity);
            } else {
                Intent startRiderActivity = new Intent(getActivity(), RiderActivity.class);

                startActivity(startRiderActivity);
            }



        }


        if (view.getId() == R.id.message) {

            Intent startRiderActivity = new Intent(getActivity(), DriverActivity.class);
            startRiderActivity.putExtra("setFragment",2);
            startActivity(startRiderActivity);
        }

        if (view.getId() == R.id.check_in) {
            CheckIn();


        }


        if (view.getId() == R.id.taxi) {

            Intent NearbyTaxiActivity = new Intent(getActivity(), NearbyTaxi.class);

            startActivity(NearbyTaxiActivity);
        }


        if (view.getId() == R.id.Add_bus_stop) {

            Intent startAddPlacesActivity = new Intent(getActivity(), AddBusStopActivity.class);

           // startAddPlacesActivity.putExtra("Title","Add Bus Stop");

            startActivity(startAddPlacesActivity);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMapReady:", "called");

        map = googleMap;
        map.setMinZoomPreference(1);
        map.setMaxZoomPreference(20);
        map.setOnCameraIdleListener(this);
        map.setOnMarkerClickListener(this);
        if (lastKnownLatitude.equals("null") || lastKnownLongitude.equals("null")) {

            return;
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude)), 10));

            Location location = new Location("gps");
            location.setLongitude(Double.parseDouble(lastKnownLongitude));
            location.setLatitude(Double.parseDouble(lastKnownLatitude));


        }


        CheckLocationAccessPermission();
    }


    @SuppressLint("MissingPermission")
    private void CheckLocationAccessPermission() {

        if (Build.VERSION.SDK_INT < 23) {

            boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

            if (isServiceRunning) {
                Log.d("CheckLocation", "service already running");
                return;
            } else {
                Intent intent = new Intent(getActivity(), GoogleService.class);
                getActivity().startService(intent);
            }
        } else {
            //if don't have location access permission

            //  ContextCompt,this will make our app backward compatible to the current version.
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMIISION);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMIISION);

                }

            } else {
                Log.d("already have: ", "permission");
                boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

                if (isServiceRunning) {
                    // Log.d("CheckLocation", String.valueOf(location.getLatitude()));

                    updateMap(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude));

                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
                    updateMap(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude));
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
                boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");
                    updateMap(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude));

                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
                }


            }
        }

        if (requestCode == CALL_PERMIISION) {
            Log.d("onRequestPermission", String.valueOf(CALL_PERMIISION));
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (viewModel.startOrCallDriver.getValue().equals("CallDriver")) {

                    Log.d("call Driver", "number");

                    viewModel.callUser(userIdToCall.getValue());
                }
           /* else if(viewModel.startOrCallDriver.getValue().equals("StartDriving")){
                viewModel.startDriving(userIdToCall.getValue(),latitideLive.getValue(),longitudeLive.getValue());}*/

                else if (viewModel.disConnectOrCallRider.getValue().equals("CallRider")) {

                    Log.d("call rider", "number");

                    viewModel.callUser(userIdToCall.getValue());
                }

            } else {
                Toast.makeText(getActivity(), "Permission denied,could not make call!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void updateMap(double lastKnownLatitude, double lastKnownLongitude) {
        SharedPreferences isDriver = getActivity().getSharedPreferences("isDriver", MODE_PRIVATE);
        String isUserDriver = isDriver.getString("isUserDriver", "false");
        Log.d("isUserDriver: ", isUserDriver);
        if (isUserDriver.equals("true")) {
           /* Drawable driverMarker = getResources().getDrawable(R.drawable.ic_bus_marker);
            BitmapDescriptor markerIcon = getMarkerIconFromDrawable(driverMarker);

            LatLng latLng = new LatLng(lastKnownLatitude, lastKnownLongitude);
            markerOption = new MarkerOptions().position(latLng).icon(markerIcon);
            marker = map.addMarker(markerOption);*/

            //   viewModel.setCurrentUserAsDriver();

            setCurrentUserAsDriver();


        } else if (isUserDriver.equals("false")) {
            LatLng latLng = new LatLng(lastKnownLatitude, lastKnownLongitude);

            viewModel.addRiderMarker(map);
        }
    }

    public void setCurrentUserAsDriver() {

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                object.put("IsDriver", true);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            viewModel.addDriverMarker(map);

                        }
                    }
                });

                // AddDriverMarker(map);
            }
        });

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("onDetach: ", "ccalled");

        viewModel.setRiderDriverMapFalse();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        Location location = new Location("sharedpreference");
        if (s.equals("com.androidapps.sharelocation.Latitude")) {

            String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

            Log.d("onChangedlatitude: ", changedLatitude);


            // location.setLatitude(Double.parseDouble(changedLatitude));


            String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


            Log.d("onChangedlongitude: ", changedLongitude);
            //location.setLongitude(Double.parseDouble(changedLongitude));

            //  updateMap(Double.parseDouble(changedLatitude), Double.parseDouble(changedLongitude));

        }
    }


    @Override
    public void onCameraIdle() {

        Log.d("onCameraIdle", String.valueOf(map.getCameraPosition().zoom));
        // viewModel.setZoomLevelForMarker(map.getCameraPosition().zoom);


        getLiveDriverList();


     /*   if (map.getCameraPosition().zoom > 15) {
//when the zoom level increase we need so hybrid map instead of normal map when auto selected

            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } else if (map.getCameraPosition().zoom < 15) {

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }


    }*/

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("onMarkerClick", String.valueOf(marker.getTag()));
        if (!isInfoWindowShown) {
            Log.d("onMarkerClick:show ", String.valueOf(isInfoWindowShown));

            marker.showInfoWindow();

            isInfoWindowShown = true;
        } else {

            Log.d("onMarkerClick:hide ", String.valueOf(isInfoWindowShown));
            marker.hideInfoWindow();
            isInfoWindowShown = false;
        }


        return true;
    }


    public void checkCallPermissiionGranted() {

        Log.d("checkCallPermissiio:", "c");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMIISION);

        } else {

            //already have permiision

            Log.d("startOrCallDriver", viewModel.startOrCallDriver.getValue());
            Log.d("disConnectOrCallRider", viewModel.disConnectOrCallRider.getValue());
            if (viewModel.startOrCallDriver.getValue().equals("CallDriver")) {

                Log.d("call Driver", "number");

                viewModel.callUser(userIdToCall.getValue());
            }
           /* else if(viewModel.startOrCallDriver.getValue().equals("StartDriving")){
                viewModel.startDriving(userIdToCall.getValue(),latitideLive.getValue(),longitudeLive.getValue());}*/

            else if (viewModel.disConnectOrCallRider.getValue().equals("CallRider")) {

                Log.d("call rider", "number");

                viewModel.callUser(userIdToCall.getValue());
            }
           /* else if(viewModel.disConnectOrCallRider.getValue().equals("DisConnect"))
                Log.d("discoone: ","called");
                viewModel.disConnect(userIdToCall.getValue());
        }*/


        }
    }
}



