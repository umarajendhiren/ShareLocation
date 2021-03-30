package com.androidapps.sharelocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.databinding.ActivityRiderBinding;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RiderActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    ActivityRiderBinding binding;
    Group radioGroup, driverCodeGroup;
    RiderViewModel riderViewModel;
    private SharedPreferences isDriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        riderViewModel = new ViewModelProvider(this).get(RiderViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rider);


        radioGroup = binding.groupRadio;
        driverCodeGroup = binding.groupDriverCode;

        binding.radioGroup.setOnCheckedChangeListener(this);

        binding.submit.setOnClickListener(this);
        isDriver=getSharedPreferences("isDriver",MODE_PRIVATE);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int checkedRadio = radioGroup.getCheckedRadioButtonId();
        if (checkedRadio == R.id.yes) {

            driverCodeGroup.setVisibility(View.VISIBLE);
            binding.workFlow.setVisibility(View.GONE);
        }

        if (checkedRadio == R.id.no) {
            binding.workFlow.setVisibility(View.VISIBLE);
            driverCodeGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

       if(view.getId()==R.id.submit) {
           String driverCode = binding.driverCode.getText().toString().trim();

           if (driverCode.isEmpty()) {

               Toast.makeText(this, "Please enter your driver code!", Toast.LENGTH_SHORT).show();
           } else {

               getDriverForCircleCode(driverCode);
           }

       }
    }


    private void getDriverForCircleCode(String driverCode) {


        /*need to query admin detail from circleName class*/

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();

        queryUser.getInBackground(driverCode, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                if (e == null) {

                    String objectId = object.getObjectId();
                    Log.d("objectId", objectId);

                    Boolean isDriver = object.getBoolean("IsDriver");
                    Log.d("isDriver", String.valueOf(isDriver));

                    if (isDriver) {

                        riderViewModel.getDriverCode().setValue(objectId);

                        binding.groupRadio.setVisibility(View.GONE);
                        binding.groupDriverCode.setVisibility(View.GONE);
                        binding.workFlow.setVisibility(View.GONE);
                        binding.container.setVisibility(View.VISIBLE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, DriverDetailFragment.class, null, null).commit();


                    }
                } else {

                    //no results found for query
                    Log.d("error", String.valueOf(e.getCode()));
                    if (e.getCode() == 101)
                        Toast.makeText(RiderActivity.this, "Please enter valid driver code!", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    public void setFragment(int fragmentId){

        if(fragmentId==2){
            SharedPreferences.Editor editor=isDriver.edit();
            editor.putString("isUserDriver","false");
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.container, DriverMapFragment.class, null, null).commit();

        }
    }
}
