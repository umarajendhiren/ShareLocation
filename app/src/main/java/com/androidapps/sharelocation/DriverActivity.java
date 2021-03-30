package com.androidapps.sharelocation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DriverActivity extends AppCompatActivity {
    SharedPreferences isDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

         isDriver=getSharedPreferences("isDriver",MODE_PRIVATE);

         if(getIntent().getExtras()!=null){

          int fragmentId= (int) getIntent().getExtras().get("setFragment");
             Log.d("fragmentId ", String.valueOf(fragmentId));
             setFragment(fragmentId);
         }

      //  getSupportFragmentManager().beginTransaction().replace(R.id.container, ShareDriverCodeFragment.class, null, "1").commit();




    }



    public void setFragment(int fragment){

        if(fragment==1){



            getSupportFragmentManager().beginTransaction().replace(R.id.container, ShareDriverCodeFragment.class, null, "1").commit();


        }

        if(fragment==2){

            getSupportFragmentManager().beginTransaction().replace(R.id.container,DriverMessageFragment.class, null, "1").commit();

        }
    }
}