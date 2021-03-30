package com.androidapps.sharelocation;

import android.content.Context;
import android.location.Location;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class MainViewModel extends ViewModel {
    MainRepository repository;


    private MutableLiveData<String> firstNameLiveData;
    private MutableLiveData<String> lastNameLiveData;
    private MutableLiveData<String> emailLiveData;
    private MutableLiveData<String> passwordLiveData;

    private MutableLiveData<String> circleNameLiveData;
    private MutableLiveData<String> yourRoleLiveData;
    private MutableLiveData<String> circleCodeFromParseLiveData;
    private MutableLiveData<String> circleCodeToJoinLiveData;


    public static MutableLiveData<String> abTitle = new MutableLiveData<>();
    public static MutableLiveData<String> createOrjoin = new MutableLiveData<>();
    public static MutableLiveData<Location> onReceiveLocation = new MutableLiveData<>();


    static IdlingResourceForTest idlingResourceForTest;

    Context context;

    @ViewModelInject
    public MainViewModel(MainRepository mainRepository, @ApplicationContext Context context) {
        this.repository = mainRepository;
        this.context = context;

    }

    public MutableLiveData<String> getFirstNameLiveDataInstance() {
        if (firstNameLiveData == null) {
            firstNameLiveData = new MutableLiveData<String>();

        }
        return firstNameLiveData;
    }

    public MutableLiveData<String> getFirstNameLiveData() {


        return firstNameLiveData;
    }

    public MutableLiveData<String> getLastNameLiveDataInstance() {
        if (lastNameLiveData == null) {
            lastNameLiveData = new MutableLiveData<String>();

        }
        return lastNameLiveData;
    }

    public MutableLiveData<String> getLastNameLiveData() {

        return lastNameLiveData;
    }

    public MutableLiveData<String> getEmailLiveDataInstance() {
        if (emailLiveData == null) {
            emailLiveData = new MutableLiveData<String>();

        }
        return emailLiveData;
    }


    public MutableLiveData<String> getEmailLiveData() {

        return emailLiveData;
    }

    public MutableLiveData<String> getPasswordLiveDataInstance() {
        if (passwordLiveData == null) {
            passwordLiveData = new MutableLiveData<String>();

        }
        return passwordLiveData;
    }


    public MutableLiveData<String> getPasswordLiveData() {


        return passwordLiveData;
    }


    public MutableLiveData<String> getCircleNameLiveDataInstance() {
       /* if (circleNameLiveData == null) {
            circleNameLiveData = new MutableLiveData<String>();

        }
        return circleNameLiveData;*/

       return repository.circleNameLive;
    }


    public MutableLiveData<String> getCircleNameLiveData() {


        return circleNameLiveData;
    }


    public MutableLiveData<String> getCircleCodeLiveDataInstance() {
     /*   if (circleCodeFromParseLiveData == null) {
            circleCodeFromParseLiveData = new MutableLiveData<String>();

        }
        return circleCodeFromParseLiveData;*/
        return repository.inviteCodeLive;
    }


    public MutableLiveData<String> getCircleCodeLiveData() {


        return circleCodeFromParseLiveData;
    }

    public MutableLiveData<String> getYourRoleLiveDataInstance() {
        if (yourRoleLiveData == null) {
            yourRoleLiveData = new MutableLiveData<String>();

        }
        return yourRoleLiveData;
    }


    public MutableLiveData<String> getYourRoleLiveData() {


        return yourRoleLiveData;
    }


    public MutableLiveData<String> getCircleCodeToJoinLiveDataInstance() {
        if (circleCodeToJoinLiveData == null) {
            circleCodeToJoinLiveData = new MutableLiveData<String>();

        }
        return circleCodeToJoinLiveData;
    }


    public MutableLiveData<String> getCircleCodeToJoinLiveData() {


        return circleCodeToJoinLiveData;
    }


    public void createNewCircle() {


        //repository.createNewGroup(circleNameLiveData.getValue(), circleCodeFromParseLiveData.getValue());
        repository.createNewGroup(repository.circleNameLive.getValue(),repository.inviteCodeLive.getValue());
    }


    public LiveData<List<UserDetailsPojo>> getAdminDetailFromUserObject(String adminId, String circleName, String enteredCircleCode, Context context) {

        return repository.getExistingMemberDetail(adminId, circleName, enteredCircleCode);
    }

    public void joinWithCircle() {

        repository.join();
    }


    public void setIdlingResourceInstance(IdlingResourceForTest idlingResourceInstance) {

        idlingResourceForTest = idlingResourceInstance;


    }

    public IdlingResourceForTest getIdlingResourceInstance() {
        return idlingResourceForTest;

    }


    public void savePlaceInServer(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude) {

        repository.savePlaceGeoPointInServer(addressTitle, selectedAddressLatitude, selectedAddressLongitude);
    }


    public MutableLiveData<List<UserDetailsPojo>> getAllCircleName() {

        return repository.getAllCircleName(context);
    }

    public void setSelectedCircleName(String circleName, String inviteCode) {
        repository.setSelectedCircleName(circleName, inviteCode);
    }

    public LiveData<List<UserDetailsPojo>> liveCircleName() {
        return repository.circleNameAndDpLive;
    }


    public void signUpWithoutEmial(String firstname,String lastname,String password) {


    }
}

