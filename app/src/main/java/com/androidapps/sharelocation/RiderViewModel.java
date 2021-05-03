package com.androidapps.sharelocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseUser;

import java.util.List;

import dagger.hilt.android.qualifiers.ApplicationContext;

import static android.content.Context.MODE_PRIVATE;

public class RiderViewModel extends ViewModel {

MainRepository repository;
Context context;
public static MutableLiveData<String> disConnectOrCallRider=new MutableLiveData<>();
public static MutableLiveData<String> startOrCallDriver=new MutableLiveData<>();
public static MutableLiveData<FragmentManager> fragmentManager=new MutableLiveData<>();


DriverMapFragment driverMapFragment;


    @ViewModelInject
    public RiderViewModel(MainRepository mainRepository, @ApplicationContext Context context) {
        this.repository = mainRepository;
        this.context = context;
//this.driverMapFragment=driverMapFragment;

    }

    public boolean isUserDriver(){

        SharedPreferences isDriver = context.getSharedPreferences("isDriver", MODE_PRIVATE);
        String isUserDriver = isDriver.getString("isUserDriver", "false");
        Log.d("isUserDriverttt: ", isUserDriver);
        if(isUserDriver.equals("true")){
            return true;
        }
        else return false;
    }


    public LiveData<List<UserDetailsPojo>> getDriverDetailFromUserObject(String adminId, String circleName, String enteredCircleCode, Context context) {

        return repository.getExistingMemberDetail(adminId, circleName, enteredCircleCode);
    }

    public void addRiderMarker(GoogleMap map) {

     repository.getRiderMarker(map);
    }

    public MutableLiveData<String> getDriverCode() {
        return repository.getDriverCodeInstance();
    }

    public void setZoomLevelForMarker(float zoom) {
        repository.setZoomLevelForMarker(zoom);
    }

    public MutableLiveData<List<UserDetailsPojo>> getDriverList() {
return repository.getDriverDetailsListLiveData();
    }

    public void addDriverMarker(GoogleMap map) {
        Log.d( "addDriverMarker","called");
        repository.AddDriverMarker(map);
    }

    public void setCurrentUserAsDriver() {
        repository.setCurrentUserAsDriver();
    }

    public void setRiderDriverMapFalse() {

        repository.isItriderMapFragment=false;
        repository.isItDriverMapFragment=false;
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

    public MutableLiveData<Boolean> isDriverAvailable() {

        return  repository.isDriverCheckedIn(ParseUser.getCurrentUser().getObjectId());


    }

    public MutableLiveData<Boolean> isDriverAvalibleLive(){

        return repository.getIsDriverAvailable();

    }


    public void disConnect(String userId) {
        Log.d( "disConnect",userId);
        repository.disConnectUser(userId);
/*if(disConnectOrCallRider.getValue().equals("CallRider")){

    Log.d( "call rider","number");

   repository.callUser(userId);
}
else if(disConnectOrCallRider.getValue().equals("DisConnect"))
        repository.disConnectUser(userId);*/
    }

    public void callUser(String userId){
        Log.d("callUser:t ",userId);
        repository.callUser(userId);
    }
    public void startDriving(String userId, double latitide, double longitude) {
        Log.d( "startDriving",userId);
        Log.d( "latitude", String.valueOf(latitide));
        Log.d( "longitude", String.valueOf(longitude));


        repository.startDriving(userId,latitide,longitude);

      /*  if(startOrCallDriver.getValue().equals("CallDriver")){

            Log.d( "call Driver","number");
//view.setOnClickListener(onPhoneCallClickInterface);
//onPhoneCallClickInterface.onCall(userId);
           // driverMapFragment.checkCallPermissiionGranted();
            //repository.callUser(userId);
        }
        else if(startOrCallDriver.getValue().equals("StartDriving"))
            repository.startDriving(userId,latitide,longitude);*/
    }

    public void senDriverNearByNotification() {

        repository.senDriverNearByNotification();
    }

    public void isHeDriver(String userId) {
       repository.isHeDriver(userId);
    }


    public void subscribeForDriverChannel(String driverObjectId) {

        repository.subscribeForDriverChannel(driverObjectId);
    }

    public void setDriverNotification(String driverId){

       // repository.getAllSavedBusStops(driverId);

        Intent busStopDialog=new Intent(context,SetBusStopNotificationActivity.class);
        busStopDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(busStopDialog);


       /* BusStopsDialog  dialog = new BusStopsDialog();

        dialog.show(fragmentManager.getValue(), "dialog");
*/




    }


    public void driverRouteMap(String driverId){


        Log.d( "driverRouteMap:",driverId);
        Intent driverRoute=new Intent(context,DriverLiveRoute.class);
        driverRoute.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        driverRoute.putExtra("DriverId",driverId);

        context.startActivity(driverRoute);
    }

    public void getDriverLiveLocation(GoogleMap mMap, String driverId) {
        repository.getDriverLiveRoute(mMap,driverId);
    }

    public void setRouteMapFalse() {
        repository.isItRouteMap=false;
    }
}


