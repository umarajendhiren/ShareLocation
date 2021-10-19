package com.androidapps.sharelocation.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.androidapps.sharelocation.viewmodel.RiderViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverLiveRoute extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private RiderViewModel viewModel;
    List<String> busStopName = new ArrayList<String>();
    TextView tvDistance, tvDuration;
    private boolean isInfoWindowShown;
    static float lastKnownDistance = 0;

    List<StringToJsonSerialization> busStopDetailsList = new ArrayList<StringToJsonSerialization>();
   /* @Override
    protected void onStop() {
        super.onStop();

        viewModel.setRouteMapFalse();
    }*/

    @Override
    protected void onPause() {
        super.onPause();

        viewModel.setIsItRouteMap(false);
    }

    String driverId;
    Calendar cl;
    Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_live_route);
        viewModel = new ViewModelProvider(this).get(RiderViewModel.class);


        if (getIntent().getExtras().get("DriverId") != null) {
            driverId = getIntent().getExtras().get("DriverId").toString();
        }


        tvDistance = findViewById(R.id.tv_distance);
        tvDuration = findViewById(R.id.tv_time);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DriverLiveRoute.this, android.R.layout.simple_spinner_item, busStopName);

        //android.R.layout.simple_spinner_dropdown_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spn_busstop);


        viewModel.getLiveRouteBusStopDetails().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> busStopDetails) {

                busStopName.clear();
                busStopDetailsList.clear();
                lastKnownDistance = 0;
                busStopDetailsList.addAll(busStopDetails);
                if (busStopDetails.size() > 0) {
                    busStopName.add("Select Bus Stop");
                    for (int i = 0; i < busStopDetails.size(); i++) {
                        Log.d("onChanged:stop ", busStopDetails.get(i).getPlaceName());

                        busStopName.add(busStopDetails.get(i).getPlaceName());


                    }
                } else if (busStopDetails.size() == 0)
                    busStopName.add("Bus Stop Not Available");


                sItems.setAdapter(adapter);
                sItems.setOnItemSelectedListener(DriverLiveRoute.this);


                SharedPreferences sharedPref = getSharedPreferences("SelectedBusStop", Context.MODE_PRIVATE);


                String selectedbusstop = sharedPref.getString("com.androidapps.sharelocation.selectedbusstop", "null");


                if (!selectedbusstop.equals(null)) {
                    Log.d("on:selectedbusstop", selectedbusstop);

                    /*int selectedBusStopPosition = adapter.getPosition(selectedbusstop);
                    sItems.setSelection(selectedBusStopPosition);*/
                    Log.d("busStopDetailssix", String.valueOf(busStopDetailsList.size()));
                    //viewModel.setSelectedBusStop(busStopDetailsList.get(selectedBusStopPosition - 1));

                    for (int j = 0; j < busStopName.size(); j++) {

                        if (selectedbusstop.equals(busStopName.get(j))) {
                            Log.d("busstop ", busStopName.get(j));
                            Log.d("busstop ", String.valueOf(j));
                       int alreadySelectedItemId=adapter.getPosition(selectedbusstop);
                            Log.d( "alreadySelectedItemId", String.valueOf(alreadySelectedItemId));
                           // if(alreadySelectedItemId!=j) {
                                sItems.setSelected(true);
                                sItems.setSelection(j);


                                // viewModel.setSelectedBusStop(busStopDetailsList.get(j));
                                break;
                           // }
                        }

               /* else{
                    sItems.setSelection(0);

                }*/
                    }


                }
            }
        });


        //get distance live data
        viewModel.getDistanceLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                tvDistance.setVisibility(View.VISIBLE);
                Log.d("onChanged:dis ", s);
                currentTime = Calendar.getInstance().getTime();
                Log.d("onChanged:time ", String.valueOf(currentTime));

                String[] time = s.split(" ");
                float distance = Float.parseFloat(time[0]);
                String ftOrMi = time[1];

                Log.d("lastKnownDistance", String.valueOf(lastKnownDistance));

                if (distance < lastKnownDistance)//coming to home
                    tvDuration.setVisibility(View.VISIBLE);

                else
                    tvDuration.setVisibility(View.GONE);

                lastKnownDistance = distance;

                tvDistance.setText(s + " Away");

            }
        });

        viewModel.getDurationLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("onChanged:dura ", s);
                Log.d("onChanged:time ", String.valueOf(currentTime));


                if (s.contains("day")) {

                    //call day
                    Log.d("onResponse:day", "called");
                    getDay(s);
                } else if (s.contains("hour")) {

                    //call hour
                    Log.d("onResponse:hour", "called");
                    getHour(s);
                } else if (s.contains("mins")) {
                    Log.d("onResponse:mints", "called");
                    getMinite(s);
                } else if (s.contains("min")) {
                    Log.d("onResponse:mint", "called");
                    getMinit(s);
                }

                // tvDuration.setText("ETA:"+s);

            }
        });

      /*  viewModel.subscribeForUserLiveQuery();
        viewModel.subscribeForCircleNameLiveQuery();*/


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.setIsItRouteMap(true);
        viewModel.isSubsribeDForUserLiveQuery().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForUserstart", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForUserLiveQuery();
                }

            }
        });
        //homePageViewModel.subscribeForCircleNameLiveQuery();
        viewModel.isSubsribeDForCircleNameLiveQuery().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSubscribed) {
                Log.d("isSubsribeDForCirclesta", String.valueOf(isSubscribed));

                if (!isSubscribed) {

                    viewModel.subscribeForCircleNameLiveQuery();
                }

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        viewModel.getDriverLiveLocation(mMap, driverId);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        Log.d("onItemSelected: ", String.valueOf(adapterView.getSelectedItem()));

        if (adapterView.getSelectedItem().equals("Select Bus Stop")) {

            tvDuration.setVisibility(View.GONE);
            tvDistance.setVisibility(View.GONE);
            SharedPreferences sharedPref = getSharedPreferences("SelectedBusStop", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("com.androidapps.sharelocation.selectedbusstop", "null");

            editor.apply();

            return;
        }
        //lastKnownDistance = 0;
        for (int j = 0; j < busStopDetailsList.size(); j++) {


            if (adapterView.getSelectedItem().equals(busStopDetailsList.get(j).getPlaceName())) {

                Log.d("onItemSelected:Geo", String.valueOf(busStopDetailsList.get(j).getGeoPoint()));

                viewModel.setSelectedBusStop(busStopDetailsList.get(j));

            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getSec(String duration) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (duration.contains("mins")) {
              /*   String minits=distanceAndDurationList.get(1).replace("mins","").trim();

                       Log.d( "onRes:miniy",minits);*/


            String[] time = duration.split(" ");

            String mini = time[0];
            String minis = time[1];
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/


            cl.add(Calendar.MINUTE, Integer.parseInt(mini));

            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }


            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


           /* marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();*/
            tvDuration.setText("Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
        }

    }

    public void getMinit(String duration) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);
//&& !duration.contains("mins")
        if (duration.contains("min")) {
              /*   String minits=distanceAndDurationList.get(1).replace("mins","").trim();

                       Log.d( "onRes:miniy",minits);*/


           /* String[] time = duration.split(" ");

            String mini = time[0];
            String minis = time[1];*/
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/


            cl.add(Calendar.MINUTE, 1);

            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }


            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


           /* marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();*/
            tvDuration.setText("Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);

        }

    }

    public void getMinite(String duration) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (duration.contains("mins")) {
              /*   String minits=distanceAndDurationList.get(1).replace("mins","").trim();

                       Log.d( "onRes:miniy",minits);*/


            String[] time = duration.split(" ");

            String mini = time[0];
            String minis = time[1];
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/


            cl.add(Calendar.MINUTE, Integer.parseInt(mini));

            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }


            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


           /* marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();*/
            tvDuration.setText("Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);

        }

    }

    public void getHour(String duration) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (duration.contains("hour")) {

            String[] time = duration.split(" ");
            String hour = time[0];
            String hours = time[1];

            if (duration.contains("mins")) {
                String mini = time[2];
                String minis = time[3];
                cl.add(Calendar.MINUTE, Integer.parseInt(mini));
            }
                        /*String hourse=distanceAndDurationList.get(1).replace("hour","").trim();
                        Log.d( "onRes:hou",hourse);*/
            cl.add(Calendar.HOUR, Integer.parseInt(hour));


            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }

            Log.d("onRespafter ", cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);


           /* marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();*/
            tvDuration.setText("Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);


        }
    }

    public void getDay(String duration) {

        cl = Calendar.getInstance();
        cl.setTime(currentTime);

        if (duration.contains("day")) {
    /*    String day=distanceAndDurationList.get(1).replace("day","").trim();
        Log.d( "onRes:day",day);*/

            String[] time = duration.split(" ");
            String day = time[0];
            String days = time[1];


            if (duration.contains("hour")) {
                String hour = time[2];
                String hours = time[3];
                cl.add(Calendar.HOUR, Integer.parseInt(hour));
            }

            if (duration.contains("mins")) {
                String mini = time[4];
                String minis = time[5];
                cl.add(Calendar.MINUTE, Integer.parseInt(mini));
            }


            cl.add(Calendar.DATE, Integer.parseInt(day));


            String amOrPm = null;
            if (cl.get(Calendar.AM_PM) == Calendar.AM) {
                Log.d("amOrPm:", "am");
                amOrPm = "am";

            } else if (cl.get(Calendar.AM_PM) == Calendar.PM) {

                amOrPm = "pm";
                Log.d("amOrPm:", "pm");
            }

            Log.d("onRespafter ", cl.get(Calendar.DATE) + "/" + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.YEAR) + cl.get(Calendar.HOUR) + " " + cl.get(Calendar.MINUTE) + " " + amOrPm);

           /* if(cl.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)){

                //same day
            }*/


            /*marker.setSnippet("Distaice: " + distanceAndDurationList.get(0) + "Arrival time " + cl.get(Calendar.MONTH) + "/" + cl.get(Calendar.DATE) + "/" + cl.get(Calendar.YEAR) + "  at" + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);
            marker.showInfoWindow();*/
            tvDuration.setText("Arrival time " + cl.get(Calendar.HOUR) + ":" + cl.get(Calendar.MINUTE) + " " + amOrPm);


        }
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
}