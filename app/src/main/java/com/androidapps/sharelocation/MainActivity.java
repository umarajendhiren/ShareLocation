package com.androidapps.sharelocation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingResource;
import androidx.viewpager2.widget.ViewPager2;

import com.androidapps.sharelocation.databinding.ActivityMainBinding;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


//an activity or a fragment that is annotated with @AndroidEntryPoint can get the ViewModel instance as normal using ViewModelProvider
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    List<ActivityTransition> transitions = new ArrayList<>();
    // ViewPager2 viewPager;
    ViewPagerAdapter adapter;
    FrameLayout fragmentContainer;
    // The Idling Resource which will be null in production.
    @Nullable
    private IdlingResourceForTest mIdlingResource;
    MainViewModel viewModel;

    ActivityMainBinding binding;
    PendingIntent mPendingIntent;

    boolean isShowedDetailPermission;
    boolean isShowedDetailPermissionForActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fragmentContainer = findViewById(R.id.fragment_container);
     /*   viewPager.setOffscreenPageLimit(1);
        setUpViewPager(viewPager);*/
isShowedDetailPermission=false;
        isShowedDetailPermissionForActivity=false;

        SharedPreferences selectedFragment = getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);
        int lastSelectedFragment = selectedFragment.getInt("com.androidapps.sharelocation.LastSelectedFragment", 0);
        Log.d("onCreateselec ", String.valueOf(lastSelectedFragment));
        if (lastSelectedFragment == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, GetStartedFragment.class, null, "1").addToBackStack("GetStartedFragment").commit();


        } else if (lastSelectedFragment == 4) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateCircleFragment.class, null, "5").commit();

        } else if (lastSelectedFragment == 5) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateNewCircle.class, null, "6").commit();

        } else if (lastSelectedFragment == 6) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CircleNameFragment.class, null, "7").commit();

        } else if (lastSelectedFragment == 7) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CircleCodeFragment.class, null, "8").commit();

        } else if (lastSelectedFragment == 8) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfilePhotoFragment.class, null, "9").commit();

        } else if (lastSelectedFragment == 9) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddPlacesFragment.class, null, "10").commit();

        } else if (lastSelectedFragment == 10) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddHomMapFragment.class, null, "11").commit();

        } else if (lastSelectedFragment == 11) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ActivityRecoganisation.class, null, "12").commit();

        } else if (lastSelectedFragment == 12) {
            Intent startactivityIntent = new Intent(this, HomaPageActivity.class);
            startActivity(startactivityIntent);
        }

    }

    public void setUpViewPager(ViewPager2 viewPager) {

        adapter = new ViewPagerAdapter(MainActivity.this);

        adapter.addFragment(new GetStartedFragment(), "Fragmet1");

        adapter.addFragment(new NameFragment(), "Fragmet2");
        adapter.addFragment(new AddEmailFragment(), "Fragmet3");
        adapter.addFragment(new PasswordFragment(), "fragment4");

        adapter.addFragment(new CreateCircleFragment(), "fragment5");
        adapter.addFragment(new CreateNewCircle(), "fragment6");

        adapter.addFragment(new CircleNameFragment(), "fragment7");
        adapter.addFragment(new CircleCodeFragment(), "fragment8");


        adapter.addFragment(new ProfilePhotoFragment(), "fragment9");

        adapter.addFragment(new AddPlacesFragment(), "fragment10");
        adapter.addFragment(new AddHomMapFragment(), "Fragmet11");


        viewPager.setAdapter(adapter);


    }


    /*adapter.addFragment(new YourRoleFragment(), "fragment9");*/
    public void setFragmentForViewPager(int positionOfFragment) {
        Log.i("positionOfFragment ", String.valueOf(positionOfFragment));
        //viewPager.setCurrentItem(positionOfFragment);


        if (positionOfFragment == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, NameFragment.class, null, "2").addToBackStack("NameFragment").commit();

        } else if (positionOfFragment == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddEmailFragment.class, null, "3").addToBackStack("AddEmailFragment").commit();

        } else if (positionOfFragment == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, PasswordFragment.class, null, "4").addToBackStack("PasswordFragment").commit();

        } else if (positionOfFragment == 4) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateCircleFragment.class, null, "5").commit();

        } else if (positionOfFragment == 5) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CreateNewCircle.class, null, "6").commit();

        } else if (positionOfFragment == 6) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CircleNameFragment.class, null, "7").commit();

        } else if (positionOfFragment == 7) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CircleCodeFragment.class, null, "8").commit();

        } else if (positionOfFragment == 8) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfilePhotoFragment.class, null, "9").commit();

        } else if (positionOfFragment == 9) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddPlacesFragment.class, null, "10").commit();

        } else if (positionOfFragment == 10) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AddHomMapFragment.class, null, "11").commit();

        } else if (positionOfFragment == 11) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ActivityRecoganisation.class, null, "12").commit();

        } else if (positionOfFragment == 12) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, NoNetworkFragment.class, null, "13").commit();

        }


    }


    public void removeSignUpFragmentFromBackStack() {

        GetStartedFragment getStartedFragment = new GetStartedFragment();
        NameFragment nameFragment = new NameFragment();
        AddEmailFragment addEmailFragment = new AddEmailFragment();
        PasswordFragment passwordFragment = new PasswordFragment();

        getSupportFragmentManager().beginTransaction().remove(getStartedFragment).commit();
        getSupportFragmentManager().beginTransaction().remove(nameFragment).commit();
        getSupportFragmentManager().beginTransaction().remove(addEmailFragment).commit();
        getSupportFragmentManager().beginTransaction().remove(passwordFragment).commit();
    }

    /**
     * Only called from test, creates and returns a new  IdlingResourceForTest.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new IdlingResourceForTest();

            viewModel.setIdlingResourceInstance(mIdlingResource);
        }
        return mIdlingResource;
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Utilities utilities = new Utilities();
        Log.d("permission", "result");
        boolean isServiceRunning = utilities.isMyServiceRunning(this, GoogleService.class);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted fine");


                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");


                } else {
                    Intent intent = new Intent(this, GoogleService.class);
                    startService(intent);
                }
            } else {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && !isShowedDetailPermission) {

                    ShowRequestPermissionRationale("Allow Access", " This app will get your location in background even if you are not using and let your circle member know your location on the map.if you deny or if you select Allow only while using app ,some features won't work.so please select Allow all the time.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                    isShowedDetailPermission = true;
                }
            }
        }
        else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted background");


                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");


                } else {
                    Intent intent = new Intent(this, GoogleService.class);
                    startService(intent);
                }
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                Log.d("permission ", "denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isShowedDetailPermission) {

                    ShowRequestPermissionRationale("Allow all the time", "This app will get your location in background even if you are not using and let your circle member know your location on the map.if you deny or if you select Allow only while using app ,some features won't work.so please select Allow all the time.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                    isShowedDetailPermission = true;

                }

            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted");
                setSelectedActivity();
                requestActivityUpdate();

                Intent startactivityIntent = new Intent(this, HomaPageActivity.class);
                startActivity(startactivityIntent);


            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && !isShowedDetailPermissionForActivity) {

                Log.d("permission", "activity denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    ShowRequestPermissionRationale("Please Allow!", "To reduce battery usage , please allow.if you deny, app will get your location all the time even if you are not moving around and that will drain your battery power.by allowing this, app won't collect your location details if you are at your home .if you are in vehicle app will get your location.", new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 3);
                    isShowedDetailPermissionForActivity = true;
                }

            }
        } */
