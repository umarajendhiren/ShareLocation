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

/*@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})*/

@RunWith(JUnit4.class)
public class MainViewModelMockTest {

    // Tells Mockito to mock the repository instance

    MutableLiveData<List<UserDetailsPojo>> liveAdminDetails;
    @Mock
    MainRepository mainRepository;

    @Mock
    Context context;


    MainViewModel mainViewModel;

    //	Tells Mockito to create the mocks based on the @Mock annotation
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    //It will tell JUnit to force tests to be executed synchronously, especially when using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        // MockitoAnnotations.initMocks(this);
        // Instantiates the class under test using the created mock
        mainViewModel = new MainViewModel(mainRepository);


        mainViewModel.getCircleNameLiveDataInstance().setValue("Family");
        mainViewModel.getCircleCodeLiveDataInstance().setValue("1234");


        //mock live data for repository method , mainRepository.isUserLoggedIn()
        MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();
        isUserLoggedIn.setValue(true);


        liveAdminDetails = new MutableLiveData<>();
        UserDetailsPojo userDetailsPojo = new UserDetailsPojo();


        userDetailsPojo.setUserName("AdminName");
        userDetailsPojo.setObjectId("AdminobjectId");
        List<UserDetailsPojo> adminDetailList = new ArrayList();
        adminDetailList.add(userDetailsPojo);

        //mock live data for repository method , mainRepository.getAdminDetailFromUserObjectTest()
        liveAdminDetails.setValue(adminDetailList);


    }


    @Test
    public void setFirstNameLiveData() {

        mainViewModel.getFirstNameLiveDataInstance().setValue("FirstName");
        assertThat(mainViewModel.getFirstNameLiveData().getValue()).isEqualTo("FirstName");
    }

    @Test
    public void setLastNameLiveData() {

        mainViewModel.getLastNameLiveDataInstance().setValue("LastName");
        assertThat(mainViewModel.getLastNameLiveData().getValue()).isEqualTo("LastName");
    }

    @Test
    public void setEmailAddressLiveData() {

        mainViewModel.getEmailLiveDataInstance().setValue("xyz@gmail.com");
        assertThat(mainViewModel.getEmailLiveData().getValue()).isEqualTo("xyz@gmail.com");
    }


    @Test
    public void setPasswordLiveData() {

        mainViewModel.getPasswordLiveDataInstance().setValue("password");
        assertThat(mainViewModel.getPasswordLiveData().getValue()).isEqualTo("password");

    }

    @Test
    public void setCircleNameLiveData() {

        mainViewModel.getCircleNameLiveDataInstance().setValue("Family");
        assertThat(mainViewModel.getCircleNameLiveData().getValue()).isEqualTo("Family");
    }

    @Test
    public void setCircleCodeLiveData() {

        mainViewModel.getCircleCodeLiveDataInstance().setValue("circleCode");
        assertThat(mainViewModel.getCircleCodeLiveData().getValue()).isEqualTo("circleCode");
    }

    @Test
    public void RoleLiveData() {

        mainViewModel.getYourRoleLiveDataInstance().setValue("Mom");
        assertThat(mainViewModel.getYourRoleLiveData().getValue()).isEqualTo("Mom");

    }


    @Test
    public void createNewCircleTest() {


        System.out.println(mainViewModel.getCircleNameLiveData().getValue());
        System.out.println(mainViewModel.getCircleCodeLiveData().getValue());


        assertThat(mainViewModel.getCircleNameLiveData().getValue()).isEqualTo("Family");
        assertThat(mainViewModel.getCircleCodeLiveData().getValue()).isEqualTo("1234");

        //createNewGroup() is void type method in repository.so we need to use doNothing() when mock.
        doNothing().when(mainRepository).createNewGroup("Family", "1234");

        mainViewModel.createNewCircle();


    }


    @Test
    public void setUserLoggedInValueInRepo() {


        //isUserLoggedIn() is void type method in repository.so we need to use doNothing() when mock.
        doNothing().when(mainRepository).isUserLoggedIn(true);


        mainViewModel.isUserLoggedIn(true, context);


    }

    @Test
    public void getUserLoggedInValueFromRepo() {


        // when(MainRepository.isUserLoggedIn).thenReturn(isUserLoggedIn);


        mainViewModel.isUserLoggedInAlready(context);

        System.out.println(mainViewModel.isUserLoggedInAlready(context).getValue());
    }

    @Test
    public void getAdminDetailFromUserObjectTest() {
        when(mainRepository.getExistingMemberDetail("adminId", "Family", "1234")).thenReturn(liveAdminDetails);

        mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context);

        System.out.println(mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context).getValue().get(0).getUserName());
        System.out.println(mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context).getValue().get(0).getObjectId());

        assertThat(mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context).getValue().get(0).getUserName()).isEqualTo("AdminName");
        assertThat(mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context).getValue().get(0).getObjectId()).isEqualTo("AdminobjectId");
    }

    @Test
    public void savePlaceInServerTest() {


        //savePlaceInServer()  void function
        doNothing().when(mainRepository).savePlaceGeoPointInServer("Home", 1233.45, 324.45);

        mainViewModel.savePlaceInServer(context, "Home", 1233.45, 324.45);
    }

    @Test
    public void updateAddressTest() {
        //updateAddress()  void function
        doNothing().when(mainRepository).updateAddress("Home", 1233.45, 324.45, "1");
        mainViewModel.updateAddress(context, "Home", 1233.45, 324.45, "1");
    }


    @Test
    public void joinWithCircleTest() {

        doNothing().when(mainRepository).join();
        mainViewModel.joinWithCircle(context);
    }
}
