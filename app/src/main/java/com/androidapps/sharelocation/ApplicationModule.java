package com.androidapps.sharelocation;

import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class ApplicationModule {



    @Provides
    ParseQuery<ParseObject> ProvideQueryCilceNameInstance() {
        ParseQuery<ParseObject> queryCircleName = new ParseQuery<ParseObject>("CircleName");
        return queryCircleName;
    }


    @Provides
    ParseQuery<ParseUser> ProvideQueryParseUserInstance() {
        ParseQuery<ParseUser> queryParseUser = ParseUser.getQuery();
        return queryParseUser;
    }

    @Nullable
    @Provides
    List ProvideChannelList() {
        return ParseInstallation.getCurrentInstallation().getList("channels");
    }

    @Singleton
    @Provides
    ParseLiveQueryClient ProvideParseLiveQueryClient() {
        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/"));





        } catch (URISyntaxException e) {

if(e!=null){

    Log.d("ProvideParsedis","disconnect");
   // parseLiveQueryClient.disconnect();
   // e.printStackTrace();
}




           // e.getReason();
        }

      /*  catch (Exception exception){

            Log.d("ProvideParseerror",exception.getMessage());
        }*/
        return parseLiveQueryClient;
    }


}
