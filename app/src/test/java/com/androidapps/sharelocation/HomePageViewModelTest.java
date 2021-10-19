package com.androidapps.sharelocation;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.repository.MainRepository;
import com.androidapps.sharelocation.viewmodel.HomePageViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HomePageViewModelTest {
    MutableLiveData<List<UserDetailsPojo>> liveUserDetails;
    @Mock
    Context context;

    @Mock
    MainRepository mainRepository;



    @InjectMocks
    HomePageViewModel homePageViewModel;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //It will tell JUnit to force tests to be executed synchronously, especially when using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {

        homePageViewModel = new HomePageViewModel(mainRepository,context);


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

        /*stub*/
        doNothing().when(mainRepository).setZoomLevelForMarker(anyInt());
        /*execution*/
        homePageViewModel.setZoomLevelForMarker(123);

        /*verification*/
        verify(mainRepository,atLeastOnce()).setZoomLevelForMarker(123);
    }

    @Test
    public void getGroupMemberList() {


        /*stub*/
        when(mainRepository.getUserDetailsLiveData()).thenReturn(liveUserDetails);

        /*execution*/
        homePageViewModel.getGroupMemberList();

        /*Assertion*/
        assertThat(homePageViewModel.getGroupMemberList().getValue()).isEqualTo(liveUserDetails.getValue());
    }

    @Test
    public void getSelectedGroupNameLiveData() {
        MutableLiveData<String> selectedCircleName = new MutableLiveData<>();
        selectedCircleName.setValue("Family");

        /*stub*/
        when(mainRepository.getSelectedGroupNameLiveData()).thenReturn(selectedCircleName);
        /*execution*/
        homePageViewModel.getSelectedGroupNameLiveData();
        /*Assertion*/
        assertThat(homePageViewModel.getSelectedGroupNameLiveData().getValue()).isEqualTo(selectedCircleName.getValue());

    }

    @Test
    public void getSelectedInviteCodeLiveData() {
        MutableLiveData<String> selectedInviteCode = new MutableLiveData<>();
        selectedInviteCode.setValue("1234");
        /*stub*/
        when(mainRepository.getSelectedInviteCodeLiveData()).thenReturn(selectedInviteCode);
        /*execution*/
        homePageViewModel.getSelectedInviteCodeLiveData();
        /*Assertion*/
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
        /*stub*/
        when(mainRepository.getAllCircleName(context)).thenReturn(allCircleName);
        /*execution*/
        homePageViewModel.getSelectedInviteCodeLiveData();
        /*Assertion*/
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

        doNothing().when(mainRepository).removePlaceFromServer(anyString(),anyString());

        homePageViewModel.removePlaceFromServer("1","home");

        verify(mainRepository,atLeastOnce()).removePlaceFromServer(anyString(),anyString());
    }

    @Test
    public void setNotification() {

        doNothing().when(mainRepository).setNotification(anyBoolean(),anyInt());

        homePageViewModel.setNotification(true,1);

        verify(mainRepository,atLeastOnce()).setNotification(anyBoolean(),anyInt());
    }

    @Test
    public void getUserDpLive() {

        doNothing().when(mainRepository).getCurrentUserDetails();

      //doReturn(liveUserDetails).when(mainRepository.userDpBitmapLive);

        homePageViewModel.getUserDpLive();

        verify(mainRepository,atLeastOnce()).getCurrentUserDetails();

    }

    @Test
    public void isUserLoggedIn() {
    }
}