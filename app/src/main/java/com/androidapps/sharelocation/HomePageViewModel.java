package com.androidapps.sharelocation;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ObservableArrayList;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.qualifiers.ApplicationContext;

import static android.content.Context.MODE_PRIVATE;

public class HomePageViewModel extends ViewModel {

    MainRepository repository;
    Context mContext;

    public static MutableLiveData<Integer> notificationOnVisibility = new MutableLiveData<>();
    public static MutableLiveData<Integer> notificationOffVisibility = new MutableLiveData<>();
    GeofencingClient geofencingClient;
    ArrayList<Geofence> geofenceList;
    PendingIntent geofencePendingIntent;
    GoogleApiClient mGoogleApiClient;
    public  static ObservableArrayList<String> livepolyString = new ObservableArrayList<>();
    @ViewModelInject
    public HomePageViewModel(MainRepository mainRepository, @ApplicationContext Context context) {

        mContext = context;
        geofencingClient = LocationServices.getGeofencingClient(mContext);
        repository = mainRepository;
        notificationOnVisibility.setValue(View.VISIBLE);
        notificationOffVisibility.setValue(View.GONE);
        geofenceList = new ArrayList<>();

      /*  GoogleApiClient.Builder apiBuild = new GoogleApiClient.Builder(mContext);
         mGoogleApiClient = apiBuild.build();*/


    }

