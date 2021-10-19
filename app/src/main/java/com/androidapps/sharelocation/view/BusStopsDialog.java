package com.androidapps.sharelocation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapps.sharelocation.R;
import com.androidapps.sharelocation.adapters.AddBusStopRecylerViewAdapter;
import com.androidapps.sharelocation.model.StringToJsonSerialization;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


/**
 * A simple {@link Fragment} subclass.
 */


@AndroidEntryPoint
public class BusStopsDialog extends DialogFragment implements View.OnClickListener {
    HomePageViewModel viewModel;
    private RecyclerView recyclerViewBusStop;
    AddBusStopRecylerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        viewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        View root = inflater.inflate(R.layout.dialog_bus_stops, container, false);
        // setContentView(R.layout.activity_add_bus_stop);



        // retrieve display dimensions
       /* Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

// inflate and adjust layout
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.your_dialog_layout, null);
        layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));

        dialog.setView(layout);*/

      /*  this.getDialog().setCanceledOnTouchOutside(true);
        this.getDialog().setTitle("Bus Stops");*/

      //  Toolbar toolbar = (Toolbar) root.findViewById(R.id.my_toolbar);


     //   ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        //setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
       // ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // Enable the Up button
       /* ab.setDisplayHomeAsUpEnabled(true);


        ab.setDisplayShowTitleEnabled(true);

        ab.setTitle("Bus Stops List");*/

     /*   addBusStop=findViewById(R.id.add_bus_stop);

        addBusStop.setOnClickListener(this);*/

        recyclerViewBusStop = root.findViewById(R.id.bus_stop_dialog);
        recyclerViewBusStop.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBusStop.setHasFixedSize(true);





        viewModel.getBusStopLive().observe(this, new Observer<List<StringToJsonSerialization>>() {
            @Override
            public void onChanged(List<StringToJsonSerialization> stringToJsonSerializations) {

                // Log.d("onChanged:bus", String.valueOf(stringToJsonSerializations.size()));



                    adapter = new AddBusStopRecylerViewAdapter(stringToJsonSerializations, getContext(), viewModel);
                    recyclerViewBusStop.setAdapter(adapter);

            }
        });

        return  root;

    }



    @Override
    public void onClick(View view) {

    }
}

