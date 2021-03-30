package com.androidapps.sharelocation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidapps.sharelocation.BackgroundLocationUpdate.NetworkConnectivityChangeBroadcastReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomaPageActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    HomePageViewModel homePageViewModel;
    boolean connected;
    NetworkConnectivityChangeBroadcastReceiver networkConnectivityChangeBroadcastReceiver;
    SharedPreferences sharedPrefConnectivity;
TextView networkState;
    NetworkInfo nInfo;
String isConnected;


    ConnectivityManager cm;
    @Override
    protected void onStart() {
        super.onStart();
        Log.d( "onStart:","called");
         cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();

         sharedPrefConnectivity = getSharedPreferences("Network", Context.MODE_PRIVATE);
        sharedPrefConnectivity
                .registerOnSharedPreferenceChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(networkConnectivityChangeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(networkConnectivityChangeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("onBackPressed: ", "called");
        // this.finish();

        SharedPreferences selectedFragment = getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = selectedFragment.edit();
        editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 12);

        editor.apply();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this will help to know ,how often user open the app .
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_home_page);
        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);

        networkState=findViewById(R.id.network_state);


        bottomNavigationView = findViewById(R.id.navigation);
        //we need to display home fragment as a default fragment in Homepage activity
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
networkConnectivityChangeBroadcastReceiver=new NetworkConnectivityChangeBroadcastReceiver();

        if (ParseUser.getCurrentUser() == null) {


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {

            //if user logged in need to show this page


        }



        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().get("circleName") != null) {

                String circleName = getIntent().getExtras().get("circleName").toString();
                String inviteCode = getIntent().getExtras().get("inviteCode").toString();
                homePageViewModel.getSelectedGroupNameLiveData().setValue(circleName);
                homePageViewModel.getSelectedInviteCodeLiveData().setValue(inviteCode);
            }
        }

      /*  if (getIntent().getExtras() != null) {

            String isDriverMapFragment = getIntent().getExtras().get("DriverMap").toString();
            Log.d("isDriverMapFragment", isDriverMapFragment);

            if (isDriverMapFragment.equals("DriverMap")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new vehicle_tracking_fragment()).commit();
            }
        } else if (getIntent().getExtras() == null) {
            Log.d("isDriverMapFragment", "null");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PeopleFragment()).commit();

        }*/


        // Pop off everything up to and including the current tab
        fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setSelectedItemId(R.id.action_people);

        Log.d("onCreate:bottom", String.valueOf(bottomNavigationView.getSelectedItemId()));
        //when the user press back button ,it will not exit app instead it will show last viewed fragment in back stack.
        // fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //isConnected = sharedPrefConnectivity.getString("com.androidapps.sharelocation.network", "null");
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        if(connected){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PeopleFragment()).commit();
        }
        else if(!connected) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NoNetworkFragment()).commit();

        }




        homePageViewModel.isConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean connected) {
                Log.d("onChanged:net", String.valueOf(connected));
                if(connected){

                    Log.d( "onS", String.valueOf(bottomNavigationView.getSelectedItemId()));
                    if(bottomNavigationView.getSelectedItemId()==R.id.action_people) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, new PeopleFragment()).commit();
                    }

                    else   if(bottomNavigationView.getSelectedItemId()==R.id.action_places) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, new PlacesFragment()).commit();
                    }
                    // networkState.setVisibility(View.VISIBLE);

                    else   if(bottomNavigationView.getSelectedItemId()==R.id.action_tracking) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, new vehicle_tracking_fragment()).commit();
                    }

                    else   if(bottomNavigationView.getSelectedItemId()==R.id.action_settings) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, new SettingsFragment()).commit();
                    }
                }
                if(!connected){



                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new NoNetworkFragment()).commit();
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {



            case R.id.action_people:
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                nInfo = cm.getActiveNetworkInfo();
                connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
                Log.d("onNavigationIte", String.valueOf(connected));
                if(!connected){
                    fragment = new NoNetworkFragment();
                    Log.d("error", "No network!");
                   // bottomNavigationView.setSelectedItemId(R.id.action_people);

                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();

                    break;
                } else if(connected) {

                    fragment = new PeopleFragment();


                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();

                    break;
                }

            case R.id.action_places:
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                nInfo = cm.getActiveNetworkInfo();
                connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
                Log.d("onNavigationIte", String.valueOf(connected));

                if(!connected) {
                    fragment = new NoNetworkFragment();
                    Log.d("error", "No network!");
                  //  bottomNavigationView.setSelectedItemId(R.id.action_places);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();

                    break;
                } else if(connected){
                    fragment = new PlacesFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    break;
                }
            case R.id.action_settings:
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                nInfo = cm.getActiveNetworkInfo();
                connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
                Log.d("onNavigationIte", String.valueOf(connected));
                if(!connected) {
                    fragment = new NoNetworkFragment();
                    Log.d("error", "No network!");
                  //  bottomNavigationView.setSelectedItemId(R.id.action_settings);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();

                    break;
                } else if(connected) {
                    fragment = new SettingsFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            // .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    break;

                }
            case R.id.action_tracking:
                cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                nInfo = cm.getActiveNetworkInfo();
                connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
                Log.d("onNavigationIte", String.valueOf(connected));
                if(!connected){
                    fragment = new NoNetworkFragment();
                    Log.d("error", "No network!");
                  //  bottomNavigationView.setSelectedItemId(R.id.action_tracking);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            //.addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();

                    break;
                } else if(connected){
                    fragment = new vehicle_tracking_fragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            // .addToBackStack(BACK_STACK_ROOT_TAG)
                            .commit();
                    break;

                }
        }


        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d( "onSharedPreference ",s);
        if(s.equals("com.androidapps.sharelocation.network")){

            String isConnected = sharedPrefConnectivity.getString("com.androidapps.sharelocation.network", "null");

            Log.d( "isConnected: ",isConnected);
            if(isConnected.equals("connected")){

                homePageViewModel.isNetworkCOnnected(true);



                    Log.d( "reconnect","called");
                   // ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/")).reconnect();
                    homePageViewModel.reConnectLiveQuery();


               /* homePageViewModel.subscribeForUserLiveQuery();
                homePageViewModel.subscribeForCircleNameLiveQuery();*/
            }
            else if(isConnected.equals("notconnected")){
                homePageViewModel.isNetworkCOnnected(false);
            }
          /*  if(isConnected.equals("connected")){

                Log.d( "onSharedselected ", String.valueOf(bottomNavigationView.getSelectedItemId()));
if(bottomNavigationView.getSelectedItemId()==R.id.action_people) {
    fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, new PeopleFragment()).commit();
}

             else   if(bottomNavigationView.getSelectedItemId()==R.id.action_places) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new PlacesFragment()).commit();
                }
             // networkState.setVisibility(View.VISIBLE);

else   if(bottomNavigationView.getSelectedItemId()==R.id.action_tracking) {
    fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, new vehicle_tracking_fragment()).commit();
}

else   if(bottomNavigationView.getSelectedItemId()==R.id.action_settings) {
    fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, new SettingsFragment()).commit();
}
            }
            if(isConnected.equals("notconnected")){



               fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new NoNetworkFragment()).commit();
            }*/


        }
    }
}
















