package com.androidapps.sharelocation;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.androidapps.sharelocation.model.UserDetailsPojo;
import com.androidapps.sharelocation.repository.MainRepository;
import com.androidapps.sharelocation.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})*/

@RunWith(JUnit4.class)
public class MainViewModelMockTest {
    MutableLiveData<String> circleName = new MutableLiveData<>();
    MutableLiveData<String> circleCode = new MutableLiveData<>();


    // Tells Mockito to mock the repository instance

    MutableLiveData<List<UserDetailsPojo>> liveAdminDetails;
    @Mock
    MainRepository mainRepository;

    @Mock
    Context context;

    /*mockito will inject all mocks into this mainviewmodel object for testing*/

    @InjectMocks
    MainViewModel mainViewModel;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor ;
    @Captor
    ArgumentCaptor<Double> doubleArgumentCaptor ;



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
        mainViewModel = new MainViewModel(mainRepository, context);


        circleName.setValue("Family");
        circleCode.setValue("1234");


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
    public void getCircleNameLiveData() {



        /* stub for mock method.
         * when getCircleNameLive() method invoked from mock repository ,then it will return predefined value.*/
        when(mainRepository.getCircleNameLive()).thenReturn(circleName);

        /*assertion*/
        assertThat(mainViewModel.getCircleNameLiveDataInstance().getValue()).isEqualTo("Family");

        /*verification*/
        verify(mainRepository, atLeastOnce()).getCircleNameLive();
    }

    @Test
    public void getInviteCodeLiveData() {


        /* stub for mock method.*/
        when(mainRepository.getInviteCodeLive()).thenReturn(circleCode);

        /*assertion*/
        assertThat(mainViewModel.getCircleCodeLiveDataInstance().getValue()).isEqualTo("1234");

        /*verification*/
        verify(mainRepository, atLeastOnce()).getInviteCodeLive();
    }

    @Test
    public void creatNewGroupTest() {

        when(mainRepository.getCircleNameLive()).thenReturn(circleName);
        when(mainRepository.getInviteCodeLive()).thenReturn(circleCode);

        /*creatNewGroup(String circleName,String inviteCode) method is void method,so no need to do anything when it's invoked.
         */
      // doNothing().when(mainRepository).createNewGroup(mainRepository.getCircleNameLive().getValue(),mainRepository.getInviteCodeLive().getValue());

        /*call viewmodel method to invoke mocked class method */
      mainViewModel.createNewCircle();
        /*verify method called once
        * we can use argument matcher anyString() as parameter for testing.so that we can send any string into that method.*/
        verify(mainRepository, atLeastOnce()).createNewGroup(anyString(), anyString());
    }

    @Test
    public void joinWithCircleTest() {

        /*do nothing when invoke void method in mock object*/
        doNothing().when(mainRepository).join();

        /*call viewmodel method to invoke mocked class method */
        mainViewModel.joinWithCircle();

        /*ensure join() method called at least  once*/
        verify(mainRepository, atLeastOnce()).join();
    }

    @Test

    public void savePlaceInServerTest(){

        /*do nothing when invoke void method in mock object*/
        doNothing().when(mainRepository).savePlaceGeoPointInServer(anyString(),anyDouble(),anyDouble());

        /*call viewmodel method to invoke mocked class method */
        mainViewModel.savePlaceInServer("Home", 1233.45, 324.45);

        /*ensure savePlaceGeoPointInServer() method called at least  once*/
        verify(mainRepository, atLeastOnce()).savePlaceGeoPointInServer(anyString(),anyDouble(),anyDouble());

    }

    @Test
    public void savePlaceInServerCaptureArgumentTest(){

        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);

        /*do nothing when invoke void method in mock object*/
        doNothing().when(mainRepository).savePlaceGeoPointInServer(anyString(),anyDouble(),anyDouble());


        /*call viewmodel method to invoke mocked class method */
        mainViewModel.savePlaceInServer("Home", 1233.45, 324.45);

        verify(mainRepository).savePlaceGeoPointInServer(stringArgumentCaptor.capture(),doubleArgumentCaptor.capture(),doubleArgumentCaptor.capture());

        List<String> addressTitle = stringArgumentCaptor.getAllValues();
        List<Double> latLang = doubleArgumentCaptor.getAllValues();


        System.out.println(addressTitle.get(0)+" "+latLang.get(0)+" "+latLang.get(1));

        assertThat(addressTitle.get(0).equals("Home"));
        assertThat(latLang.get(0).equals(1233.45));
        assertThat(latLang.get(1).equals(324.45));




    }



    @Test
    public void getAdminDetailFromUserObjectTest() {

        /*stub*/
        when(mainRepository.getExistingMemberDetail(anyString(), anyString(), anyString())).thenReturn(liveAdminDetails);

        /*execute method*/
        mainViewModel.getAdminDetailFromUserObject("adminId", "Family", "1234", context);

        /*verify the execution*/
        verify(mainRepository,atLeastOnce()).getExistingMemberDetail(anyString(),anyString(),anyString());

        /*assertion*/
        assertThat(mainRepository.getExistingMemberDetail(anyString(),anyString(),anyString()).getValue().get(0).getObjectId().equals("AdminobjectId"));

    }


    @Test
    public void setSelectedCircleNameTest(){

        /*stub*/
        doNothing().when(mainRepository).setSelectedCircleName(anyString(),anyString());

        /*execute method*/
        mainViewModel.setSelectedCircleName(circleName.getValue(),circleCode.getValue());

        /*verify the execution*/
        verify(mainRepository,atLeastOnce()).setSelectedCircleName(anyString(),anyString());
    }

    public void setFirstNameLiveDataTest() {



        mainViewModel.getFirstNameLiveDataInstance().setValue("FirstName");
        assertThat(mainViewModel.getFirstNameLiveData().getValue()).isEqualTo("FirstName");
    }

    @Test
    public void setLastNameLiveDataTest() {

        mainViewModel.getLastNameLiveDataInstance().setValue("LastName");
        assertThat(mainViewModel.getLastNameLiveData().getValue()).isEqualTo("LastName");
    }

    @Test
    public void setEmailAddressLiveDataTest() {

        mainViewModel.getEmailLiveDataInstance().setValue("xyz@gmail.com");
        assertThat(mainViewModel.getEmailLiveData().getValue()).isEqualTo("xyz@gmail.com");
    }


    @Test
    public void setPasswordLiveDataTest() {

        mainViewModel.getPasswordLiveDataInstance().setValue("password");
        assertThat(mainViewModel.getPasswordLiveData().getValue()).isEqualTo("password");

    }


}
