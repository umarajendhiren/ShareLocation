package com.androidapps.sharelocation;

import com.androidapps.sharelocation.model.StringToJsonSerialization;

public interface OnClickCallListener {
    void onCall(String userId,double latitide, double longitude);
    void onDisconnect(String userId);
    void onClickRouteName(StringToJsonSerialization routeDetails, int clickPosition);
}
