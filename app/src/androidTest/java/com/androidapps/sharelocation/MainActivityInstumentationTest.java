package com.androidapps.sharelocation;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

//Add annotation to specify AndroidJUnitRunner class as the default test runner
@RunWith(AndroidJUnit4.class)

public class MainActivityInstumentationTest {
    private IdlingResource mIdlingResource;
    private Instrumentation.ActivityResult mActivityResult;

    /*This Rule is usually used to grant runtime permissions to avoid the permission dialog from showing up and blocking the App’s Ui*/
    @Rule
    public GrantPermissionRule galleryAccessPermission = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    public GrantPermissionRule locationAccessPermission = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);

        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mIdlingResource = activity.getIdlingResource();
                // To prove that the test fails, omit this call:
                IdlingRegistry.getInstance().register(mIdlingResource);
            }
        });

        setupImageUri();
    }

    //Add the rule that provides functional testing of a single activity
 /*   @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);
*/

    /*  ActivityScenario activityScenario=ActivityScenario.launch(MainActivity.class).onActivity(
                   activity -> { onView(allOf(withId(R.id.button_get_started), withText("Get Started")));}
           );*/

    @Test
    public void MainActivityGetStartedFlowTest() {


        onView(withId(R.id.button_get_started)).check(matches(isDisplayed())).perform(click());


        //now we should see NameFragment.


//if the user click on continue without entering first name or last name ,need to show toast and need to show same Name  Fragment to enter name.

        onView(withId(R.id.edit_first_name)).check(matches(isDisplayed())).perform(typeText("Uma"), closeSoftKeyboard());
        onView(withId(R.id.edit_last_name)).check(matches(isDisplayed())).perform(typeText("Ashok"), closeSoftKeyboard());

        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_last_name)))).check(matches(isDisplayed())).perform(click());

//if the user click on continue without entering email,need to show toast and need to show same email  Fragment to enter email address.

        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_email)))).check(matches(isDisplayed())).perform(click());

//if the user entered email then click on continue need to show password fragment.

        onView(withId(R.id.edit_email)).check(matches(isDisplayed())).perform(typeText("uma2011ece@gmail.com"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_email)))).check(matches(isDisplayed())).perform(click());

//if the user click on continue without entering password need to show same password fragment
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_password)))).check(matches(isDisplayed())).perform(click());

        //if the user entered password then click on continue need to show CreateCircleFragment .
        onView(withId(R.id.edit_password)).check(matches(isDisplayed())).perform(typeText("password"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_password)))).check(matches(isDisplayed())).perform(click());


        //when we re run the test ,it will throw, user name  already exist exception ,so we need to give different name and mail

        onView(withId(R.id.edit_first_name)).check(matches(isDisplayed())).perform(clearText(), typeText("xxx"), closeSoftKeyboard());
        onView(withId(R.id.edit_last_name)).check(matches(isDisplayed())).perform(clearText(), typeText("yyy"), closeSoftKeyboard());

        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_last_name)))).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.edit_email)).check(matches(isDisplayed())).perform(clearText(), typeText("xyz@gmail.com"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_email)))).check(matches(isDisplayed())).perform(click());

        onView(withId(R.id.edit_password)).check(matches(isDisplayed())).perform(clearText(), typeText("password"), closeSoftKeyboard());
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_password)))).check(matches(isDisplayed())).perform(click());

        //if the user click continue button in createCircle fragment,need to show createNewCircle fragment.
        onView(withId(R.id.map_image)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.circle_def)))).check(matches(isDisplayed())).perform(click());


        //when the user click create new circle need to show CircleNameFragment.

        onView(allOf(withId(R.id.btn_create_circle), hasSibling(withId(R.id.tv_no_code)))).check(matches(isDisplayed())).perform(click());


        //in circleNameFragment need to enter circle name ,when the user click continue ,need to show CircleCodeFragment
        onView(withId(R.id.edit_circle_name)).check(matches(isDisplayed())).perform(typeText("MyCircleName"), closeSoftKeyboard());

        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.edit_circle_name)))).check(matches(isDisplayed())).perform(click());


        //when user click in done sharing need to show your role fragment

        onView(allOf(withId(R.id.btn_done), hasSibling(withId(R.id.tv_code)))).check(matches(isDisplayed())).perform(click());

        //when user click on mom need to show ProfilePhotoFragment.
        onView(allOf(withId(R.id.btn_mom), hasSibling(withId(R.id.btn_dad)))).check(matches(isDisplayed())).perform(click());


        //Setup the intent

        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_PICK),
                hasData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        intending(expectedIntent).respondWith(mActivityResult);

        //Click the select button
        onView(allOf(withId(R.id.btn_add_photo))).check(matches(isDisplayed())).perform(click());

        intended(expectedIntent);


//Check the image is displayed
        onView(withId(R.id.image_dp)).check(matches(hasDrawable()));


        //when continue ,need to show AddPlacesFragment,first it will ask location access permission.so we gave that permission in rule before test/
        onView(allOf(withId(R.id.btn_continue), hasSibling(withId(R.id.btn_add_photo)))).check(matches(isDisplayed())).perform(click());


        //when continue,need to show map AddHomMapFragment,first it will ask location access permission.so we gave that permission in rule before test
        onView(allOf(withId(R.id.button_continue), hasSibling(withId(R.id.tv_tips)))).check(matches(isDisplayed())).perform(click());


        //when continue need to store address in server then show homepage activity
        onView(allOf(withId(R.id.save), hasSibling(withId(R.id.imageView_marker)))).check(matches(isDisplayed())).perform(click());


    }

    public static BoundedMatcher<View, ImageView> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable");
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                return imageView.getDrawable() != null;
            }
        };
    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }

    }


    public void setupImageUri() {
// Stub intent
        Resources resources = InstrumentationRegistry.getTargetContext().getResources();
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources
                .getResourcePackageName(
                        R.mipmap.ic_launcher) + '/' + resources.getResourceTypeName(
                R.mipmap.ic_launcher) + '/' + resources.getResourceEntryName(
                R.mipmap.ic_launcher));
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        mActivityResult = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, resultData);


    }
}
