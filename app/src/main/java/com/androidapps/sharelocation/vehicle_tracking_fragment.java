package com.androidapps.sharelocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.Context.MODE_PRIVATE;

@AndroidEntryPoint
public class vehicle_tracking_fragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    MainViewModel mainViewModel;
    SwitchCompat riderDriverSwitch;
    Button getStarted;
    TextView riderHint,driverHint;
    Group getStartedGroup;
    FragmentContainerView fragmentContainerView;
    SharedPreferences isDriver;


    @Override
    public void onResume() {
        super.onResume();

        isDriver=getActivity().getSharedPreferences("isDriver",MODE_PRIVATE);
        String isUserDriver= isDriver.getString("isUserDriver","null");
        Log.d("isUserDriverr: ",isUserDriver);
        if(isUserDriver.equals("false")  || isUserDriver.equals("true")){
            getStartedGroup.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();

            Log.d("isUserDriver","falsee");

        }

        else if(isUserDriver.equals("null"))
        {

            Log.d("isUserDriver","null");
            getStartedGroup.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.GONE);


        }}

     /*   else if(isUserDriver.equals("true")){

            Log.d("isUserDriver","true");
            getStartedGroup.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);

            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();


        }*//*

        else if(isUserDriver.equals("null"))
        {

            Log.d("isUserDriver","null");
            getStartedGroup.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.GONE);


        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehicle_tracking, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        riderDriverSwitch=view.findViewById(R.id.switch_select);
        riderDriverSwitch.setOnCheckedChangeListener(this);
        //riderHint=view.findViewById(R.id.rider_hint);
        driverHint=view.findViewById(R.id.driver_hint);
        getStarted=view.findViewById(R.id.get_started_button);
        getStarted.setOnClickListener(this);


        getStartedGroup=view.findViewById(R.id.get_started_group);
        fragmentContainerView=view.findViewById(R.id.fragment_container);

        riderDriverSwitch.setChecked(true);
        driverHint.setText("If your kid going to school by auto or school bus,you can track your driver here.\n\n  OR \n\nif you are going to office by bus you can find your driver here.\n");




        isDriver=getActivity().getSharedPreferences("isDriver",MODE_PRIVATE);
        String isUserDriver= isDriver.getString("isUserDriver","null");
        Log.d("isUserDriver: ",isUserDriver);
        if(isUserDriver.equals("false")  || isUserDriver.equals("true")){
            getStartedGroup.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();

            Log.d("isUserDriver","falsee");

        }

     /*   else if(isUserDriver.equals("true")){

            Log.d("isUserDriver","true");
            getStartedGroup.setVisibility(View.GONE);
            fragmentContainerView.setVisibility(View.VISIBLE);

            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();


        }*/

        else if(isUserDriver.equals("null"))
        {

            Log.d("isUserDriver","null");
            getStartedGroup.setVisibility(View.VISIBLE);
            fragmentContainerView.setVisibility(View.GONE);


        }


     /*   SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isUserHasDriver", Context.MODE_PRIVATE);

if(sharedPreferences!=null) {
    String isUserHasDriver = sharedPreferences.getString("com.androidapps.sharelocation.hasdriver", null);


    Log.d("isUserHasDriver", isUserDriver);

    if (isUserHasDriver.equals("true")) {

        getStartedGroup.setVisibility(View.GONE);
        fragmentContainerView.setVisibility(View.VISIBLE);

        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();


    }*/




        return view;
    }

    @Override
    public void onClick(View view) {

if(view.getId()==R.id.get_started_button){

    if(riderDriverSwitch.isChecked()){


        SharedPreferences.Editor editor=isDriver.edit();
        editor.putString("isUserDriver","false");
        editor.apply();

        getStartedGroup.setVisibility(View.GONE);
        fragmentContainerView.setVisibility(View.VISIBLE);

        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, DriverMapFragment.class, null, "2").commit();


       /* Intent  startRiderActivity=new Intent(getActivity(),RiderActivity.class);
        startActivity(startRiderActivity);*/


    }

    else if(!riderDriverSwitch.isChecked()){

        SharedPreferences.Editor editor=isDriver.edit();
        editor.putString("isUserDriver","true");
        editor.apply();
        Intent startDriverActivity=new Intent(getActivity(),DriverActivity.class);
        startActivity(startDriverActivity);
    }

}
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked){


           /*     riderHint.setVisibility(View.VISIBLE);
                driverHint.setVisibility(View.GONE);*/

            driverHint.setText("If your kid going to school by auto or school bus,you can track your driver here.\n\n  OR \n\nif you are going to office by bus you can find your driver here.\n");




        }
        else {
            driverHint.setText("If you are driver,you can register here and share your code with your rider.so they can track you on map");

          /*  riderHint.setVisibility(View.GONE);
            driverHint.setVisibility(View.VISIBLE);*/
        }
    }
}