/*else if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {

            Log.i("PermissionsResultRead", "read permission granted");
            pickPhoto();
        }*//*


    }
*/

    public void pickPhoto() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 123);

    }



    private void ShowRequestPermissionRationale(String title, String message, String[] permission, int requestCode) {



/*You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them.

                        "Since background location access has not been granted, this app will not be able to discover beacons in the background.
                        Please go to Settings -> Applications -> Permissions and grant background location access to this app.*/

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //from version 29 we need to ask backround locaion access permission

                           // if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.d("versionGreaterThan29", String.valueOf(Build.VERSION.SDK_INT));
                                ActivityCompat.requestPermissions(MainActivity.this, permission,requestCode);

                        }
                    }
                })

                .setCancelable(false)
                .create()
                .show();
    }




    public void requestActivityUpdate() {

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
       /* transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.TILTING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());*/

        Log.d("onCreate: ", "called");
        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Task<Void> task = ActivityRecognition.getClient(this)
                //. requestActivityUpdates(1000,getPendingIntent());

                .requestActivityTransitionUpdates(request, getPendingIntent());

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success
                        Log.d("onSuccess: ", "requestSuccess!");


                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle error
                        Log.d("error: ", e.getMessage());
                    }
                }
        );
    }

    private PendingIntent getPendingIntent() {
        // Reuse the PendingIntent if we already have it.


        if (mPendingIntent != null) {
            return mPendingIntent;
        }

        Log.d( "getPendingIntent: ","called");
        Intent intent = new Intent(this, ActivityBroadCastReceiver.class);
        intent.setAction("TRANSITION_ACTION_RECEIVER");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }


    public void setSelectedActivity() {

        SharedPreferences selectedFragment = this.getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = selectedFragment.edit();
        editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 12);

        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d( "onActivityResult:1","clalled");
    }
}









