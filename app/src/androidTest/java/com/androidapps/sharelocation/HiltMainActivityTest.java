package com.androidapps.sharelocation;


import androidx.test.filters.MediumTest;

import com.parse.Parse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

//Medium Instrumentation Unit test

//this is Medium test,Medium tests are integration tests,because this test will interact between all the layer such as
// view,viewModel,repository to test single method.

//this is Instrumentation Unit test because test need android device to get input from view


/*we must annotate any UI test that uses Hilt with @HiltAndroidTest.
This annotation is responsible for generating the Hilt components for each test.*/
@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest


public class HiltMainActivityTest {

    /*HiltAndroidRule manages the components' state and is used to perform injection on your test:*/
    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);





//@ApplicationContext Context context,

  /*  @Nullable
    List channelsList*/

   /* @Inject
    ParseQuery<ParseObject> queryCircleName;
    @Inject
    ParseQuery<ParseUser> queryParseUser;
    @Inject
    ParseLiveQueryClient parseLiveQueryClient;

    @Inject
    MainRepository mainRepository;
*/


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
       // ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
    }




    @Test
    public void launchFragmentInMainActivityContainer() {

       // ActivityScenario.launch(MainActivity.class).onActivity(activity -> {

            onView(withId(R.id.button_get_started)).check(matches(isDisplayed())).perform(click());


            //now we should see NameFragment.
            //when we re run the test ,it will throw, user name  already exist exception ,so we need to give different name and mail

            onView(withId(R.id.edit_first_name)).check(matches(isDisplayed())).perform(clearText(), typeText("xxx"), closeSoftKeyboard());
            onView(withId(R.id.edit_last_name)).check(matches(isDisplayed())).perform(clearText(), typeText("yyy"), closeSoftKeyboard());

            onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_last_name)))).check(matches(isDisplayed())).perform(click());

            onView(withId(R.id.edit_email)).check(matches(isDisplayed())).perform(clearText(), typeText("xyz@gmail.com"), closeSoftKeyboard());
            onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_email)))).check(matches(isDisplayed())).perform(click());

            onView(withId(R.id.edit_password)).check(matches(isDisplayed())).perform(clearText(), typeText("password"), closeSoftKeyboard());
            onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_password)))).check(matches(isDisplayed())).perform(click());


    }


}

