package com.androidapps.sharelocation;

public class BusStopsPojo {
    String busStopName;

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public double getBusStopLatitude() {
        return busStopLatitude;
    }

    public void setBusStopLatitude(double busStopLatitude) {
        this.busStopLatitude = busStopLatitude;
    }

    public double getBusStopLongitude() {
        return busStopLongitude;
    }

    public void setBusStopLongitude(double busStopLongitude) {
        this.busStopLongitude = busStopLongitude;
    }

    double  busStopLatitude;
    double busStopLongitude;
}
