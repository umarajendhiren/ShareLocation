package com.androidapps.sharelocation.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.adapters.AddBusRouteRecylerViewAdapter;
import com.androidapps.sharelocation.view.DriverRouteMapActivity;
import com.androidapps.sharelocation.OnClickCallListener;
import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.model.StringToJsonSerialization;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddBusStopActivity extends AppCompatActivity implements View.OnClickListener {
    TextView addBusStop, addRoute;
    HomePageViewModel viewModel;
    AddBusRouteRecylerViewAdapter adapter;
    RecyclerView recyclerViewBusStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        setContentView(R.layout.activity_add_bus_stop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);

        ab.setTitle("Add Your Stops");


        addRoute = findViewById(R.id.add_route);

        addRoute.setOnClickListener(this);


        recyclerViewBusStop = findViewById(R.id.recycler_view_bus_route);
        recyclerViewBusStop.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBusStop.setHasFixedSize(true);

        viewModel.getAllSavedBusRoute();

        viewModel.getBusRouteLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

                if (stringToJsonSerializations != null && stringToJsonSerializations.size() > 0) {

                    Log.d("onChanged:busRoute", String.valueOf(stringToJsonSerializations.size()));
                    adapter = new AddBusRouteRecylerViewAdapter(R.layout.add_bus_route_list, viewModel, new OnClickCallListener() {
                        @Override
                        public void onCall(String userId, double latitide, double longitude) {

                        }

                        @Override
                        public void onDisconnect(String userId) {

                        }

                        @Override
                        public void onClickRouteName(StringToJsonSerialization routeDetails,int objectPosition) {

                            Log.d("onClickRouteName:", routeDetails.getRouteName() + " " + routeDetails.getOrigin()+" "+objectPosition);
                            viewModel.updateRouteDetail(routeDetails,objectPosition);


                        }
                    });
                    adapter.setBusRouteList(stringToJsonSerializations);
                    recyclerViewBusStop.setAdapter(adapter);
                }
            }
        });


      /*  viewModel.getLiveRoute().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> polyStrings) {
                Log.d( "onChanged:pol", String.valueOf(polyStrings));
                viewModel.livepolyString.addAll(polyStrings);
                Log.d( "onChanged:poly", String.valueOf(viewModel.livepolyString.size()));


            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        // viewModel.getAllSavedBusStops();
    }

    @Override
    public void onClick(View view) {
       /* if(view.getId()==R.id.add_bus_stop){

            Intent startAddPlacesActivity = new Intent(this, AddPlacesActivity.class);

            startAddPlacesActivity.putExtra("Title","Add Bus Stop");

            startActivity(startAddPlacesActivity);


        }*/

        if (view.getId() == R.id.add_route) {


            Intent shareRouteActivity = new Intent(this, DriverRouteMapActivity.class);


            startActivity(shareRouteActivity);
        }


    }


}