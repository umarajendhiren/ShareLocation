package com.androidapps.sharelocation.hiltDI;

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
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {
    private String DISTANCE_API_BASE_URL = "https://maps.googleapis.com/";
    private String SNAP_TO_ROAD_API_BASE_URL = "https://roads.googleapis.com/v1/";
    private Retrofit retrofitSnapToRoadApi;
    private Retrofit retrofitDiastanceApi;


    @Provides
    public  ParseQuery<ParseObject> ProvideQueryCilceNameInstance() {
        ParseQuery<ParseObject> queryCircleName = new ParseQuery<ParseObject>("CircleName");
        return queryCircleName;
    }


    @Provides
    public  ParseQuery<ParseUser> ProvideQueryParseUserInstance() {
        ParseQuery<ParseUser> queryParseUser = ParseUser.getQuery();
        return queryParseUser;
    }

    @Nullable
    @Provides
  public    List ProvideChannelList() {
        return ParseInstallation.getCurrentInstallation().getList("channels");
    }

    @Singleton
    @Provides
  public  static ParseLiveQueryClient ProvideParseLiveQueryClient() {
        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://sharemylocation.b4a.app/"));


        } catch (URISyntaxException e) {

            if (e != null) {

                Log.d("ProvideParsedis", "disconnect");
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

    @Singleton
    @Provides
    @Named("DistanceServiceInstance")
    public  Retrofit provideDistanceServiceInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        if (retrofitDiastanceApi == null) {


            retrofitDiastanceApi = new Retrofit.Builder()
                    .baseUrl(DISTANCE_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();

        }
        return retrofitDiastanceApi;
        //return retrofit.create(DistanceApiClient.class);
    }
    @Singleton
    @Provides
    @Named("SnapToRoadServiceInstance")
    public Retrofit provideSnapToRoadServiceInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        if (retrofitSnapToRoadApi == null) {


            retrofitSnapToRoadApi = new Retrofit.Builder()
                    .baseUrl(SNAP_TO_ROAD_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();

        }
        return retrofitSnapToRoadApi;
        //return retrofit.create(DistanceApiClient.class);
    }

}
