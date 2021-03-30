package com.androidapps.sharelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.BackgroundLocationUpdate.GoogleService;
import com.androidapps.sharelocation.databinding.FragmentPeopleBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PeopleFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener, SharedPreferences.OnSharedPreferenceChangeListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = PeopleFragment.class.getSimpleName();

    private GoogleMap mMap;


    private static final int LOCATION_PERMIISION = 789;


    private LiveData<List<UserDetailsPojo>> userDetailLiveData;

    private RecylerViewAdapter mAdapter;
    CircleNameDialogRecyclerView dialog;

    private HomePageViewModel homePageViewModel;
    private String SelectedMapType, preferenceCircleName, preferenceInviteCode;

    private RecyclerView recyclerView;
    private SupportMapFragment mapFragment;
    private AlertDialog.Builder builder;
    private Button groupNameButton;
    BottomSheetBehavior bottomSheet;
    private final List<String> preferenceValue = new ArrayList<>();
    FragmentPeopleBinding binding;
    String lastKnownLatitude, lastKnownLongitude;
    Utilities utilities;

    SharedPreferences sharedPrefSelectedCircle;
    SharedPreferences sharedPrefLocation;
    SharedPreferences sharedPrefLocationPermission;
    private boolean isInfoWindowShown;
    private PendingIntent mPendingIntent;
    private List<ActivityTransition> transitions = new ArrayList<>();
    List<String> selectedCircleInviteCode = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);

        binding = FragmentPeopleBinding.inflate(inflater, container, false);


        groupNameButton = binding.groupName;
        ImageView addPersonImage = binding.addPersonImage;
        binding.tvAddMember.setOnClickListener(this);
        binding.groupName.setOnClickListener(this);
        binding.addPersonImage.setOnClickListener(this);

        utilities = new Utilities();

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


        View rootLayout = binding.bottomNavigationContainer;

        bottomSheet = BottomSheetBehavior.from(rootLayout);

        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d("onCreateView: ", "null");

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        // if (mapFragment != null) {
        // mapFragment.getMapAsync(this);


        // }


        sharedPrefLocationPermission = getActivity().getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);
        String changedPermission = sharedPrefLocationPermission.getString("com.androidapps.sharelocation.locationpermission", "null");

        updateLocationPermission(changedPermission);

        View root = binding.getRoot();

        //  ObserveUserDetailLiveData();

        //  homePageViewModel.subscribeForUserLiveQuery();
        homePageViewModel.isSubsribeDForUserLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUser", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForUserLiveQuery();
                }

            }
        });
        //homePageViewModel.subscribeForCircleNameLiveQuery();
        homePageViewModel.isSubsribeDForCircleNameLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCircle", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });

        /*need to change this*/
        AdView adView = new AdView(getActivity());

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-8155753852159052/2560632852");

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

       /* homePageViewModel.subscribeForCircleNameLiveQuery();
        homePageViewModel.subscribeForUserLiveQuery();*/


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
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
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 12);

                editor.apply();


               /* SharedPreferences sharedPref = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString("com.androidapps.sharelocation.defaultCircleName", homePageViewModel.getSelectedGroupNameLiveData().getValue());
                edit.putString("com.androidapps.sharelocation.inviteCode", homePageViewModel.getSelectedInviteCodeLiveData().getValue());
                edit.apply();*/


                //Toast.makeText(getActivity(), "Press again to exit!", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);


                requireActivity().onBackPressed();
                // getActivity().finish();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);

        preferenceCircleName = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
        preferenceInviteCode = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");


        Log.d("onMapReadycir ", preferenceCircleName);

        homePageViewModel.getSelectedInviteCodeLiveData().setValue(preferenceInviteCode);
        homePageViewModel.getSelectedGroupNameLiveData().setValue(preferenceCircleName);





        homePageViewModel.getSelectedGroupNameLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

