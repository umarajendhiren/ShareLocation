package com.androidapps.sharelocation;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlacesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    RecyclerView recyclerView;
    AddPlacesRecylerViewAdapter adapter;
    HomePageViewModel homePageViewModel;
    TextView circleName;

    GeofencingClient geofencingClient;

    List<String> listOfPlaces = new ArrayList<>();
    TextView home, school, work, grocery, addPlace;
    List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;

    @Override
    public void onStart() {
        super.onStart();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_places, container, false);


        geofencingClient = LocationServices.getGeofencingClient(getActivity());


        home = root.findViewById(R.id.home);
        work = root.findViewById(R.id.work);
        school = root.findViewById(R.id.school);
        grocery = root.findViewById(R.id.grocery);
        addPlace = root.findViewById(R.id.add_place);
        circleName = root.findViewById(R.id.group_name);
        List<StringToJsonSerialization> geofenceListLive = new ArrayList<>();
        List<String> geofenceToUnSubscribe= new ArrayList<>();

        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
       // homePageViewModel.getAllGeoFences();

       /* if (homePageViewModel.getGeoFenceLive()!= null) {

            Log.d("onCreateView: ","called");

                homePageViewModel.getGeoFenceLive().observe(getViewLifecycleOwner(), new Observer<List<StringToJsonSerialization>>() {
                    @Override
                    public void onChanged(List<StringToJsonSerialization> geoFenceList) {
                        geofenceListLive.clear();

                        for (StringToJsonSerialization geoFence : geoFenceList) {
                            geofenceListLive.add(geoFence);
                        }

                        Log.d("onChanged:geo", String.valueOf(geofenceListLive.size()));

                        buildGeoFence(geofenceListLive);

                    }
                });
            }*/


homePageViewModel.getRemovedGeoFence().observe(getViewLifecycleOwner(), new Observer<String>() {
    @Override
    public void onChanged(String removedGeoFence) {
        Log.d("onChanged:removedGeo",removedGeoFence);

        geofenceToUnSubscribe.clear();
        geofenceToUnSubscribe.add(removedGeoFence);
        unRegisterGeoFences(geofenceToUnSubscribe);


    }
});


if( homePageViewModel.getSelectedGroupNameLiveData().getValue()==null  || homePageViewModel.getSelectedGroupNameLiveData().getValue().equals("defaultCircleName")   ){

    circleName.setVisibility(View.GONE);
}

else{

    circleName.setVisibility(View.VISIBLE);
    circleName.setText(homePageViewModel.getSelectedGroupNameLiveData().getValue() + " Places");
    //homePageViewModel.getSavedPlaces();
}



