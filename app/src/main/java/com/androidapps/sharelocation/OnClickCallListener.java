package com.androidapps.sharelocation;

public interface OnClickCallListener {
    void onCall(String userId,double latitide, double longitude);
    void onDisconnect(String userId);
}
