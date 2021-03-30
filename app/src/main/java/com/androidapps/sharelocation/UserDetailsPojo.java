package com.androidapps.sharelocation;

import android.graphics.Bitmap;

public class UserDetailsPojo {
    String userName;

    public String getDistanceInMiles() {
        return distanceInMiles;
    }

    public void setDistanceInMiles(String distanceInMiles) {
        this.distanceInMiles = distanceInMiles;
    }

    String distanceInMiles;

double latitude;
double destinationLatitude;

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    double destinationLongitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    double longitude;
    public int getPlaceIcon() {
        return placeIcon;
    }

    public void setPlaceIcon(int placeIcon) {
        this.placeIcon = placeIcon;
    }

    int placeIcon;
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    String objectId;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    String inviteCode;

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    String circleName;
    String userCurrentLocation;

    public UserDetailsPojo() {
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    String userObjectId;


    public static UserDetailsPojo getUserDetailsPojoInstance(){

        return new UserDetailsPojo();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCurrentLocation() {
        return userCurrentLocation;
    }

    public void setUserCurrentLocation(String userCurrentLocation) {
        this.userCurrentLocation = userCurrentLocation;
    }

    public Bitmap getUserDp() {
        return userDp;
    }

    public void setUserDp(Bitmap userDp) {
        this.userDp = userDp;
    }

    Bitmap userDp;
}
