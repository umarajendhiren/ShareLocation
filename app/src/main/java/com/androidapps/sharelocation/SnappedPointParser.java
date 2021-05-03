package com.androidapps.sharelocation;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnappedPointParser {


    public List<List<HashMap<String, String>>> parseSnappedPoints(JSONObject jObject) {

        List<List<HashMap<String, String>>> snappedList = new ArrayList<List<HashMap<String, String>>>();
       /* JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;*/


        //List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
        JSONArray routeArray = null;
        try {
            routeArray = jObject.getJSONArray("snappedPoints");

            Log.d("routArray", String.valueOf(routeArray.length()));
            if (routeArray.length() > 0) {

                for (int i = 0; i < routeArray.length(); i++) {
                    JSONObject FirstObject = routeArray.getJSONObject(i);

JSONObject locationObject=FirstObject.getJSONObject("location");

                    Log.d( "locationObject", String.valueOf(locationObject.length()));


                    //  * Traversing all points

                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("lat", String.valueOf(locationObject.get("latitude")));
                    hm.put("lng", String.valueOf(locationObject.get("longitude")));
                    path.add(hm);

                    Log.d("parse:path", String.valueOf(path.get(i)));


                }


                snappedList.add(path);
            }

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();


        }
        Log.d("snappedList: ", String.valueOf(snappedList.size()));
        return snappedList;
    }
}


