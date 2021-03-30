package com.androidapps.sharelocation;

import com.parse.ParseGeoPoint;

public class StringToJsonSerialization {

    String placeName;

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
