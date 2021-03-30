package com.androidapps.sharelocation;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountDetailViewModel extends ViewModel {

    MainRepository repository;


public static MutableLiveData<String> nullCircle = new MutableLiveData<>();

    public static MutableLiveData<String> needToEdit = new MutableLiveData<>();

    @ViewModelInject
    public AccountDetailViewModel(MainRepository mainRepository) {

        repository = mainRepository;



    }



    public void getAccountDetail(Context context) {


        repository.getCurrentUserDetails();

    }

    public MutableLiveData<String> getUserName() {

        return repository.currentUserNameLive;
    }

    public MutableLiveData<Bitmap> getUserDp() {

        return repository.userDpBitmapLive;
    }

    public MutableLiveData<String> getPhoneNumber() {





            return repository.userPhoneLive;



    }

    public MutableLiveData<String> getCountryCode() {





        return repository.countryCodeLive;



    }

    public MutableLiveData<String> getCircleName() {
        if(repository.getSelectedGroupNameLiveData().getValue()==null  || repository.getSelectedGroupNameLiveData().getValue().equals("defaultCircleName")){


         nullCircle.setValue("You are not in any circle");
         return nullCircle;
        }
        else {
            return repository.getSelectedGroupNameLiveData();
        }

    }

    public void leaveCircle(String circleName) {
        repository.leaveCircle(circleName);
    }

    public void deleteUser() {

        repository.leaveFromAllCircle();
    }

    public void resetPassword(String mailId) {

        repository.resetPassword(mailId);
    }

    public void savePhoneNumber(String phone) {
        repository.savePhoneNumber(phone);
    }


    public void saveName(String firstName, String lastName) {

        repository.saveName(firstName,lastName);
    }

    public void sendFeedback(String message) {

        repository.sendFeedback(message);
    }

    public MutableLiveData<Boolean> isNetworkConnected() {
        return repository.isNetworkCOnnectedInstance();


    }
}
