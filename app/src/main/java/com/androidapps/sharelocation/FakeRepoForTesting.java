package com.androidapps.sharelocation;

import androidx.lifecycle.MutableLiveData;

public class FakeRepoForTesting {
    public static MutableLiveData<String> circleNameLiveFake = new MutableLiveData<>();
    public static MutableLiveData<String> inviteCodeLiveFake = new MutableLiveData<>();
    FakeRepoForTesting(){}



    public MutableLiveData<String> getFakeCircleNameLive(){
        circleNameLiveFake.setValue("Family");
        return circleNameLiveFake;

    }

    public MutableLiveData<String> getFakeinviteCodeLive(){
        inviteCodeLiveFake.setValue("1234");
        return inviteCodeLiveFake;

    }

}
