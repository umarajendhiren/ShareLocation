package com.androidapps.sharelocation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;

import static com.parse.Parse.getApplicationContext;

public class ParseReceiver extends ParsePushBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String intentMessage=intent.getStringExtra(KEY_PUSH_DATA);
       // String intentIcon=intent.getStringExtra(PROPERTY_PUSH_ICON);

       // intent.putExtra(PROPERTY_PUSH_ICON,R.drawable.ic_8888);

        intent.getClipData();
        intent.getComponent();
        intent.getCategories();
        Log.d("onReceive:intentMe ",intentMessage);
        //Log.d("onReceive:intentIcon ", intentIcon);

//sendNotification(context,intentMessage,intentchannal);

    }

   /* @Override
    protected NotificationCompat.Builder getNotificationBuilder(Context context, Intent intent) {
        return super.getNotificationBuilder(context, intent);
    }*/

    private void sendNotification(Context context, String notificationDetails, String channal) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ShareLocation";
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(channal, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), HomaPageActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_2222)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_8888))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channal); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
