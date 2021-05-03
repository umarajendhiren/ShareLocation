package com.androidapps.sharelocation;


import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import java.util.HashMap;
import java.util.List;

public class StringToJsonSerialization {

    String placeName;
    String routeName;
    LatLng origin,destination;
    HashMap<String,LatLng> wayPoints;
    String originPlaceName;

    public String getOriginPlaceName() {
        return originPlaceName;
    }

    public void setOriginPlaceName(String originPlaceName) {
        this.originPlaceName = originPlaceName;
    }

    public String getDestinationPlaceName() {
        return destinationPlaceName;
    }

    public void setDestinationPlaceName(String destinationPlaceName) {
        this.destinationPlaceName = destinationPlaceName;
    }

    String destinationPlaceName;

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public HashMap<String,LatLng>getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(HashMap<String,LatLng> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public List<String> getPolyPoints() {
        return polyPoints;
    }

    public void setPolyPoints(List<String> polyPoints) {
        this.polyPoints = polyPoints;
    }

    public void setGeoPoint(ParseGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    List<String> polyPoints;


    public boolean isNotificationOn() {
        return isNotificationOn;
    }

    public void setNotificationOn(boolean notificationOn) {
        isNotificationOn = notificationOn;
    }

    boolean isNotificationOn;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    String objectId;
    ParseGeoPoint geoPoint;


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public ParseGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(double latitude, double longitude) {
        geoPoint = new ParseGeoPoint(latitude, longitude);

    }


}
