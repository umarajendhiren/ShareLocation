/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.androidapps.sharelocation;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import java.util.ArrayList;

import dagger.hilt.android.HiltAndroidApp;
//Kicks off Hilt code generation.
@HiltAndroidApp
public class StarterApplication extends Application implements LifecycleObserver {




  /*add this application class to setUp parse server
  * add parse server dependency
  * add this application in manifest like android:name=".StarterApplication"
  *  android:usesCleartextTraffic="true" to access server url start with http://*/
  @Override
  public void onCreate() {
    super.onCreate();
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("tvo4tsUdmH3YSI7iUgewB41l7eIDKn3wLTg8oOld")
            .clientKey("6Gnexs6CgHFE6ilZMLbJCaIaOcV2efGds0Lq1bJs")

            .server("https://parseapi.back4app.com/")

            .build()
    );


/*
    final HashMap<String, String> params = new HashMap<>();
 ParseLiveQueryClient   parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/"));
    params.put("event", " " );*/





    Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
  /*  if(Parse.LOG_LEVEL_ERROR==Log.ERROR){
      Log.d("onCreate:erre","called");

      try {
        ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/")).disconnect();
      } catch (URISyntaxException e) {
        e.printStackTrace();
        Log.d("ProvideParsedis","disconnect");

      }
    }*/

   /* if(Parse.LOG_LEVEL_ERROR==Log.ERROR){

      Log.d("onCreate:erre","called");
      final HashMap<String, String> params = new HashMap<>();
      ParseCloud.callFunctionInBackground("onLiveQueryEvent", params, new FunctionCallback<Object>() {


        @Override
        public void done(Object object, ParseException e) {
          if(e==null){


            Log.d( "onLiveQueryEvent","called");


          }

          else if(e!=null){

            Log.d( "onLiveQueryEvent",e.getMessage());


          }
        }
      });


    }*/


    ArrayList<String> channels = new ArrayList<>();
    channels.add("MyCircle");
    final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
// don't forget to change the line below with the sender ID you obtained at Firebase
    installation.put("GCMSenderId", "93204905876");
  //  installation.put("channels", channels);
    installation.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if(e == null)
        {


                Log.d("INSTALATION_LOG", installation.getDeviceToken()+"   "+installation.getInstallationId()+"    "+ installation.getPushType());

        }
        else
        {
          e.printStackTrace();
        }
      }
    });




      ParseACL defaultACL = new ParseACL();
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

/*
      FirebaseApp.initializeApp(this);

    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
      @Override
      public void onComplete(@NonNull Task<InstanceIdResult> task) {
        if (!task.isSuccessful()) {
          Log.w( "Fetchinokenfailed", task.getException());
          return;
        }

        // Get new FCM registration token
        String token = task.getResult().getToken();

             *//* // Log and toast
              String msg = getString(R.string.msg_token_fmt, token);
              Log.d(TAG, msg);*//*
        Log.d( "onComplete:id",token);
        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
      }
    });*/




  }

  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  public void onAppBackgrounded() {
    //App in background
    Log.d( "onAppBackgrounded: ","called");
    SharedPreferences isInBackground=getSharedPreferences("isInBackground",MODE_PRIVATE);
    SharedPreferences.Editor editor=isInBackground.edit();
    editor.putString("Background","true");
    editor.apply();

  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void onAppForegrounded() {
    // App in foreground
    Log.d("onAppForegrounded: ","called");
    SharedPreferences isInBackground=getSharedPreferences("isInBackground",MODE_PRIVATE);
    SharedPreferences.Editor editor=isInBackground.edit();
    editor.putString("Background","false");
    editor.apply();
  }
}
