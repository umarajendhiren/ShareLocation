package com.androidapps.sharelocation;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anupamchugh on 27/11/15.
 */

public class DirectionsJSONParser {

  public static List<String> polyPointsString=new ArrayList<>();

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */


    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>() ;


       /* JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;*/


       // List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
        JSONArray routeArray = null;
        try {
            routeArray = jObject.getJSONArray("routes");
            Log.d( "parse:", String.valueOf(routeArray.length()));
            Log.d("parse:", String.valueOf(jObject));
for(int i=0;i<routeArray.length();i++) {
    JSONObject routesObj = routeArray.getJSONObject(i);

    JSONArray legsArray = routesObj.getJSONArray("legs");
    for (int j = 0; j < legsArray.length(); j++) {

        JSONObject legArrayFirstObject = legsArray.getJSONObject(j);

        JSONObject distOb = legArrayFirstObject.getJSONObject("distance");
        JSONObject timeOb = legArrayFirstObject.getJSONObject("duration");

        Log.i("Diatance :", distOb.getString("text"));
        Log.i("Time :", timeOb.getString("text"));

        JSONArray jSteps = legArrayFirstObject.getJSONArray("steps");
        polyPointsString.clear();
        for (int k = 0; k < jSteps.length(); k++) {
            JSONObject stepFirstObject = jSteps.getJSONObject(k);

            JSONObject polyObject = stepFirstObject.getJSONObject("polyline");
            String polyPoints = String.valueOf(polyObject.get("points"));
            Log.i("polyline :", polyPoints);
            polyPointsString.add(polyPoints);

            List<LatLng> list = decodePoly(polyPoints);



            //  * Traversing all points
            for (int l = 0; l < list.size(); l++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                path.add(hm);

                Log.d("parse:path", String.valueOf(path.get(l)));
            }
        }

        routes.add(path);
    }
}

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();


        }




     /*   try {

            jRoutes = jObject.getJSONArray("routes");

           // * Traversing all routes
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                Log.d( "parse:leg", String.valueOf(jLegs.length()));
                //List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

//                * Traversing all legs
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jRoutes.get(j)).getJSONArray("steps");
                    Log.d( "parse:step", String.valueOf(jSteps.length()));
                   // * Traversing all steps
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        Log.d( "poly:",polyline);

                      //  * Traversing all points
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);

                            Log.d("parse:path", String.valueOf(path.get(l)));
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }*/
        Log.d( "parse:rout", String.valueOf(routes.size()));
        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        Log.d("decodePoly: ", String.valueOf(poly.size()));

        return poly;
    }


/*
    public void animateMarker(final LatLng toPosition, Marker marker, final boolean hideMarke) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarke) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }*/
}