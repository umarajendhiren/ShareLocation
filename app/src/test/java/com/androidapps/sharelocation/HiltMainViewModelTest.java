package com.androidapps.sharelocation;

import android.os.Build;

import com.parse.Parse;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.mockito.Mockito.spy;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;
/*we must annotate any UI test that uses Hilt with @HiltAndroidTest.
This annotation is responsible for generating the Hilt components for each test.*/


@HiltAndroidTest

//RobolectricTestRunner ,we can run the test that depends on the android framework without emulator or real device.
//execution time  is faster in this runner.
@RunWith(RobolectricTestRunner.class)
@LooperMode(PAUSED)
@Config(sdk = {Build.VERSION_CODES.O_MR1}, application = HiltTestApplication.class)
public class HiltMainViewModelTest {


    MainViewModel mainViewModel;
    @Inject
    MainRepository mainRepository;


    /*@Inject
    MainRepository MockRepository;*/
    /*HiltAndroidRule manages the components' state and is used to perform injection on your test:*/
    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    // @Before => JUnit 4 annotation that specifies this method should run before each test is run
    // Useful to do setup for objects that are needed in the test
    //To tell Hilt to populate the @Inject fields call hiltRule.inject()
    @Before
    public void init() {
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("tvo4tsUdmH3YSI7iUgewB41l7eIDKn3wLTg8oOld")
                .clientKey("6Gnexs6CgHFE6ilZMLbJCaIaOcV2efGds0Lq1bJs")

                .server("https://parseapi.back4app.com/")

                .build()
        );


        hiltRule.inject();

        mainViewModel = new MainViewModel(mainRepository);
        // ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

    }


/*
    @Test
    public void signUpInServer() {
        mainViewModel.getFirstNameLiveDataInstance().setValue("XXX");
        mainViewModel.getLastNameLiveDataInstance().setValue("YYY");
        mainViewModel.getPasswordLiveDataInstance().setValue("Password");
        mainViewModel.getEmailLiveDataInstance().setValue("XYZ@gmail.com");
        mainViewModel.signUpInServer();


    }*/


    @Test
    public void createNewCircle() {
       // parseUser = new ParseUser();
        ParseUser currentUser = new ParseUser();
        currentUser.setUsername("Uma");
        currentUser.setPassword("pass");
        currentUser.setObjectId("BpeIOVKC5D");

       // when(currentUser.getObjectId()).thenReturn("BpeIOVKC5D");


        ParseUser partialMockCurrentUser = spy(currentUser);
        System.out.println(partialMockCurrentUser.getObjectId());


       // doReturn("BpeIOVKC5D").when(parseUser).getObjectId();

        mainViewModel.getCircleNameLiveDataInstance().setValue("Family");
        mainViewModel.getCircleCodeLiveDataInstance().setValue("12345");
       // Shadows.shadowOf(Looper.getMainLooper()).idleFor(250, TimeUnit.MILLISECONDS);



       //  mainViewModel.createNewCircle();

    }
    //@Test => JUnit g4 annotation specifying this is a test to be run


    /* TextView tvHelloWorld = (TextView) activity.findViewById(R.id.tvHelloWorld);
        assertNotNull("TextView could not be found", tvHelloWorld);
        assertTrue("TextView contains incorrect text",
            "Hello world!".equals(tvHelloWorld.getText().toString()));*/
  /*  @Test
    public void signUpInServerTest() {

        onView(withId(R.id.button_get_started)).check(matches(isDisplayed())).perform(click());


        //now we should see NameFragment.
        //when we re run the test ,it will throw, user name  already exist exception ,so we need to give different name and mail

        onView(withId(R.id.edit_first_name)).check(matches(isDisplayed())).perform(clearText(), typeText("xxx"), closeSoftKeyboard());

        onView(withId(R.id.edit_first_name)).check(matches(withText("xxx")));

        onView(withId(R.id.edit_last_name)).check(matches(isDisplayed())).perform(clearText(), typeText("yyy"), closeSoftKeyboard());

        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_last_name)))).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.edit_email)).check(matches(isDisplayed())).perform(clearText(), typeText("xyz@gmail.com"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_email)))).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.edit_password)).check(matches(isDisplayed())).perform(clearText(), typeText("password"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_password)))).check(matches(isDisplayed())).perform(click());
    }*/


}