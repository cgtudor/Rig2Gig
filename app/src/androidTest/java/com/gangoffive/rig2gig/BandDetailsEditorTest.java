package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.BandDetailsEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.TabStatePreserver;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BandDetailsEditorTest {
    private HashMap<String, Object> bandData;
    private Activity activty;
    private ListingManager manager;
    private BandDetailsEditor confirmationClass, mockClass;

    @Rule
    public ActivityTestRule<BandDetailsEditor> testRule = new ActivityTestRule(BandDetailsEditor.class);


    @BeforeClass
    public static void setup() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    @Before
    public void setUp() throws Exception {
        confirmationClass = new BandDetailsEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void populateInitialFields() {}

            @Override
            public void saveTabs() {}

        };

        mockClass = mock(BandDetailsEditor.class);
        bandData = new HashMap();
        bandData.put("availability", "test availability");
        bandData.put("charge", "test charge");
        bandData.put("description", "test description");
        bandData.put("distance", "1");
        bandData.put("email", "test@email.com");
        bandData.put("latitude","10");
        bandData.put("longitude","6");
        ArrayList<String> genres = new ArrayList<>();
        genres.add("test genre");
        bandData.put("genres", genres);
        bandData.put("location", "test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member");
        bandData.put("members", members);
        bandData.put("name", "test name");
        bandData.put("phone-number", "123");
        bandData.put("rating", "test rating");
        manager = mock(ListingManager.class);
        //when(manager.postDataToDatabase(any(),any(Drawable.class),any(CreateAdvertisement.class)))
        testRule.getActivity().setListingManager(manager);
    }

    public void enterTestData()
    {
        onView(withId(R.id.name)).perform(typeText("test name edit"));
        closeSoftKeyboard();
        onView(withId(R.id.band_location)).perform(typeText("test location edit"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(typeText("1"));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(typeText("test@email.com"));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(typeText("0"));
        closeSoftKeyboard();
    }

    public void deleteAllFields()
    {
        onView(withId(R.id.name)).perform(replaceText(""));
        onView(withId(R.id.venue_description_final)).perform(replaceText(""));
        onView(withId(R.id.email)).perform(replaceText(""));
        onView(withId(R.id.phone)).perform(replaceText(""));
    }



    public void deleteOneCharFields()
    {
        onView(withId(R.id.name)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.band_location)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
    }

    public void enterOneCharAllFields()
    {
        onView(withId(R.id.name)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.band_location)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(typeText("1"));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(typeText("0"));
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
        onView(withId(R.id.detailsImage)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.band_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.selectGenres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        onView(withId(R.id.detailTitle)).check(matches(withText("Edit details")));
        onView(withId(R.id.nameLabel)).check(matches(withText("Band Name:")));
        onView(withId(R.id.name)).check(matches(withText("")));
        onView(withId(R.id.locationLabel)).check(matches(withText("Band Location:")));
        onView(withId(R.id.band_location)).check(matches(withText("")));
        onView(withId(R.id.distanceLabel)).check(matches(withText("Distance:")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
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
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.band_location)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.selectGenres)).check(matches(isDisplayed()));
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
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.changeImageLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.band_location)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.selectGenres)).check(matches(isDisplayed()));
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
        onView(withId(R.id.detailsImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.changeImageLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsImage)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.band_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.distanceLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.selectGenres)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }


    @Test
    public void testPopulateInitialFields() throws InterruptedException {
        Thread.sleep(2000);
        testRule.getActivity().onSuccessFromDatabase(bandData);
        Thread.sleep(2000);
        testRule.getActivity().onSuccessfulImageDownload();
        Thread.sleep(2000);
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.name)).check(matches(withText("test name")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("1")));
        onView(withId(R.id.genres)).check(matches(withText("test genre")));
        onView(withId(R.id.email)).check(matches(withText("test@email.com")));
        onView(withId(R.id.phone)).check(matches(withText("123")));
    }

    @Test
    public void testReinitialiseTabs() {

        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.name)).check(matches(withText("test name")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("1")));
        onView(withId(R.id.genres)).check(matches(withText("test genre")));
        onView(withId(R.id.email)).check(matches(withText("test@email.com")));
        onView(withId(R.id.phone)).check(matches(withText("123")));
        onView(withId(R.id.selectGenres)).check(matches(withText("Edit Genres")));
    }

    @Test
    public void testOnDataBaseResultListingFailure() {
        Enum result = ListingManager.CreationResult.LISTING_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Updating details failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultImageFailure() {
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
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.cancel)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testDataInput()
    {
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.name)).check(matches(withText("test name edit")));
        onView(withId(R.id.band_location)).check(matches(withText(endsWith("test location edit"))));
        onView(withId(R.id.venue_description_final)).check(matches(withText(endsWith("1"))));
        onView(withId(R.id.email)).check(matches(withText("test@email.com")));
        onView(withId(R.id.phone)).check(matches(withText("0")));
        onView(withId(R.id.selectGenres)).check(matches(withText("Select Genres")));
    }

    @Test
    public void testDataInputSwipeAwaySwipeBack()
    {
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.name)).check(matches(withText("test name edit")));
        onView(withId(R.id.band_location)).check(matches(withText(endsWith("test location edit"))));
        onView(withId(R.id.venue_description_final)).check(matches(withText(endsWith("1"))));
        onView(withId(R.id.email)).check(matches(withText("test@email.com")));
        onView(withId(R.id.phone)).check(matches(withText("0")));
    }

    @Test
    public void testButtonColourChangeOnValidData()
    {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        verify(manager,times(1)).getImage(testRule.getActivity());
    }

    @Test
    public void testButtonColourChangeOnInvalidData() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessfulImageDownload();
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
        verify(manager,times(1)).getImage(testRule.getActivity());
    }

    @Test
    public void testDataInputOnceThenDeleteOneChar() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        deleteOneCharFields();
        onView(withId(R.id.name)).check(matches(withText("")));
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
        onView(withId(R.id.genres)).check(matches(withText("")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));
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
    public void testUnusedOverrideOnSuccessFromDatabase()
    {
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable) confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
        onView(withId(R.id.createListing)).perform(click());
        testRule.getActivity().onSuccessFromDatabase(bandData, bandData);
        verify(manager,times(1)).getImage(testRule.getActivity());
        onView(withId(R.id.name)).check(matches(withText("")));
        onView(withId(R.id.band_location)).check(matches(withText(endsWith(""))));
        onView(withId(R.id.venue_description_final)).check(matches(withText(endsWith(""))));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));
    }

    @Test
    public void testSelectGenres()
    {
        testRule.getActivity().onSuccessFromDatabase(null);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.selectGenres)).perform(click());
        onView(withId(R.id.genresMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(bandData);
        assertThat(confirmationClass.getBand(),is(equalTo(bandData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testBeginTabPreservation()
    {
        TabStatePreserver tabPreserver = mock(TabStatePreserver.class);
        confirmationClass.setTabPreserver(tabPreserver);
        confirmationClass.beginTabPreservation();
        verify(tabPreserver,times(1)).preserveTabState();
    }

    @Test
    public void testvalidateDataMapEmptyField()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setBand(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        listing.put("email", "valid@email.com");
        confirmationClass.setBand(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }
}




