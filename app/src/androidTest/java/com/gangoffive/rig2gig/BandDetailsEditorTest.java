package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Intent.ACTION_MAIN;
import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BandDetailsEditorTest {
    private HashMap<String, Object> bandData;
    private Activity activty;

        @Rule
        public ActivityTestRule<BandDetailsEditor> testRule = new ActivityTestRule(BandDetailsEditor.class);

        @Before
        public void setUp() throws Exception {
            bandData = new HashMap();
            bandData.put("availability", "test availability");
            bandData.put("charge", "test charge");
            bandData.put("description", "test description");
            bandData.put("distance", "test distance");
            bandData.put("email", "test@email.com");
            bandData.put("genres", "test genres");
            bandData.put("location", "test location");
            ArrayList<String> members = new ArrayList<>();
            members.add("test member");
            bandData.put("members", members);
            bandData.put("name", "test name");
            bandData.put("phone-number", "123");
            bandData.put("rating", "test rating");
            testRule.getActivity().onSuccessFromDatabase(bandData);
            testRule.getActivity().saveTabs();
        }

        @Test
        public void testActivityInView() {
            onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        }

        @Test
        public void testComponentVisibility() {
            onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
            onView(withId(R.id.title)).check(matches(isDisplayed()));
            onView(withId(R.id.tabs)).check(matches(isDisplayed()));
            onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
            onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
            onView(withId(R.id.cancel)).check(matches(isDisplayed()));
            onView(withId(R.id.createListing)).check(matches(isDisplayed()));
            onView(withId(R.id.bandAdImageMain)).check(matches(isDisplayed()));
            onView(withId(R.id.imageView)).check(matches(isDisplayed()));
            onView(withId(R.id.changeImageLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.image)).check(matches(isDisplayed()));
            onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
            onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
            onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.detailsTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.detailsView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.distance)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.genresLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.emailLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.email)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.phoneLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.phone)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        @Test
        public void testTextOfComponents() {
            onView(withId(R.id.title)).check(matches(withText("Edit details")));
            onView(withId(R.id.cancel)).check(matches(withText("Cancel")));
            onView(withId(R.id.createListing)).check(matches(withText("Confirm")));
            onView(withId(R.id.galleryImage)).check(matches(withText("Gallery")));
            onView(withId(R.id.takePhoto)).check(matches(withText("Camera")));
            onView(withId(R.id.detailsTitle)).check(matches(withText("Edit details")));
            onView(withId(R.id.nameLabel)).check(matches(withText("Band Name:")));
            onView(withId(R.id.name)).check(matches(withText("")));
            onView(withId(R.id.locationLabel)).check(matches(withText("Band Location:")));
            onView(withId(R.id.location)).check(matches(withText("")));
            onView(withId(R.id.distanceLabel)).check(matches(withText("Distance:")));
            onView(withId(R.id.distance)).check(matches(withText("")));
            onView(withId(R.id.genresLabel)).check(matches(withText("Genres:")));
            onView(withId(R.id.genres)).check(matches(withText("")));
            onView(withId(R.id.emailLabel)).check(matches(withText("Email:")));
            onView(withId(R.id.email)).check(matches(withText("")));
            onView(withId(R.id.phoneLabel)).check(matches(withText("Phone Number:")));
            onView(withId(R.id.phone)).check(matches(withText("")));
        }

        @Test
        public void testSwipeLeftOnce() throws InterruptedException {
            onView(withId(R.id.view_pager)).perform(swipeLeft());
            onView(withId(R.id.app_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.tabs)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.view_pager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.button_holder)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.cancel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.createListing)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.bandAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
            onView(withId(R.id.detailsView)).check(matches(isDisplayed()));
            onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.name)).check(matches(isDisplayed()));
            onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.location)).check(matches(isDisplayed()));
            onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.distance)).check(matches(isDisplayed()));
            onView(withId(R.id.genresLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.genres)).check(matches(isDisplayed()));
            onView(withId(R.id.emailLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.email)).check(matches(isDisplayed()));
            onView(withId(R.id.phoneLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.phone)).check(matches(isDisplayed()));
        }

        @Test
        public void testSwipeLeftTwice() throws InterruptedException {
            onView(withId(R.id.view_pager)).perform(swipeLeft());
            onView(withId(R.id.view_pager)).perform(swipeLeft());
            onView(withId(R.id.app_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.tabs)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.view_pager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.button_holder)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.cancel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.createListing)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.bandAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
            onView(withId(R.id.detailsView)).check(matches(isDisplayed()));
            onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.name)).check(matches(isDisplayed()));
            onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.location)).check(matches(isDisplayed()));
            onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.distance)).check(matches(isDisplayed()));
            onView(withId(R.id.genresLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.genres)).check(matches(isDisplayed()));
            onView(withId(R.id.emailLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.email)).check(matches(isDisplayed()));
            onView(withId(R.id.phoneLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.phone)).check(matches(isDisplayed()));
        }

        @Test
        public void testSwipeLeftTwiceThenRightOnce() throws InterruptedException {
            onView(withId(R.id.view_pager)).perform(swipeLeft());
            onView(withId(R.id.view_pager)).perform(swipeRight());
            onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
            onView(withId(R.id.title)).check(matches(isDisplayed()));
            onView(withId(R.id.tabs)).check(matches(isDisplayed()));
            onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
            onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
            onView(withId(R.id.cancel)).check(matches(isDisplayed()));
            onView(withId(R.id.createListing)).check(matches(isDisplayed()));
            onView(withId(R.id.bandAdImageMain)).check(matches(isDisplayed()));
            onView(withId(R.id.imageView)).check(matches(isDisplayed()));
            onView(withId(R.id.changeImageLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.image)).check(matches(isDisplayed()));
            onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
            onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
            onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.detailsTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.detailsView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.distance)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.genresLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.emailLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.email)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.phoneLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.phone)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }

        @Test
        public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
            testRule.getActivity().onSuccessFromDatabase(bandData);
            Button confirm = testRule.getActivity().findViewById(R.id.createListing);
            ColorDrawable colour = (ColorDrawable) confirm.getBackground();
            int intColour = colour.getColor();
            assertEquals(-5062987, intColour);
            ColorStateList textcolour = confirm.getTextColors();
            intColour = textcolour.getDefaultColor();
            assertEquals(-11711154, intColour);
        }


        @Test
        public void testPopulateInitialFieldsNoAd() throws InterruptedException {

            testRule.getActivity().setBand(bandData);
            testRule.getActivity().saveTabs();
            testRule.getActivity().setViewReferences();
            testRule.getActivity().populateInitialFields();
            onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
            onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
            onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
            onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
            onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
            onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));
        }

        @Test
        public void testReinitialiseTabsNoAd() {


            testRule.getActivity().setBand(bandData);
            testRule.getActivity().saveTabs();
            testRule.getActivity().setViewReferences();
            testRule.getActivity().populateInitialFields();
            testRule.getActivity().reinitialiseTabs();
            onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
            onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
            onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
            onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
            onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
            onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));
            onView(withId(R.id.view_pager)).perform(swipeLeft());
            testRule.getActivity().saveTabs();
            testRule.getActivity().setViewReferences();
            testRule.getActivity().populateInitialFields();
            testRule.getActivity().reinitialiseTabs();
            onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
            onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
            onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
            onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
            onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
            onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));

        }

    @Test
    public void testPopulateInitialFieldsExistingAd() throws InterruptedException {

        testRule.getActivity().setBand(bandData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
        onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
        onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
        onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
        onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));
    }

    @Test
    public void testReinitialiseTabsExistingAd() {


        testRule.getActivity().setBand(bandData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
        onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
        onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
        onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
        onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.name)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.location)).check(matches(withText(bandData.get("location").toString())));
        onView(withId(R.id.distance)).check(matches(withText(bandData.get("distance").toString())));
        onView(withId(R.id.genres)).check(matches(withText(bandData.get("genres").toString())));
        onView(withId(R.id.email)).check(matches(withText(bandData.get("email").toString())));
        onView(withId(R.id.phone)).check(matches(withText(bandData.get("phone-number").toString())));

    }

        @Test
        public void testOnDataBaseResultListingFailure() {
            Enum result = ListingManager.CreationResult.LISTING_FAILURE;
            testRule.getActivity().handleDatabaseResponse(result);
            onView(withText("Listing creation failed.  Check your connection and try again"))
                    .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        }

        @Test
        public void testOnDataBaseResultImageFailure() {
            Enum result = ListingManager.CreationResult.IMAGE_FAILURE;
            testRule.getActivity().handleDatabaseResponse(result);
            onView(withText("Listing creation failed.  Check your connection and try again"))
                    .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        }

        @Test
        public void testOnDataBaseResultSuccess() {
            ListingManager manager = mock(ListingManager.class);
            testRule.getActivity().setListingManager(manager);
            when(manager.getListingRef()).thenReturn("testRef");
            Enum result = ListingManager.CreationResult.SUCCESS;
            testRule.getActivity().handleDatabaseResponse(result);
            onView(withText("Advertisement created successfully"))
                    .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
            assertTrue(testRule.getActivity().isFinishing());
            onView(withId(R.id.bandListingDetailsMain)).check(matches(isDisplayed()));
        }

        @Test
        public void testCancelAdvertisement() {
            onView(withId(R.id.cancel)).perform(click());
            assertTrue(testRule.getActivity().isFinishing());
        }
}




