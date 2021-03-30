package com.androidapps.sharelocation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.BackgroundLocationUpdate.AddBusStopRecylerViewAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetBusStopNotificationActivity extends AppCompatActivity implements View.OnClickListener {
TextView addBusStop;
HomePageViewModel viewModel;
AddBusStopRecylerViewAdapter adapter;
RecyclerView recyclerViewBusStop;
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

        viewModel.getBusStopLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

                // Log.d("onChanged:bus", String.valueOf(stringToJsonSerializations.size()));
                adapter = new AddBusStopRecylerViewAdapter(stringToJsonSerializations,SetBusStopNotificationActivity.this, viewModel);
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



    }


}