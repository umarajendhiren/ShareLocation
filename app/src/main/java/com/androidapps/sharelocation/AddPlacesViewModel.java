package com.androidapps.sharelocation;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;


public class AddPlacesViewModel extends ViewModel {
    public static MutableLiveData<String> abTitle = new MutableLiveData<>();
    public static MutableLiveData<String> latitude = new MutableLiveData<>();
    public static MutableLiveData<String> longitude = new MutableLiveData<>();


    MainRepository mainRepository;

    @ViewModelInject
    public AddPlacesViewModel(MainRepository mainRepository) {
        this.mainRepository = mainRepository;
    }

    public void savePlaceInServer(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude) {

        mainRepository.savePlaceGeoPointInServer(addressTitle, selectedAddressLatitude, selectedAddressLongitude);
    }

    public void updateAddress(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude, String postionToChange) {


        mainRepository.updateAddress(addressTitle, selectedAddressLatitude, selectedAddressLongitude, postionToChange);
    }

    public void addBusStop(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude) {
        mainRepository.addBusStop(addressTitle,selectedAddressLatitude,selectedAddressLongitude);
    }

    public void updateBusStopAddress(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude,int positionToUpdate) {
        mainRepository.updateBusStopAddress(addressTitle,selectedAddressLatitude,selectedAddressLongitude,positionToUpdate);
    }

   /* public void updateRouteDetail(StringToJsonSerialization roteDetail) {
        Log.d ("update", String.valueOf(roteDetail));
        mainRepository.getUpdateRoute().setValue(roteDetail);

        if(roteDetail.routeName!=null) {
            Log.d ("updaterou", String.valueOf(roteDetail));
            mainRepository. getRoteDetail().setRouteName(roteDetail.routeName);
            mainRepository.getRoteDetail().setOrigin(roteDetail.getOrigin());
            mainRepository. getRoteDetail().setDestination(roteDetail.getDestination());
            mainRepository. getRoteDetail().setPolyPoints(roteDetail.getPolyPoints());
            mainRepository. getRoteDetail().setWayPoints(roteDetail.getWayPoints());

        }}*/

    public StringToJsonSerialization getRouteDetail() {
        return  mainRepository.getRoteDetail();
    }

    public void setUpdateRouteDetail(StringToJsonSerialization updateRouteDetail) {
        mainRepository.getUpdateRoute().setValue(updateRouteDetail);

    }

    public List<StringToJsonSerialization> getStopList() {
        return  mainRepository.getStopList();
    }
    public void setBusStopLive(List<StringToJsonSerialization> busStopLive) {


        mainRepository.busStopList.addAll(busStopLive);
        mainRepository.getBusStopLive().postValue(busStopLive);

    }
}
