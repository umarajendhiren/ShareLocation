package com.androidapps.sharelocation;

import android.content.Context;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseFile;

import java.util.List;

public class CreateJoinViewModel extends ViewModel {


    MainRepository mainRepository;
    public static MutableLiveData<String> selectedFragment;

    public MutableLiveData<String> getSelectedFragmentInstance() {

        if (selectedFragment == null) {
            selectedFragment = new MutableLiveData<>();
            return selectedFragment;
        } else return selectedFragment;
    }

    public static MutableLiveData<String> inviteCode = new MutableLiveData<>();

    @ViewModelInject
    public CreateJoinViewModel(MainRepository mainRepository) {

        this.mainRepository = mainRepository;

        inviteCode = mainRepository.inviteCodeLive;
    }

    public void createNewGroup(String circleName, Context context) {


        String inviteCode = Utilities.getUniqueId();
        inviteCode = inviteCode.substring(0, 7);

        mainRepository.createNewGroup(circleName, inviteCode,this);
    }


    public MutableLiveData<String> getLiveInviteCode() {

      //  Log.d("getLiveInviteCode: ",mainRepository.inviteCodeLive.getValue());
        return mainRepository.getSelectedInviteCodeLiveData();
    }


    public LiveData<List<UserDetailsPojo>> getExistingMemberDetail(Context context) {

        return mainRepository.existingMemberDetailLive;

    }

    public void joinWithCircle(String inviteCode, Context context) {


        mainRepository.joinWithCircle(inviteCode);
    }

    public MutableLiveData<Boolean> isAlreadyInCircle() {
        return mainRepository.getIsAlreadyInCircleInstance();
    }


    public void join() {

        mainRepository.join();
    }

    public void storeParseFileInServer(ParseFile parseFile, Context context) {

        mainRepository.storeParseFile(parseFile);
    }


    public boolean isItValidPassword(String inviteCode) {
       return mainRepository.isValidInviteCode(inviteCode);


    }





    public MutableLiveData<Boolean> isItValidCode() {

        return mainRepository.getIsValidInviteCodeInstance();

    }



    public void isNetworkCOnnected(boolean isConnected) {
        mainRepository.isNetworkCOnnectedInstance().setValue(isConnected);
    }

    public MutableLiveData<Boolean> isConnected() {

        return mainRepository.isNetworkCOnnectedInstance();

    }
}

/**/

