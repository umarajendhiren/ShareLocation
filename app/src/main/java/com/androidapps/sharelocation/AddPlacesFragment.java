package com.androidapps.sharelocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidapps.sharelocation.BackgroundLocationUpdate.GoogleService;
import com.androidapps.sharelocation.databinding.FragmentAddPlacesBinding;


public class AddPlacesFragment extends Fragment implements View.OnClickListener {

    /*Location permission for next fragment called when this fragment is visble,because pager adapter load two fragment(current and next) at once.
     offScreenLimit is 1 by default.*/
    private static int LOCATION_PERMIISION_FINE = 1;
    private static int LOCATION_PERMIISION_BACKROUND = 2;
    FragmentAddPlacesBinding viewBinding;
    Utilities utilities;
    private boolean isShowedDetailPermission=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        viewBinding = FragmentAddPlacesBinding.inflate(inflater, container, false);

        viewBinding.buttonContinue.setOnClickListener(this);
        View view = viewBinding.getRoot();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d( "handleOnBackPressed: ","called");
                // Handle the back button event
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    Log.d("backstack:", String.valueOf(i));
                    fm.popBackStack();


                }

                SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = selectedFragment.edit();
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment",9);

                editor.apply();



                Toast.makeText( getActivity(),"Press again to exit!",Toast.LENGTH_SHORT).show();
                this.setEnabled(false);
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        utilities=new Utilities();

        CheckLocationAccessPermission();
        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_continue) {
            ((MainActivity) getActivity()).setFragmentForViewPager(10);

        }
    }




    @SuppressLint("MissingPermission")
    private void CheckLocationAccessPermission() {

        if (Build.VERSION.SDK_INT < 23) {
//before 23 we no need to  ask any  permission

            boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

            if (isServiceRunning) {
                Log.d("CheckLocation", "service already running");



            } else {
                Intent intent = new Intent(getActivity(), GoogleService.class);
                getActivity().startService(intent);
            }


        } else if (Build.VERSION.SDK_INT >23 &&  Build.VERSION.SDK_INT < 29) {

            //before 29 we no need to ask background location access permission but need to ask location access permission
            Log.d("versionLessThan29", String.valueOf(Build.VERSION.SDK_INT));
            //if don't have location access permission

            //  ContextCompt,this will make our app backward compatible to the current version.
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permission


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMIISION_FINE);


            }
        }



      else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //from version 29 we need to ask backround locaion access permission

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("versionGreaterThan29", String.valueOf(Build.VERSION.SDK_INT));
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMIISION_BACKROUND);
            }
        }

      else             if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            {
//already have  permission
                boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);

                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");


                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
                }
            }

        }




    }
    /**
     * Shows rationale dialog for displaying why the app needs permission
     * Only shown if the user has denied the permission request previously
     */




//this callback called after the user tap allow or deny permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("permission", "result");
        boolean isServiceRunning = utilities.isMyServiceRunning(getActivity(), GoogleService.class);
        if (requestCode == LOCATION_PERMIISION_FINE ) {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted fine");


                    if (isServiceRunning) {
                        Log.d("CheckLocation", "service already running");


                    } else {
                        Intent intent = new Intent(getActivity(), GoogleService.class);
                        getActivity().startService(intent);
                    }
                } else {

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && !isShowedDetailPermission) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ShowRequestPermissionRationale("Allow Access", " This app will get your location in background even if you are not using and let your circle member know your location on the map.if you deny or if you select Allow only while using app ,some features won't work.so please select Allow all the time.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                        }
                        isShowedDetailPermission = true;
                    }
                }
            }

        else if (requestCode == LOCATION_PERMIISION_BACKROUND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted background");


                if (isServiceRunning) {
                    Log.d("CheckLocation", "service already running");


                } else {
                    Intent intent = new Intent(getActivity(), GoogleService.class);
                    getActivity().startService(intent);
                }
            }
            else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                Log.d("permission ", "denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isShowedDetailPermission) {

                    ShowRequestPermissionRationale("Allow all the time", "This app will get your location in background even if you are not using and let your circle member know your location on the map.if you deny or if you select Allow only while using app ,some features won't work.so please select Allow all the time.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                    isShowedDetailPermission = true;

                }

            }
        }
    }



    private void ShowRequestPermissionRationale(String title, String message, String[] permission, int requestCode) {



/*You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them.

                        "Since background location access has not been granted, this app will not be able to discover beacons in the background.
                        Please go to Settings -> Applications -> Permissions and grant background location access to this app.*/

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //from version 29 we need to ask backround locaion access permission

                            // if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d("versionGreaterThan29", String.valueOf(Build.VERSION.SDK_INT));
                            requestPermissions(permission,requestCode);

                        }
                    }
                })

                .setCancelable(false)
                .create()
                .show();
    }
    }

