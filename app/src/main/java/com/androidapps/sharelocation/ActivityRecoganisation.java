package com.androidapps.sharelocation;

import android.Manifest;
import android.app.PendingIntent;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecoganisation extends Fragment {

     PendingIntent mPendingIntent;

    List<ActivityTransition> transitions = new ArrayList<>();
    private boolean isShowedDetailPermissionForActivity=false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_recoganisation,container,false);




        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            Log.d("onCreate: ", "check");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d("onCreate: ", "request");
                requestPermissions( new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 3);
            } else {
                setSelectedActivity();
                requestActivityUpdate();

                Intent startactivityIntent = new Intent(getActivity(), HomaPageActivity.class);
                startActivity(startactivityIntent);


            }
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

            // setSelectedActivity();
            requestActivityUpdate();

            Intent startactivityIntent = new Intent(getActivity(), HomaPageActivity.class);
            startActivity(startactivityIntent);

        }





          OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Log.d("handleOnBackPressed: ", "called");
                // Handle the back button event
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    Log.d("backstack:", String.valueOf(i));
                    fm.popBackStack();


                }

                SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


                SharedPreferences.Editor editor = selectedFragment.edit();
                editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 11);

                editor.apply();


                Toast.makeText(getActivity(), "Press again to exit!", Toast.LENGTH_SHORT).show();
                this.setEnabled(false);

                requireActivity().onBackPressed();
               // getActivity().finish();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return view;
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

        Task<Void> task = ActivityRecognition.getClient(getActivity())
                //. requestActivityUpdates(1000,getPendingIntent());

                .requestActivityTransitionUpdates(request, getPendingIntent());

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Handle success
                        Log.d("onSuccess: ", "requestSuccess!");




                      /*  Intent intent = new Intent();
                        intent.setAction("ACTIVITY_TRANSITIONS");
                        List<ActivityTransitionEvent> events = new ArrayList<>();
                        ActivityTransitionEvent transitionEvent;
                        transitionEvent = new ActivityTransitionEvent(DetectedActivity.STILL,
                                ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos());
                        events.add(transitionEvent);
                        transitionEvent = new ActivityTransitionEvent(DetectedActivity.WALKING,
                                ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos());
                        events.add(transitionEvent);
                        ActivityTransitionResult resultt = new ActivityTransitionResult(events);
                        SafeParcelableSerializer.serializeToIntentExtra(resultt, intent,
                                "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT");
                        sendBroadcast(intent);*/
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
        Intent intent = new Intent(getActivity(), ActivityBroadCastReceiver.class);
        intent.setAction("TRANSITION_ACTION_RECEIVER");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }


    public void setSelectedActivity() {

        SharedPreferences selectedFragment = getActivity().getSharedPreferences("SelectedFragment", Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = selectedFragment.edit();
        editor.putInt("com.androidapps.sharelocation.LastSelectedFragment", 12);

        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "granted");
                setSelectedActivity();
                requestActivityUpdate();

                Intent startactivityIntent = new Intent(getActivity(), HomaPageActivity.class);
                startActivity(startactivityIntent);


            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED && !isShowedDetailPermissionForActivity) {

                Log.d("permission", "activity denied");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    ShowRequestPermissionRationale("Please Allow!", "To reduce battery usage , please allow.if you deny, app will get your location all the time even if you are not moving around and that will drain your battery power.By allowing this, app won't collect your location details if you are at your home .If you are in vehicle , app will get your location.", new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 3);
                    isShowedDetailPermissionForActivity = true;
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