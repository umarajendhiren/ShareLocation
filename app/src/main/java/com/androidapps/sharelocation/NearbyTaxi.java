package com.androidapps.sharelocation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;


public class NearbyTaxi extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerVehicle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_taxi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Nearby Vehicle");

        ArrayList<String> vehicleList = new ArrayList<String>();
        vehicleList.add("Car");
        vehicleList.add("Bike");
        vehicleList.add("Auto");
        vehicleList.add("Van");
        vehicleList.add("Bus");
        vehicleList.add("Share Auto");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, vehicleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerVehicle = findViewById(R.id.vehicle_spinner);
        spinnerVehicle.setOnItemSelectedListener(this);

        spinnerVehicle.setAdapter(adapter);



      /*  FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        Log.d("onItemClick: ", String.valueOf(adapterView.getSelectedItem()));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}