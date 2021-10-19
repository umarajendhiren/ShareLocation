package com.androidapps.sharelocation.DistanceApi;

import com.androidapps.sharelocation.PojoForGSONConverter.RouteAndWaypoint;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DistanceApiClient {

        @GET("maps/api/distancematrix/json")
        Call<DistanceResponse> getDistanceInfo(
                @QueryMap Map<String, String> parameters
        );

//URL query string "{parameters}&{waypoints}&key={key}" must not have replace block. For dynamic query parameters use @Query
      //  String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + waypoints + "&key=AIzaSyCGKsTZuZgZm5fXxcOBwIh4Qg7MnTPYqyo";

        @GET("maps/api/directions/json?")
        Call<RouteAndWaypoint> getRouteDetail (@QueryMap Map<String, String> parameters);

}
