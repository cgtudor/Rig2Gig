package com.gangoffive.rig2gig;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Button;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.musician.management.MusicianDetailsEditor;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MusicianDetailsEditorTest {
    private HashMap<String, Object> musicianData;
    private Activity activty;
    private ListingManager manager;

    @Rule
    public ActivityTestRule<MusicianDetailsEditor> testRule = new ActivityTestRule(MusicianDetailsEditor.class);

    @BeforeClass
    public static void setup() {
        Looper.prepare();
    }

    @Before
    public void setUp() throws Exception {
        musicianData = new HashMap();
        musicianData.put("distance", "test distance");
        musicianData.put("latitude","10");
        musicianData.put("longitude","6");
        musicianData.put("genres", "test genres");
        musicianData.put("location", "test location");
        ArrayList<String> bands = new ArrayList<>();
        bands.add("test band");
        musicianData.put("members", bands);
        musicianData.put("name", "test name");
        musicianData.put("rating", "test rating");
        musicianData.put("user-ref", "test uesr-ref");
        manager = mock(ListingManager.class);
        testRule.getActivity().setListingManager(manager);
    }

    public void enterTestData()
    {
        onView(withId(R.id.venue_name_final)).perform(typeText("test name edit"));
        closeSoftKeyboard();
        onView(withId(R.id.musician_location)).perform(typeText("test location edit"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(typeText("test distance edit"));
        closeSoftKeyboard();
    }

    public void deleteAllFields()
    {
        onView(withId(R.id.venue_name_final)).perform(replaceText(""));
        onView(withId(R.id.venue_description_final)).perform(replaceText(""));
    }



    public void deleteOneCharFields()
    {
        onView(withId(R.id.venue_name_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.musician_location)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
    }

    public void enterOneCharAllFields()
    {
        onView(withId(R.id.venue_name_final)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.musician_location)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(typeText("0"));
        closeSoftKeyboard();
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
        onView(withId(R.id.detailsImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.changeImageLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_name_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.musician_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.selectGenres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testTextOfComponents() {
        onView(withId(R.id.title)).check(matches(withText("Edit details")));
        onView(withId(R.id.cancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.createListing)).check(matches(withText("Confirm")));
        onView(withId(R.id.galleryImage)).check(matches(withText("Gallery")));
        onView(withId(R.id.takePhoto)).check(matches(withText("Camera")));
        onView(withId(R.id.detailsTitle)).check(matches(withText("Edit details")));
        onView(withId(R.id.nameLabel)).check(matches(withText("Musician Name:")));
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.locationLabel)).check(matches(withText("Musician Location:")));
        onView(withId(R.id.musician_location)).check(matches(withText("")));
        onView(withId(R.id.distanceLabel)).check(matches(withText("Distance:")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));
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
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_name_final)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.musician_location)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.selectGenres)).check(matches(isDisplayed()));
        onView(withId(R.id.genres)).check(matches(isDisplayed()));
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
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_name_final)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.musician_location)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.selectGenres)).check(matches(isDisplayed()));
        onView(withId(R.id.genres)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeLeftTwiceThenRightOnce() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.changeImageLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_name_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.musician_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.selectGenres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.genres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }


    @Test
    public void testPopulateInitialFields() throws InterruptedException {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));
    }

    @Test
    public void testReinitialiseTabs() throws InterruptedException {

        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));

    }

    @Test
    public void testOnDataBaseResultListingFailure() {
        Enum result = ListingManager.CreationResult.LISTING_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Updating details failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultImageFailure() throws InterruptedException {
        Enum result = ListingManager.CreationResult.IMAGE_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Updating details failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultSuccess() {
        Enum result = ListingManager.CreationResult.SUCCESS;
        testRule.getActivity().handleDatabaseResponse(result);
        //unable to test toast due to activity running in isolation and finishing
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testCancelAdvertisement() {
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.cancel)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testDataInput()
    {
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.venue_name_final)).check(matches(withText("test name edit")));
        onView(withId(R.id.musician_location)).check(matches(withText(endsWith("test location edit"))));
    }

    @Test
    public void testDataInputSwipeAwaySwipeBack()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.venue_name_final)).check(matches(withText("test name edit")));
        onView(withId(R.id.musician_location)).check(matches(withText(endsWith("test location edit"))));
    }

    @Test
    public void testButtonColourChangeOnValidData()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        verify(manager,times(1)).getImage(testRule.getActivity());
    }

    @Test
    public void testButtonColourChangeOnInvalidData() throws InterruptedException {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        deleteAllFields();
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        verify(manager,times(0)).postDataToDatabase(any(),any(),any());
        onView(withText("Details not updated.  Ensure all fields are complete and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testDataInputTwiceThenDeleteOneChar()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        enterOneCharAllFields();
        deleteOneCharFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("a")));
        onView(withId(R.id.musician_location)).check(matches(withText(endsWith("a"))));
        onView(withId(R.id.venue_description_final)).check(matches(withText("0")));
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        verify(manager,times(1)).getImage(testRule.getActivity());
    }

    @Test
    public void testDataInputOnceThenDeleteOneChar() throws InterruptedException {
        Thread.sleep(1000);
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        deleteOneCharFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        verify(manager,times(0)).postDataToDatabase(any(),any(),any());
        onView(withText("Details not updated.  Ensure all fields are complete and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testGetGalleryImage()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_GET_CONTENT));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withId(R.id.galleryImage)).perform(click());
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void testGalleryImageReturned()  {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setMusician(musicianData);
        testRule.getActivity().onSuccessFromDatabase(musicianData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        Intent intent = new Intent();
        Resources resources = InstrumentationRegistry.getInstrumentation().getContext().getResources();
        Uri uri = Uri.parse("android.resource://com.gangoffive.rig2gig/drawable/misc");
        intent.setData(uri);
        assertTrue(testRule.getActivity().getImageView().getDrawable() == null);
        testRule.getActivity().onActivityResult(1,-1,intent);
        assertTrue(testRule.getActivity().getImageView().getDrawable() != null);
    }
}