    private void registerGeoFences() {

       // mGoogleApiClient == null  || !mGoogleApiClient.isConnected()

        if( geofenceList==null  || geofenceList.size()==0)
        {
            Log.d( "AddGeoFences: ","return");
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("onSuccess: ","geoFencesAdd");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d( "onFailure: ",e.getMessage().toString());
                    }
                });
    }

    private void unRegisterGeoFences(){

     /*   if(mGoogleApiClient == null  || !mGoogleApiClient.isConnected()  )
        {

            return;
        }*/
        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("onSuccess: ","unregistered");
            }
        });
    }


    public void storeUserCurrentLocationInServer(Context context, Location lastKnownLocation,GoogleMap googleMap) {
        this.mContext = context;
       repository.storeUserCurrentLocationInServer(context, lastKnownLocation, googleMap);
      //  repository.storeUserCurrentLocationInServer(context, lastKnownLocation);
    }

    public void UpdateMapWithMarker(Context applicationContext, GoogleMap mMap, List<String> selectedCircleName) {
        repository.UpdatewithCustomMarker(applicationContext, mMap, selectedCircleName);
    }

    public void setZoomLevelForMarker(float zoomLevelForMarker) {
        repository.setZoomLevelForMarker(zoomLevelForMarker);
    }


    public LiveData<List<UserDetailsPojo>> getGroupMemberList() {
        return repository.getUserDetailsLiveData();

    }

   /* public LiveData<GoogleMap> getMap() {
        return repository.getLiveMap();
    }*/


    public MutableLiveData<String> getSelectedGroupNameLiveData() {
        return repository.getSelectedGroupNameLiveData();
    }

    public void setSelectedGroupNameInviteCodeLiveData(String selectedCircleName, String inviteCode, CircleNameDialogRecyclerView dialogRecyclerView) {


        repository.getSelectedInviteCodeLiveData().setValue(inviteCode);

        repository.getSelectedGroupNameLiveData().setValue(selectedCircleName);



        Log.d( "setSelectedGroupName",selectedCircleName+" "+inviteCode);


        dialogRecyclerView.dismiss();
    }

    public MutableLiveData<String> getSelectedInviteCodeLiveData() {
        return repository.getSelectedInviteCodeLiveData();
    }

    public MutableLiveData<List<UserDetailsPojo>> getAllCircleName(Context context) {
        return repository.getAllCircleName(context);
    }

    public MutableLiveData<String> getCircleNameLive() {
        return repository.getSelectedGroupNameLiveData();
    }
    public MutableLiveData<String> getInviteCodeLive() {
        return repository.getSelectedInviteCodeLiveData();
    }


    public MutableLiveData<List<StringToJsonSerialization>> getSavedPlaces() {

        // repository.getAllPlaceGeoPoints();

    return   repository.placeNameAndGeoPointLive;

       // Log.d( "getSavedPlaces: ",repository.placeNameAndGeoPointLive.getValue().get(0).getPlaceName());
       //return repository.placeNameAndGeoPointLive;
    }

    public MutableLiveData<List<StringToJsonSerialization>> getPlacesLive() {

        return repository.getplaceNameAndGeoPointLive();

        // Log.d( "getSavedPlaces: ",repository.placeNameAndGeoPointLive.getValue().get(0).getPlaceName());
        //return repository.placeNameAndGeoPointLive;
    }

    public void setPlaceLive(){

        repository.placeNameAndGeoPointLive.setValue(null);
    }


    public MutableLiveData<ListViewAddPlaceVisibilityPojo> checkHomeAvailabe(List<String> placeNames) {

        return repository.checkHomeAvailable(placeNames);
    }

    public void removePlaceFromServer(String positionToUpdate,String placeNameRemove) {
        repository.removePlaceFromServer(positionToUpdate,placeNameRemove);
    }

    public void setNotification(boolean isOn, int poition) {
        repository.setNotification(isOn, poition);
    }



    public MutableLiveData<Bitmap> getUserDpLive() {

        repository.getCurrentUserDetails();
        return repository.userDpBitmapLive;
    }


    public void isUserLoggedIn(boolean b) {
        repository.isUserLoggedIn(b);
    }


    public void launchCreateCircleFragment(CircleNameDialogRecyclerView activity) {

        Intent intentCreate = new Intent(activity.getContext(), CreateAndJoinCircleActivity.class);


        intentCreate.putExtra("createCircle", "Create");
        activity.getContext().startActivity(intentCreate);
        activity.dismiss();
    }

    public void launchJoinCircleFragment(CircleNameDialogRecyclerView activity) {
        Intent intentJoin = new Intent(activity.getContext(), CreateAndJoinCircleActivity.class);
        intentJoin.putExtra("joinCircle", "Join");

        activity.getContext().startActivity(intentJoin);
        activity.dismiss();
    }

    public MutableLiveData<Boolean> isSubsribeDForUserLiveQuery() {
        return repository.getSubscribeForUserLiveData();
    }
    public MutableLiveData<Boolean> isSubsribeDForCircleNameLiveQuery() {
        return repository.getSubscribeForCircleNameLiveData();
    }

    public void subscribeForUserLiveQuery() {
        repository.subscribeForUserLiveQuery();
    }

    public void subscribeForCircleNameLiveQuery() {
      repository.subscribeForCircleNameLiveQuery();
    }


    public void AddToGeoFenceList(String key,double latitude,double longitude){
//repository.AddToGeoFenceList(key,latitude,longitude);

     /*   String requestKey=key+" "+repository.circleNameLive.getValue()+repository.inviteCodeLive.getValue();
        Log.d( "AddToGeoFenceList: ",requestKey);
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(requestKey)

                .setCircularRegion(
                        latitude,longitude,100
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        Log.d( "AddToGeoFenceList: ", String.valueOf(geofenceList.size()));

        unRegisterGeoFences();
        registerGeoFences();*/
    }

    public void removeGeoFenceFromList(String key){


        String removeKey=key+repository.circleNameLive.getValue()+repository.inviteCodeLive.getValue();

        for(int i=0;i<geofenceList.size();i++) {
            String requestKey=geofenceList.get(i).getRequestId();


            if (requestKey.equals(removeKey)) {
                Log.d( "removeGeoFence: ",requestKey);
                geofenceList.remove(i);
                Log.d( "remive: ", String.valueOf(geofenceList.size()));

            }
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(mContext,GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public MutableLiveData<List<StringToJsonSerialization>> getGeoFenceLive() {


            return repository.getGeoFenceList();


    }

    public void getAllGeoFences() {
        repository.getAllGeoFences();
    }

    public MutableLiveData<String> getRemovedGeoFence() {
       return repository.getRemovedGeoFenceInstance();
    }

    public void unsubscribeForChannal(String placeName) {
        repository.unsubscribeForChannal(placeName+" "+repository.circleNameLive.getValue()+repository.inviteCodeLive.getValue());
    }

    public void deleteDp(String imageFileName) {
       // repository.deleteDp(imageFileName);
    }

    public void userCurrentLocation(double latitude,double longitude){
        Log.d("userCurrentLocation: ",latitude+" "+longitude);

        Intent streetViewIntent=new Intent(mContext,StreetView.class);

        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);

        streetViewIntent.putExtra("position",bundle);

        mContext.startActivity(streetViewIntent);

    }

    public void getDistance(){

       repository.getDistanceInfo(39.794691666666665,-75.48544166666667,39.82087333333333,-75.54932166666667);

    }

    public void startDriving(String userId,double latitide,double longitude) {
        Log.d( "startDriving",userId);
        Log.d( "latitude", String.valueOf(latitide));
        Log.d( "longitude", String.valueOf(longitude));



        repository.startDriving(userId,latitide,longitude);
    }

    public void isNetworkCOnnected(boolean isConnected) {
        repository.isNetworkCOnnectedInstance().setValue(isConnected);
    }

    public MutableLiveData<Boolean> isConnected(){
        return  repository.isNetworkCOnnectedInstance();
    }

    public void reConnectLiveQuery() {
        repository.reConnectLiveQuery();
    }


    public boolean isUserDriver(){

        SharedPreferences isDriver = mContext.getSharedPreferences("isDriver", MODE_PRIVATE);
        String isUserDriver = isDriver.getString("isUserDriver", "false");
        Log.d("isUserDriverttt: ", isUserDriver);
        if(isUserDriver.equals("true")){
            return true;
        }
        else return false;
    }

    public void getAllSavedBusRoute() {
        Log.d( "getAllSavedBusStops:","called");

        repository.getAllSavedBusRoute(ParseUser.getCurrentUser().getObjectId());
    }

    public MutableLiveData<List<StringToJsonSerialization>> getBusStopLive() {
        return repository.getBusStopLive();

    }
    public void setBusStopLive(List<StringToJsonSerialization> busStopLive) {
       /* if(busStopLive.size()>0){

           // if(repository.getStopList().size()>0)
           // repository.getStopList().clear();
           repository.getStopList().addAll(busStopLive);
            Log.d("setBusStopLive: ", String.valueOf(repository.getStopList().size()));
        }
*/
        repository.busStopList.addAll(busStopLive);

         repository.getBusStopLive().setValue(busStopLive);

    }



    public void setBusStopList(List<StringToJsonSerialization> busStopLive) {


            if (repository.getStopList().size() > 0)
                repository.getStopList().clear();

         if (busStopLive.size() > 0) {
             repository.getStopList().addAll(busStopLive);

         }
        Log.d("setBusStopLive: ", String.valueOf(repository.getStopList().size()));

    }


    public void setExistingBusStopList(List<StringToJsonSerialization> busStopLive) {


        if (repository.existingBusStopList.size() > 0)
            repository.existingBusStopList.clear();

     repository.existingBusStopList.addAll(busStopLive);

     repository.addExistingBusStops();

    }


    public List<StringToJsonSerialization> getBusStopList(){
        return  repository.getStopList();
        }

    public void removeBusStopFromServer(String posionToRemove) {
        repository.removeBusStopFromServer(posionToRemove);
    }

    public void setNotificationForBusStop(boolean isOn, int positionToChange,String addressTitle,double selectedLatitude,double selectedLongitude,String driverId) {
        repository.setNotificationForBusStop(isOn,positionToChange,addressTitle,selectedLatitude,selectedLongitude,driverId);
    }

    public void shareRouteMap(StringToJsonSerialization routeDetail) {

       /* List<String> polyPoints
        repository.shareRouteMap(polyPoints);*/

        repository.shareRouteMap(routeDetail);
    }

    public void unShareRouteMap(StringToJsonSerialization routeDetail) {

       /* List<String> polyPoints
        repository.shareRouteMap(polyPoints);*/

        repository.unShareRoute(routeDetail);
    }


    public void saveRoute(StringToJsonSerialization routeDetail) {

        repository.saveRoute(routeDetail);
    }

    public LiveData<List<StringToJsonSerialization>> getBusRouteLive() {
        return repository.getBusRouteLive();
    }

    public void removeBusRouteFromServer(StringToJsonSerialization roteDetail) {
        Log.d ("removeBusRoute", String.valueOf(roteDetail));
        repository.removeBusRouteFromServer(roteDetail);
    }

    public void updateRouteDetail(StringToJsonSerialization roteDetail,int objectPosition) {
        Log.d ("update", String.valueOf(roteDetail));
        repository.getUpdateRoute().setValue(roteDetail);
        repository.objectPosition=objectPosition;


        if(roteDetail.routeName!=null) {
            Log.d ("updaterou", String.valueOf(roteDetail));
           repository. getRoteDetail().setRouteName(roteDetail.routeName);
            repository.getRoteDetail().setOrigin(roteDetail.getOrigin());
            repository. getRoteDetail().setDestination(roteDetail.getDestination());
            repository. getRoteDetail().setPolyPoints(roteDetail.getPolyPoints());
            repository. getRoteDetail().setWayPoints(roteDetail.getWayPoints());

        }
        Intent startDriverRouteMap=new Intent(mContext,DriverRouteMapActivity.class);
        startDriverRouteMap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(startDriverRouteMap);
     /*   startDriverRouteMap.putExtra("RouteName",roteDetail.getRouteName());
        startDriverRouteMap.putExtra("Origin",roteDetail.getOrigin());
        startDriverRouteMap.putExtra("Destination",roteDetail.getDestination());
        startDriverRouteMap.putExtra("WayPoints",roteDetail.getRouteName());
        startDriverRouteMap.putExtra("RouteName",roteDetail.getRouteName());
        startDriverRouteMap.putExtra("RouteName",roteDetail.getRouteName());*/
    }

    public MutableLiveData<StringToJsonSerialization> getUpdateRouteDetail() {
        return  repository.getUpdateRoute();
    }

    public void setUpdateRouteDetail(StringToJsonSerialization updateRouteDetail) {
        repository.getUpdateRoute().setValue(updateRouteDetail);

    }

    public void updateRouteDetailInServer(StringToJsonSerialization routeDetail) {
        repository.updateRouteDetailInServer(routeDetail);
    }

    public void getAllSavedBusStops() {
        repository.getAllSavedBusStops(ParseUser.getCurrentUser().getObjectId());
    }

    public void addBusStop(String busStopTitle, double latitude, double longitude) {
        repository.addBusStop(busStopTitle,latitude,longitude);
    }

  /*  public MutableLiveData<List<String>> getLiveRoute() {
       // repository.getPolyString();
      *//*  if(repository.getPolyStringLive().getValue()!=null)
            livepolyString.addAll(repository.getPolyStringLive().getValue());*//*
       // return repository.getPolyStringLive();
    }*/
}
