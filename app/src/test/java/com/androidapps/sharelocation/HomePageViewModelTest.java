package com.androidapps.sharelocation;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HomePageViewModelTest {

    @Mock
    Context context;

    @Mock
    MainRepository mainRepository;

    HomePageViewModel homePageViewModel;

    MutableLiveData<List<UserDetailsPojo>> liveUserDetails;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //It will tell JUnit to force tests to be executed synchronously, especially when using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {

        homePageViewModel = new HomePageViewModel(mainRepository);


        liveUserDetails = new MutableLiveData<>();
        UserDetailsPojo userDetailsPojo = new UserDetailsPojo();


        userDetailsPojo.setUserName("userName");
        userDetailsPojo.setObjectId("userObjectIs");
        List<UserDetailsPojo> adminDetailList = new ArrayList();
        adminDetailList.add(userDetailsPojo);

        //mock live data for repository method , mainRepository.getUserDetailsLiveData()
        liveUserDetails.setValue(adminDetailList);
    }


    @Test
    public void testSetZoomLevelForMarker() {
        doNothing().when(mainRepository).setZoomLevelForMarker(123);
        homePageViewModel.setZoomLevelForMarker(123);
    }

    @Test
    public void getGroupMemberList() {

        when(mainRepository.getUserDetailsLiveData()).thenReturn(liveUserDetails);
        homePageViewModel.getGroupMemberList();

        assertThat(homePageViewModel.getGroupMemberList().getValue()).isEqualTo(liveUserDetails.getValue());
    }

    @Test
    public void getSelectedGroupNameLiveData() {
        MutableLiveData<String> selectedCircleName = new MutableLiveData<>();
        selectedCircleName.setValue("Family");


        when(mainRepository.getSelectedGroupNameLiveData()).thenReturn(selectedCircleName);
        homePageViewModel.getSelectedGroupNameLiveData();
        assertThat(homePageViewModel.getSelectedGroupNameLiveData().getValue()).isEqualTo(selectedCircleName.getValue());

    }

    @Test
    public void getSelectedInviteCodeLiveData() {
        MutableLiveData<String> selectedInviteCode = new MutableLiveData<>();
        selectedInviteCode.setValue("1234");
        when(mainRepository.getSelectedInviteCodeLiveData()).thenReturn(selectedInviteCode);
        homePageViewModel.getSelectedInviteCodeLiveData();

        assertThat(homePageViewModel.getSelectedInviteCodeLiveData().getValue()).isEqualTo(selectedInviteCode.getValue());
    }

    @Test
    public void getAllCircleName() {
        UserDetailsPojo userDetailsPojo=new UserDetailsPojo();

        userDetailsPojo.setCircleName("Family");

        List<UserDetailsPojo> circleNameList=new ArrayList<>();
        circleNameList.add(userDetailsPojo);

        MutableLiveData<List<UserDetailsPojo>> allCircleName=new MutableLiveData<>();
        allCircleName.setValue(circleNameList);

        when(mainRepository.getAllCircleName(context)).thenReturn( allCircleName);
        homePageViewModel.getSelectedInviteCodeLiveData();

        assertThat(homePageViewModel.getAllCircleName(context).getValue()).isEqualTo(allCircleName.getValue());

    }


    @Test
    public void getSavedPlaces() {
    }

    @Test
    public void checkHomeAvailabe() {
    }

    @Test
    public void removePlaceFromServer() {
    }

    @Test
    public void setNotification() {
    }

    @Test
    public void getUserDpLive() {
    }

    @Test
    public void isUserLoggedIn() {
    }
}