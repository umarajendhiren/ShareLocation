package com.androidapps.sharelocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeoBroadcastReceiver";
    ArrayList triggeringGeofencesIdsList;
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(

                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.

            Log.i(TAG, geofenceTransitionDetails);
            //sendNotification(geofenceTransitionDetails);
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
                sendArrivedNPushNotification();
            }

            else  if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
                sendLeftPushNotification();
            }

        } else {
            // Log the error.
            Log.e("error","somthing wrong");
        }
    }

    private void sendLeftPushNotification() {

        for(int i=0;i<triggeringGeofencesIdsList.size();i++) {
            Log.d("sendPushNotification: ", String.valueOf(
                    triggeringGeofencesIdsList.get(i)));
            final HashMap<String, String> params = new HashMap<>();

            String triggeredPlace = (String) triggeringGeofencesIdsList.get(i);
            if(triggeredPlace.contains(" ")) {
               /* placeName= placeName.substring(0, placeName.indexOf(" "));
                Log.d("placeName: ",placeName);
                params.put("userLocation", placeName);*/


                String placeName = triggeredPlace.substring(0, triggeredPlace.indexOf(" "));


               // Log.d("placeName: ", triggeredPlace);

                if (placeName.equals("BusStop")) {

                    //send diver left
                    Log.d("sendLeftPu, ", "send Bus stop");

                   String busStop=triggeredPlace.replace("BusStop","").trim();

                    Log.d("placeName: ",busStop);
                    sendDriverLeftPush(busStop);

                    return;
                } else {

                    String circleName = triggeredPlace.substring(1, triggeredPlace.indexOf(" "));
                    Log.d("circleName: ", circleName);
                    params.put("userLocation", placeName);
                    params.put("circleName", circleName);
                }
            }




            Log.d("deviceToken: ", ParseInstallation.getCurrentInstallation().getDeviceToken());
            params.put("deviceToken", ParseInstallation.getCurrentInstallation().getDeviceToken());
            /*query.notEqualTo("deviceToken", request.params.deviceToken);*/
            DateTimeFormatter dtf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dtf = DateTimeFormatter.ofPattern(" hh:mm a");
                LocalDateTime time = LocalDateTime.now();
                dtf.format(time);
                Log.d("time: ",dtf.format(time));
                params.put("time",  dtf.format(time));
            }


            //Log.d("usernearbyLocation: ", serialization.getPlaceName());
           // params.put("userLocation", String.valueOf(triggeringGeofencesIdsList.get(i)));

            // Log.d("channal: ", serialization.getPlaceName());
            params.put("channels", String.valueOf(triggeringGeofencesIdsList.get(i)));


            params.put("userName", ParseUser.getCurrentUser().getUsername());

            ParseCloud.callFunctionInBackground("leftPush", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object response, ParseException exc) {
                    if (exc == null) {
                        Log.d("done: ", "Push message sent!!!");
                    } else {
                        // Something went wrong
                        //Toast.makeText(GeofenceBroadcastReceiver.class, exc.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error: ", String.valueOf(exc));

                    }
                }
            });
        }
    }




    private void sendDriverArrivedPush(String busStopName) {

        final HashMap<String, String> params = new HashMap<>();

        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern(" hh:mm a");
            LocalDateTime time = LocalDateTime.now();
            dtf.format(time);
            Log.d("time: ",dtf.format(time));
            params.put("time",  dtf.format(time));
        }


        //Log.d("usernearbyLocation: ", serialization.getPlaceName());
        // params.put("userLocation", String.valueOf(triggeringGeofencesIdsList.get(i)));

        // Log.d("channal: ", serialization.getPlaceName());

        Log.d("channal: ",  "BusStop " + busStopName + " " + ParseUser.getCurrentUser().getObjectId());
        params.put("channels", "BusStop " + busStopName + " " + ParseUser.getCurrentUser().getObjectId());


        params.put("userName", ParseUser.getCurrentUser().getUsername());

        params.put("busStopName",busStopName);

        Log.d( "busStopName: ",busStopName);

        ParseCloud.callFunctionInBackground("driverArrived", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null) {
                    Log.d("done: ", "Push message sent!!!");
                } else {
                    // Something went wrong
                    //Toast.makeText(GeofenceBroadcastReceiver.class, exc.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error: ", String.valueOf(exc));

                }
            }
        });
    }





    private void sendDriverLeftPush(String busStopName) {

        final HashMap<String, String> params = new HashMap<>();

        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern(" hh:mm a");
            LocalDateTime time = LocalDateTime.now();
            dtf.format(time);
            Log.d("time: ",dtf.format(time));
            params.put("time",  dtf.format(time));
        }


        //Log.d("usernearbyLocation: ", serialization.getPlaceName());
        // params.put("userLocation", String.valueOf(triggeringGeofencesIdsList.get(i)));

         Log.d("channal: ",  "BusStop " + busStopName + " " + ParseUser.getCurrentUser().getObjectId());


        params.put("channels", "BusStop " + busStopName + " " + ParseUser.getCurrentUser().getObjectId());


        params.put("userName", ParseUser.getCurrentUser().getUsername());

        params.put("busStopName",busStopName);

        Log.d( "busStopName: ",busStopName);

        ParseCloud.callFunctionInBackground("driverLeft", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if (exc == null) {
                    Log.d("done: ", "Push message sent!!!");
                } else {
                    // Something went wrong
                    //Toast.makeText(GeofenceBroadcastReceiver.class, exc.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("error: ", String.valueOf(exc));

                }
            }
        });
    }


    private String getGeofenceTransitionDetails(

            int geofenceTransition,
            List<Geofence> triggeringGeofences) {



        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        triggeringGeofencesIdsList = new ArrayList();


        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            Log.d( "transitionDetails: ",geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        Log.d("getGeo ",geofenceTransitionString);
        Log.d("getGeo ",triggeringGeofencesIdsString);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "transition - enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "transition - exit";
            default:
                return "unknown transition";
        }
}



    private void sendArrivedNPushNotification(
            ) {


        List<String> subscribedChannals = ParseInstallation.getCurrentInstallation().getList("channels");

        //if subscribed channel contains placeName(place name is Channel name) in installation object means notification is ON for that channel.so we can send notification


        for(int i=0;i<triggeringGeofencesIdsList.size();i++) {
            Log.d("sendPushNotification: ", String.valueOf(triggeringGeofencesIdsList.get(i)));


            final HashMap<String, String> params = new HashMap<>();

            String triggeredPlace = (String) triggeringGeofencesIdsList.get(i);

            if(triggeredPlace.contains(" ")){
               String placeName= triggeredPlace.substring(0, triggeredPlace.indexOf(" "));





                if(placeName.equals("BusStop")){
                  String[] spliitedPlaceName= triggeredPlace.split(" ");

                  //for(int j=0;j<spliitedPlaceName.length;j++){

                  String  busStop=triggeredPlace.replace("BusStop","").trim();

                    Log.d("placeName: ",busStop);


                    //send diver left
                    Log.d("sendArrivedPu, ","send Bus stop");

                    sendDriverArrivedPush(busStop);
                    return;
                }

                else {
                    String circleName = triggeredPlace.substring(1, triggeredPlace.indexOf(" "));

                    Log.d("circleName: ", circleName);
                    params.put("userLocation", placeName);
                    params.put("circleName", circleName);
                }
            }

            Log.d("deviceToken: ", ParseInstallation.getCurrentInstallation().getDeviceToken());
            params.put("deviceToken", ParseInstallation.getCurrentInstallation().getDeviceToken());
            /*query.notEqualTo("deviceToken", request.params.deviceToken);*/


            //Log.d("usernearbyLocation: ", serialization.getPlaceName());
           // params.put("userLocation", String.valueOf(triggeringGeofencesIdsList.get(i)));

            // Log.d("channal: ", serialization.getPlaceName());
            params.put("channels", String.valueOf(triggeringGeofencesIdsList.get(i)));

            params.put("userName", ParseUser.getCurrentUser().getUsername());


            DateTimeFormatter dtf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dtf = DateTimeFormatter.ofPattern(" hh:mm a");
                LocalDateTime time = LocalDateTime.now();
                dtf.format(time);
                Log.d("time: ",dtf.format(time));
                params.put("time",  dtf.format(time));
            }




            ParseCloud.callFunctionInBackground("arrivedPush", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object response, ParseException exc) {
                    if (exc == null) {
                        Log.d("done: ", "Push message sent!!!");
                    } else {
                        // Something went wrong
                        //Toast.makeText(GeofenceBroadcastReceiver.class, exc.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error: ", String.valueOf(exc));

                    }
                }
            });
        }
          //  params.put("adminId", adminId);


            // ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
          /*  queryParseUser.getInBackground(updatedUserId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    if (e == null) {
                        updatesLocationUserName = object.getUsername();
                        Log.d("userName: ", updatesLocationUserName);
                        params.put("userName", updatesLocationUserName);

                        // Calling the cloud code function
                        ParseCloud.callFunctionInBackground("arrivedPush", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object response, ParseException exc) {
                                if (exc == null) {
                                    Log.d("done: ", "Push message sent!!!");
                                } else {
                                    // Something went wrong
                                    Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("error: ", String.valueOf(exc));

                                }
                            }
                        });
                    }
                }
            });*/


        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
  /*  private void sendNotification(String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
*/