selectedCircleInviteCode.clear();

                if (s != null) {


                    Log.d("onChangedsele: ", s);

                    if (s.equals("defaultCircleName"))
                        groupNameButton.setText("Create New Group");

                    else
                        groupNameButton.setText(s);


                    selectedCircleInviteCode.add(homePageViewModel.getSelectedGroupNameLiveData().getValue());
                    selectedCircleInviteCode.add(homePageViewModel.getSelectedInviteCodeLiveData().getValue());


                    SharedPreferences sharedPref = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("com.androidapps.sharelocation.defaultCircleName", s);
                    editor.putString("com.androidapps.sharelocation.inviteCode", homePageViewModel.getSelectedInviteCodeLiveData().getValue());
                    editor.apply();


                    Log.d("onChanged:cirin",homePageViewModel.getSelectedGroupNameLiveData().getValue()+" "+homePageViewModel.getSelectedInviteCodeLiveData().getValue());


                    if (mMap != null) {
                        homePageViewModel.UpdateMapWithMarker(getContext(), mMap, selectedCircleInviteCode);
                    }



                } else if (s == null) {
                    groupNameButton.setText("Create New Group");


                    selectedCircleInviteCode.clear();
                    selectedCircleInviteCode.add(null);
                    selectedCircleInviteCode.add(null);

                    if (mMap != null) {
                        homePageViewModel.UpdateMapWithMarker(getContext(), mMap, selectedCircleInviteCode);
                    }

                }
            }

        });


        sharedPrefLocation = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


        lastKnownLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");
        lastKnownLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");

        if (mMap != null && !lastKnownLatitude.equals("null") || !lastKnownLongitude.equals("null")) {

            Location location = new Location("sharedpreference");


            //  String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

            Log.d("onChangedlatefir: ", lastKnownLatitude);


            location.setLatitude(Double.parseDouble(lastKnownLatitude));


            //  String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


            Log.d("onChangedlongitfirs: ", lastKnownLongitude);
            location.setLongitude(Double.parseDouble(lastKnownLongitude));


            // homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);
            homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);

        }
       /* Log.d("onMapReady: ", lastKnownLatitude);
        if(mMap!=null && !lastKnownLatitude.equals("null") || !lastKnownLongitude.equals("null")) {



            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude)), 10));

            Location location = new Location("gps");
            location.setLongitude(Double.parseDouble(lastKnownLongitude));
            location.setLatitude(Double.parseDouble(lastKnownLatitude));


             homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);*/

        // homePageViewModel.getDistance();
/*
if(mMap!=null) {


    if (preferenceCircleName.equals("defaultCircleName"))
        groupNameButton.setText("Create New Group");

    else
        groupNameButton.setText(preferenceCircleName);

    preferenceValue.clear();
    preferenceValue.add(preferenceCircleName);
    preferenceValue.add(preferenceInviteCode);

    //need to wait for seconds,because UpdateMapWithMarker uses value from server.but above method store the value in server. onEvent() call back called after this method executed.so got error.

    new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    // your code here
                    Log.d("run: ", "waiting for serverrr, notify update");

                    Log.d("run: ", preferenceValue.get(0));
                    Log.d("run: ", preferenceValue.get(1));

                    homePageViewModel.UpdateMapWithMarker(getActivity(), mMap, preferenceValue);
                }
            },
            2000
    );
}
*/
        return root;
    }


    //for update bottom sheet
    private void ObserveUserDetailLiveData() {

        homePageViewModel.getGroupMemberList().observe(getViewLifecycleOwner(), new Observer<List<UserDetailsPojo>>() {
            @Override
            public void onChanged(List<UserDetailsPojo> userDetailsPojoList) {
                Log.d("onChanged:re ", String.valueOf(userDetailsPojoList.size()));
                //  Log.d("onChanged:re 0", String.valueOf(userDetailsPojoList.get(0).getUserName()));
                //Log.d("onChanged:re 1", String.valueOf(userDetailsPojoList.get(0).getUserName()));

                mAdapter = new RecylerViewAdapter(userDetailsPojoList, homePageViewModel);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: shared");

        sharedPrefLocation
                .registerOnSharedPreferenceChangeListener(this);

        sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);
        sharedPrefSelectedCircle.registerOnSharedPreferenceChangeListener(this);

        sharedPrefLocationPermission = getActivity().getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);
        sharedPrefLocationPermission.registerOnSharedPreferenceChangeListener(this);
        //SharedPreferences sharedPref = getActivity().getSharedPreferences("LastKnownLocation", Context.MODE_PRIVATE);


        /*lastKnownLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");
        lastKnownLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");*/

        //  requestActivityPermission();


        homePageViewModel.isSubsribeDForUserLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUserstart", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForUserLiveQuery();
                }

            }
        });
        //homePageViewModel.subscribeForCircleNameLiveQuery();
        homePageViewModel.isSubsribeDForCircleNameLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCirclesta", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });


        if (mapFragment != null) {
            mapFragment.getMapAsync(this);


        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMarkerClickListener(this);

        mMap.setMinZoomPreference(1);
        mMap.setMaxZoomPreference(20);
        Log.d("onMapClick: ", String.valueOf(mMap.getCameraPosition()));

        setInfoWindowClickToPanorama(mMap);

        CheckLocationAccessPermission();


        // mMap.setBuildingsEnabled(true);

        Log.d("onMapReady: ", lastKnownLatitude);

        if (!lastKnownLatitude.equals("null") || !lastKnownLongitude.equals("null")) {


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude)), 10));
        }
      /*  if (lastKnownLatitude.equals("null") || lastKnownLongitude.equals("null")) {

            return;
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lastKnownLatitude), Double.parseDouble(lastKnownLongitude)), 10));

            Location location = new Location("gps");
            location.setLongitude(Double.parseDouble(lastKnownLongitude));
            location.setLatitude(Double.parseDouble(lastKnownLatitude));


           // homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);
        }*/

