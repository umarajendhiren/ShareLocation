package com.androidapps.sharelocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.BackgroundLocationUpdate.AddBusStopRecylerViewAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddBusStopActivity extends AppCompatActivity implements View.OnClickListener {
TextView addBusStop;
HomePageViewModel viewModel;
AddBusStopRecylerViewAdapter adapter;
RecyclerView recyclerViewBusStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        setContentView(R.layout.activity_add_bus_stop);

        Toolbar toolbar=(Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);

        ab.setTitle("Add Your Stops");

        addBusStop=findViewById(R.id.add_bus_stop);

        addBusStop.setOnClickListener(this);

        recyclerViewBusStop=findViewById(R.id.recycler_view_bus_stop);
        recyclerViewBusStop.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBusStop.setHasFixedSize(true);

        viewModel.getAllSavedBusStops();

        viewModel.getBusStopLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

               // Log.d("onChanged:bus", String.valueOf(stringToJsonSerializations.size()));
                adapter = new AddBusStopRecylerViewAdapter(stringToJsonSerializations, AddBusStopActivity.this, viewModel);
                recyclerViewBusStop.setAdapter(adapter);
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
        if(view.getId()==R.id.add_bus_stop){

/*
            Intent startAddPlacesActivity = new Intent(this, AddPlacesActivity.class);

            startAddPlacesActivity.putExtra("Title","Add Bus Stop");

            startActivity(startAddPlacesActivity);*/


            Intent startAddPlacesActivity = new Intent(this, DriverRouteMapActivity.class);

          //  startAddPlacesActivity.putExtra("Title","Add Bus Stop");

            startActivity(startAddPlacesActivity);
        }



    }


}