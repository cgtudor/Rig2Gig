package com.gangoffive.rig2gig.index;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;
import android.widget.TextView;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ToastMatcher;
import com.gangoffive.rig2gig.advert.index.VenueAdapter;
import com.gangoffive.rig2gig.advert.index.VenueAdvertIndexActivity;
import com.gangoffive.rig2gig.band.management.ManageBandMembersActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VenueAdvertIndexActivityTest
{
    private IntentsTestRule intentsTestRule = new IntentsTestRule(VenueAdvertIndexActivity.class);

    @BeforeClass
    public static void setupClass() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    @Before
    public void setup()
    {

    }

    @Rule
    public ActivityTestRule<VenueAdvertIndexActivity> testRule = new ActivityTestRule<>(VenueAdvertIndexActivity.class);

   /* @Test
    public void testActivityInView()
    {
        onView(ViewMatchers.withId(R.id.venueAdvertIndexMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.viewVenuesMain)).check(matches(isDisplayed()));
        onView(withId(R.id.viewSwipeContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.viewRecyclerView)).check(matches(isDisplayed()));
        *//*onView(withId(R.id.savedVenuesMain)).check(matches(isDisplayed()));
        onView(withId(R.id.savedSwipeContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.savedRecyclerView)).check(matches(isDisplayed()));*//*
    }

    @Test
    public void testTextOfComponents()
    {
        onView(allOf(isAssignableFrom(TextView.class), withParent(withResourceName("toolbar")))).check(matches(withText("Venue Adverts")));
    }

    @Test
    public void testOnSuccessFromDatabaseOneAdvert() throws InterruptedException {

    }

    @Test
    public void testOnSuccessFromDatabaseMultipleAdverts() {

    }

    @Test
    public void testClickAdvert() {

    }

    @Test
    public void testScroll()
    {

    }

*//*    @Test
    public void testPressPhoneBackButton()
    {
        pressBack();
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testPressMenuBarBackButton()
    {
        onView(withContentDescription("Navigate up")).perform(click());
        assertTrue(testRule.getActivity().isBackClicked());
        assertTrue(testRule.getActivity().isFinishing());
    }*//*

*//*    @Test
    public void testSwipeToRefresh() {

    }*//*

    @Test
    public void testOnActivityResultFavouritedAdvert() throws InterruptedException {

    }
*/}