/*homePageViewModel.getSavedPlaces().observe(getViewLifecycleOwner(), new Observer<List<StringToJsonSerialization>>() {
    @Override
    public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

            Log.d("onChangedplaces:","called");


        }
    });*/


     /*   homePageViewModel.getCircleNameLive().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {


                if (s != null) {
                    Log.d("onChangedplaces:",s);
                    homePageViewModel.getSavedPlaces();
                    circleName.setText(s + " Places");
                }

                else{

                    //if no group clear places list
                   *//* listOfPlaces.clear();
                    geofenceListLive.clear();
                    homePageViewModel.geofenceList.clear();*//*

               homePageViewModel.setPlaceLive();

                   *//* adapter = new AddPlacesRecylerViewAdapter(null, getContext(), homePageViewModel);
                    recyclerView.setAdapter(adapter);*//*
                }


            }
        });*/

        home.setOnClickListener(this);
        work.setOnClickListener(this);
        school.setOnClickListener(this);
        grocery.setOnClickListener(this);
        addPlace.setOnClickListener(this);


        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        homePageViewModel.getPlacesLive().observe(getViewLifecycleOwner(), new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

                if (stringToJsonSerializations != null) {
                    Log.d("onChanged:savedPlaces ", "called");

                    listOfPlaces.clear();
                    geofenceListLive.clear();
                    homePageViewModel.geofenceList.clear();
                    for (int i = 0; i < stringToJsonSerializations.size(); i++) {

                        listOfPlaces.add(stringToJsonSerializations.get(i).getPlaceName());
                        Log.d( "onChanged:list", String.valueOf(listOfPlaces.size()));
                        geofenceListLive.add(stringToJsonSerializations.get(i));


                    }

                    buildGeoFence(geofenceListLive);


                    homePageViewModel.checkHomeAvailabe(listOfPlaces).observe(getViewLifecycleOwner(), new Observer<ListViewAddPlaceVisibilityPojo>() {
                        @Override
                        public void onChanged(ListViewAddPlaceVisibilityPojo listViewAddPlaceVisibilityPojo) {

                            Log.d("onChanged:home","called");
                            if (listViewAddPlaceVisibilityPojo.isHomeAvailable) {
                                home.setVisibility(View.GONE);
                            } else home.setVisibility(View.VISIBLE);


                            if (listViewAddPlaceVisibilityPojo.isSchoolAvailable) {
                                school.setVisibility(View.GONE);
                            } else school.setVisibility(View.VISIBLE);


                            if (listViewAddPlaceVisibilityPojo.isGroceryAvailable) {
                                grocery.setVisibility(View.GONE);
                            } else grocery.setVisibility(View.VISIBLE);

                            if (listViewAddPlaceVisibilityPojo.isWorkAvailable) {
                                work.setVisibility(View.GONE);
                            } else work.setVisibility(View.VISIBLE);

                        }

                    });
                } else {

                    work.setVisibility(View.VISIBLE);
                    school.setVisibility(View.VISIBLE);
                    home.setVisibility(View.VISIBLE);
                    grocery.setVisibility(View.VISIBLE);

                }
                adapter = new AddPlacesRecylerViewAdapter(stringToJsonSerializations, getContext(), homePageViewModel);
                recyclerView.setAdapter(adapter);




            }


        });


        return root;
    }

    private void buildGeoFence(List<StringToJsonSerialization> geoFenceList) {
        geofenceList.clear();
        for (int i = 0; i < geoFenceList.size(); i++) {
String circleNameLive=homePageViewModel.getCircleNameLive().getValue();
String inviteCodeLive=homePageViewModel.getInviteCodeLive().getValue();

            String requestKey = null;

                requestKey = geoFenceList.get(i).getPlaceName()+ " " +circleNameLive+ " "+ inviteCodeLive;

            double latitude = geoFenceList.get(i).getGeoPoint().getLatitude();
            double longitude = geoFenceList.get(i).getGeoPoint().getLongitude();

            Log.d("buildGeoFence: ", requestKey);
            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(requestKey)

                    .setCircularRegion(
                            latitude, longitude, 100
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

            Log.d("AddToGeoFenceList: ", String.valueOf(geofenceList.size()));
            registerGeoFences();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("onItemClick: ", String.valueOf(adapterView.getSelectedItem()));
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        Intent intent = new Intent(getActivity(), AddPlacesActivity.class);

        switch (id) {

            case R.id.home:
                intent.putExtra("Title", "Home");

                break;
            case R.id.work:
                intent.putExtra("Title", "Work");

                break;
            case R.id.school:
                intent.putExtra("Title", "School");

                break;
            case R.id.grocery:
                intent.putExtra("Title", "Grocery");

                break;

            case R.id.add_place:
                intent.putExtra("Title", "Add Place");
                break;


        }


        startActivity(intent);


    }


    private void registerGeoFences() {

        // mGoogleApiClient == null  || !mGoogleApiClient.isConnected()

        if (geofenceList == null || geofenceList.size() == 0) {
            Log.d("AddGeoFences: ", "return");
            return;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("onSuccess: ", "geoFencesAdd");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("onFailure: ", e.getMessage().toString());

                        if(e.getMessage().equals("13")){

                            Log.d( "onFailure: ","enable backgroung location permission!");
                            Toast.makeText(getContext(), "Please Check Your Location Permission And Allow All Time!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void unRegisterGeoFences(List<String> GeoFenceIdToRemove) {

     /*   if(mGoogleApiClient == null  || !mGoogleApiClient.isConnected()  )
        {

            return;
        }*/
        geofencingClient.removeGeofences(GeoFenceIdToRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("onSuccess: ", "unregistered");
            }
        });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
