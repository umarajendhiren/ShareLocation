package com.androidapps.sharelocation;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


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
}
