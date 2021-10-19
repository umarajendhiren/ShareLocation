package com.androidapps.sharelocation.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.adapters.AvailableBusStopAdapter;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetBusStopNotificationActivity extends AppCompatActivity implements View.OnClickListener {
    TextView addBusStop;
    HomePageViewModel viewModel;
    AvailableBusStopAdapter adapter;
    RecyclerView recyclerViewBusStop;

    List<StringToJsonSerialization> busStopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        setContentView(R.layout.dialog_bus_stops);


     /*   Toolbar toolbar=(Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);

        ab.setTitle("Bus Stops");*/

        recyclerViewBusStop = findViewById(R.id.bus_stop_dialog);
        recyclerViewBusStop.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBusStop.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerViewBusStop.addItemDecoration(itemDecoration);


        if(getIntent().getStringExtra("driverId")!=null)
        viewModel.getAllSavedBusStops(getIntent().getStringExtra("driverId"));


        StringToJsonSerialization emptyStop=new StringToJsonSerialization();
        emptyStop.setPlaceName("Stop not available!");

        List<StringToJsonSerialization> emptyList=new ArrayList<>();
        emptyList.add(emptyStop);

        viewModel.getBusStopLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> availableBusStopList) {

                // Log.d("onChanged:bus", String.valueOf(stringToJsonSerializations.size()));
                busStopList = new ArrayList<>();

                if(availableBusStopList != null && availableBusStopList.size()==0) {
                    adapter = new AvailableBusStopAdapter(emptyList, SetBusStopNotificationActivity.this, viewModel);
                    recyclerViewBusStop.setAdapter(adapter);
                }
               else if (availableBusStopList != null && availableBusStopList.size() > 0) {

                    /*for (int i = 0; i < availableBusStopList.size(); i++) {

                        HashMap<String, LatLng> stopNameAndLatLng = availableBusStopList.get(i).getWayPoints();
                        for (String stopName : stopNameAndLatLng.keySet()) {

                            StringToJsonSerialization busStopDetail = new StringToJsonSerialization();
                            Log.d("onChanged:stop", stopName);
                            busStopDetail.setPlaceName(stopName);
                            double busStopLatitude = availableBusStopList.get(i).getWayPoints().get(stopName).latitude;
                            double busStopLongitude = availableBusStopList.get(i).getWayPoints().get(stopName).longitude;

                            busStopDetail.setGeoPoint(busStopLatitude, busStopLongitude);
                            busStopDetail.setNotificationOn(availableBusStopList.get(i).isNotificationOn());
                            busStopDetail.setObjectId(availableBusStopList.get(i).getObjectId());


                            List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");


                            for (int j = 0; j < subscribedChannels.size(); j++) {

                                Log.d("subscribedChannel ", String.valueOf(subscribedChannels.get(j)));
                                String currentPlace = "BusStop " + json.getPlaceName() + " " + getSelectedDriverId().getValue();
                                if (currentPlace.equals(subscribedChannels.get(j))) {

                                    Log.d("setNotificationOn", json.getPlaceName() + " notification ON");
                                    serialization.setNotificationOn(true);
                                }


                            }

                            busStopList.add(busStopDetail);

                            Log.d("busStopListsi: ", String.valueOf(busStopList.size()));
                        }
                    }*/
                    adapter = new AvailableBusStopAdapter(availableBusStopList, SetBusStopNotificationActivity.this, viewModel);
                    recyclerViewBusStop.setAdapter(adapter);

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // viewModel.getAllSavedBusStops();
    }

    @Override
    public void onClick(View view) {


    }


}