/*

        sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);

        preferenceCircleName = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
        preferenceInviteCode = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");


        Log.d("onMapReadycir ", preferenceCircleName);
        Log.d("onMapReadycir ", preferenceInviteCode);


//if shared preference value is null,default value will be used
        if (preferenceCircleName.equals("defaultCircleName"))
            groupNameButton.setText("Create New Group");

        else
            groupNameButton.setText(preferenceCircleName);

        preferenceValue.clear();
        preferenceValue.add(preferenceCircleName);
        preferenceValue.add(preferenceInviteCode);
*/

        //need to wait for seconds,because UpdateMapWithMarker uses value from server.but above method store the value in server. onEvent() call back called after this method executed.so got error.
    /*    preferenceValue.clear();
        preferenceValue.add(homePageViewModel.getSelectedGroupNameLiveData().getValue());
        preferenceValue.add(homePageViewModel.getSelectedInviteCodeLiveData().getValue());*/

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                        Log.d("run: ", "waiting for serverrr, notify update");

                        Log.d( "run: ",selectedCircleInviteCode.get(0));
                        Log.d( "run: ",selectedCircleInviteCode.get(1));


                        if (mMap != null) {
                            homePageViewModel.UpdateMapWithMarker(getContext(), mMap, selectedCircleInviteCode);
                        }
                    }
                },
                2000
        );
        //for update map
       /* homePageViewModel.getSelectedGroupNameLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {


                List<String> selectedCircleInviteCode = new ArrayList<>();
                if (s != null) {
                    Log.d("onChanged: ", s);
                    groupNameButton.setText(s);

                    selectedCircleInviteCode.add(homePageViewModel.getSelectedGroupNameLiveData().getValue());
                    selectedCircleInviteCode.add(homePageViewModel.getSelectedInviteCodeLiveData().getValue());

                    homePageViewModel.UpdateMapWithMarker(getContext(), mMap, selectedCircleInviteCode);


                    SharedPreferences sharedPref = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("com.androidapps.sharelocation.defaultCircleName", s);
                    editor.putString("com.androidapps.sharelocation.inviteCode", homePageViewModel.getSelectedInviteCodeLiveData().getValue());
                    editor.apply();
                } else if (s == null) {
                    groupNameButton.setText("Create New Group");


                    selectedCircleInviteCode.clear();
                    selectedCircleInviteCode.add(null);
                    selectedCircleInviteCode.add(null);


                    homePageViewModel.UpdateMapWithMarker(getContext(), mMap, selectedCircleInviteCode);

                }
            }

        });*/


    }

    private void requestActivityPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            Log.d("onCreate: ", "check");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d("onCreate: ", "request");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 123);
            } else {
                requestActivityUpdate();
            }
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            requestActivityUpdate();

        }
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMIISION);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMIISION);

                }

            } else {
                Log.d("already have: ", "permission");
                boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");
                    return;
                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
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
                    return;
                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
                }


            }
        }

        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted");

                requestActivityUpdate();
            }

        }


    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.group_name) {
            dialog = new CircleNameDialogRecyclerView();

            dialog.show(getChildFragmentManager(), "dialog");


        }

        if (view.getId() == R.id.add_person_image) {

            if (groupNameButton.getText().equals("Create New Group")) {
                Toast.makeText(getActivity(), "Create Group To Share Invite Code!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent addPersonIntent = new Intent(getActivity(), CreateAndJoinCircleActivity.class);
            addPersonIntent.putExtra("addPerson", "Add");
            startActivity(addPersonIntent);
        }

        if (view.getId() == R.id.tv_add_member) {
            if (groupNameButton.getText().equals("Create New Group")) {
                Toast.makeText(getActivity(), "Create Group To Share Invite Code!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent addPersonIntent = new Intent(getActivity(), CreateAndJoinCircleActivity.class);
            addPersonIntent.putExtra("addPerson", "Add");
            startActivity(addPersonIntent);

        }
    }


    @Override
    public void onMapClick(LatLng latLng) {


        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        if (mMap.getCameraPosition().zoom > 15) {
//when the zoom level increase we need so hybrid map instead of normal map when auto selected

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } else if (mMap.getCameraPosition().zoom < 15) {

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

    }


    @Override
    public void onCameraMove() {

        //homePageViewModel.setZoomLevelForMarker(mMap.getCameraPosition().zoom);
    }

    @Override
    public void onCameraIdle() {

        homePageViewModel.setZoomLevelForMarker(mMap.getCameraPosition().zoom);

        // userDetailLiveData = homePageViewModel.getGroupMemberList();
        ObserveUserDetailLiveData();


        if (mMap.getCameraPosition().zoom > 15) {
//when the zoom level increase we need so hybrid map instead of normal map when auto selected

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        } else if (mMap.getCameraPosition().zoom < 15) {

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        Log.d("onSharedChanged: ", s);

   /*     if (s.equals("com.androidapps.sharelocation.inviteCode")|| s.equals("com.androidapps.sharelocation.defaultCircleName")) {

            if (homePageViewModel != null && preferenceValue.size() > 0)
            {


                sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);

                preferenceCircleName = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
                preferenceInviteCode = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");

                if (preferenceCircleName.equals("defaultCircleName") || preferenceInviteCode.equals("defaultInviteCode"))
                    groupNameButton.setText("Create New Group");

                else
                    groupNameButton.setText(preferenceCircleName);


                preferenceValue.add(preferenceCircleName);
                preferenceValue.add(preferenceInviteCode);

                Log.d("onResumepre", preferenceCircleName);
                homePageViewModel.UpdateMapWithMarker(getActivity(), mMap, preferenceValue);
            }


        }*/
     /*   homePageViewModel.isSubsribeDForUserLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUser", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForUserLiveQuery();
                }

            }
        });

        homePageViewModel.isSubsribeDForCircleNameLiveQuery().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCircle", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    homePageViewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });
*/
       /* homePageViewModel.subscribeForUserLiveQuery();
        homePageViewModel.subscribeForCircleNameLiveQuery();*/
        SharedPreferences.Editor editor = sharedPrefLocation.edit();
        Location location = new Location("sharedpreference");
        if (s.equals("com.androidapps.sharelocation.Latitude")) {

            String changedLatitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Latitude", "null");

            Log.d("onChangedlatitude: ", changedLatitude);


            location.setLatitude(Double.parseDouble(changedLatitude));


            String changedLongitude = sharedPrefLocation.getString("com.androidapps.sharelocation.Longitude", "null");


            Log.d("onChangedlongitude: ", changedLongitude);
            location.setLongitude(Double.parseDouble(changedLongitude));


            // homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);
            homePageViewModel.storeUserCurrentLocationInServer(getContext(), location, mMap);


        } else if (s.equals("com.androidapps.sharelocation.locationpermission")) {

            //set location permission

            String changedPermission = sharedPrefLocationPermission.getString("com.androidapps.sharelocation.locationpermission", "null");

            // updateLocationPermission(changedPermission);

        }





/*else  if(s.equals("com.androidapps.sharelocation.defaultCircleName")) {
            if (homePageViewModel != null && preferenceValue.size() > 0) {
                preferenceValue.clear();

               // sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);

                preferenceCircleName = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
                preferenceInviteCode = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");

                if (preferenceCircleName.equals("defaultCircleName"))
                    groupNameButton.setText("Create New Group");

                else
                    groupNameButton.setText(preferenceCircleName);


                preferenceValue.add(preferenceCircleName);
                preferenceValue.add(preferenceInviteCode);

                Log.d("onResumepre", preferenceValue.get(0));
                homePageViewModel.UpdateMapWithMarker(getActivity(), mMap, preferenceValue);
            }
        }*/

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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume:b", "vsdfg");

       /* if (homePageViewModel != null && preferenceValue.size() > 0) {


            sharedPrefSelectedCircle = getActivity().getSharedPreferences("circle", Context.MODE_PRIVATE);

            preferenceCircleName = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.defaultCircleName", "defaultCircleName");
            preferenceInviteCode = sharedPrefSelectedCircle.getString("com.androidapps.sharelocation.inviteCode", "defaultInviteCode");

            if (preferenceCircleName.equals("defaultCircleName"))
                groupNameButton.setText("Create New Group");

            else
                groupNameButton.setText(preferenceCircleName);


            preferenceValue.add(preferenceCircleName);
            preferenceValue.add(preferenceInviteCode);

            Log.d("onResumepre", preferenceCircleName);
            homePageViewModel.UpdateMapWithMarker(getActivity(), mMap, preferenceValue);
        }*/

    }

    private void setInfoWindowClickToPanorama(GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        // Check the tag
                        // if (marker.getTag() == "poi") {


                        Intent streetViewIntent = new Intent(getActivity(), StreetView.class);

                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitude", marker.getPosition().latitude);
                        bundle.putDouble("longitude", marker.getPosition().longitude);
                        bundle.putString("userName", marker.getTitle());

                        streetViewIntent.putExtra("position", bundle);

                        startActivity(streetViewIntent);
                        // Set the position to the position of the marker
                          /*  StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            marker.getPosition());

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.map,
                                            streetViewFragment)
                                    .addToBackStack(null).commit();*/
                    }

                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("onMarkerClick: ", marker.getTitle());
        //String userAddress=Utilities.getAddressFromLocation(getActivity(),marker.getPosition().latitude,marker.getPosition().longitude);

        // marker.setSnippet(userAddress);


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


    public void requestActivityUpdate() {

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());


        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_FOOT)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
/*
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.UNKNOWN)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.UNKNOWN)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());*/
       /* transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.TILTING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());*/

        Log.d("onCreate: ", "called");
        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Task<Void> task = ActivityRecognition.getClient(getActivity())
                //. requestActivityUpdates(1000,getPendingIntent());

                .requestActivityTransitionUpdates(request, getPendingIntent());

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success
                        Log.d("onSuccess: ", "requestSuccess!");


                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle error
                        Log.d("error: ", e.getMessage());
                    }
                }
        );
    }

    private PendingIntent getPendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (mPendingIntent != null) {
            return mPendingIntent;
        }
        Intent intent = new Intent(getActivity(), ActivityBroadCastReceiver.class);
        intent.setAction("TRANSITION_ACTION_RECEIVER");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }


    public void onBackPressed() {

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
    }


}

