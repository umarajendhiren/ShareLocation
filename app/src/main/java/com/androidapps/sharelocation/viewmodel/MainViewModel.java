package com.androidapps.sharelocation.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidapps.sharelocation.IdlingResourceForTest;
import com.androidapps.sharelocation.repository.MainRepository;
import com.androidapps.sharelocation.model.UserDetailsPojo;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class MainViewModel extends ViewModel {
    MainRepository repository;


    private MutableLiveData<String> firstNameLiveData;
    private MutableLiveData<String> lastNameLiveData;
    private MutableLiveData<String> emailLiveData;
    private MutableLiveData<String> passwordLiveData;


    private MutableLiveData<String> yourRoleLiveData;

    private MutableLiveData<String> circleCodeToJoinLiveData;


    public static MutableLiveData<String> abTitle = new MutableLiveData<>();
    public static MutableLiveData<String> createOrjoin = new MutableLiveData<>();


    public static IdlingResourceForTest idlingResourceForTest;


    Context context;

    @Inject
    public MainViewModel(MainRepository mainRepository, @ApplicationContext Context context) {
        this.repository = mainRepository;
        this.context = context;

    }

    public void createNewCircle() {


       // repository.createNewGroup(repository.circleNameLive.getValue(), repository.inviteCodeLive.getValue());
        repository.createNewGroup(repository.getCircleNameLive().getValue(), repository.getInviteCodeLive().getValue());
    }

    public void joinWithCircle() {

        repository.join();
    }

    public void savePlaceInServer(String addressTitle, double selectedAddressLatitude, double selectedAddressLongitude) {

        repository.savePlaceGeoPointInServer(addressTitle, selectedAddressLatitude, selectedAddressLongitude);
    }

    public LiveData<List<UserDetailsPojo>> getAdminDetailFromUserObject(String adminId, String circleName, String enteredCircleCode, Context context) {

        return repository.getExistingMemberDetail(adminId, circleName, enteredCircleCode);
    }

    public void setSelectedCircleName(String circleName, String inviteCode) {
        repository.setSelectedCircleName(circleName, inviteCode);
    }

    public LiveData<List<UserDetailsPojo>> liveCircleName() {
        return repository.circleNameAndDpLive;
    }

    public void getAllCircleName() {

         repository.getAllCircleName(context);
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

        //return repository.circleNameLive;
        return repository.getCircleNameLive();
    }


    public MutableLiveData<String> getCircleCodeLiveDataInstance() {

        return repository.getInviteCodeLive();

    }


    public MutableLiveData<String> getYourRoleLiveDataInstance() {
        if (yourRoleLiveData == null) {
            yourRoleLiveData = new MutableLiveData<String>();

        }
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









    public void setIdlingResourceInstance(IdlingResourceForTest idlingResourceInstance) {

        idlingResourceForTest = idlingResourceInstance;


    }

    public IdlingResourceForTest getIdlingResourceInstance() {
        return idlingResourceForTest;

    }







}

