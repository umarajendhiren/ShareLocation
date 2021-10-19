package com.androidapps.sharelocation.SnapToRoadApiClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface SnapToRoadService {

        @GET("snapToRoads?")
        Call<SnapToRoadResponse> getSnapToRoadJsonResult(
                @QueryMap Map<String, String> snappedPath
        );


}
