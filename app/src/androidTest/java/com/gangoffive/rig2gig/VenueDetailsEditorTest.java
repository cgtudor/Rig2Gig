package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Button;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.venue.management.VenueDetailsEditor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.CursorMatchers.withRowString;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VenueDetailsEditorTest {
    private HashMap<String, Object> venueData;
    private Activity activty;
    private ListingManager manager;

    @Rule
    public ActivityTestRule<VenueDetailsEditor> testRule = new ActivityTestRule(VenueDetailsEditor.class);

    @BeforeClass
    public static void setup() {
        Looper.prepare();
    }

    @Before
    public void setUp() throws Exception {
        venueData = new HashMap();
        venueData.put("availability", "test availability");
        venueData.put("charge", "test charge");
        venueData.put("description", "test description");
        venueData.put("email-address", "test@email.com");
        venueData.put("latitude","10");
        venueData.put("longitude","6");
        venueData.put("type", "test type");
        venueData.put("location", "test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member");
        venueData.put("members", members);
        venueData.put("name", "test name");
        venueData.put("phone-number", "123");
        venueData.put("rating", "test rating");
        manager = mock(ListingManager.class);
        //when(manager.postDataToDatabase(any(),any(Drawable.class),any(CreateAdvertisement.class)))
        testRule.getActivity().setListingManager(manager);
    }

    public void enterTestData()
    {

        onView(withId(R.id.venue_name_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("test name edit"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_location)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("test location edit"));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("test email edit"));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("0"));
        closeSoftKeyboard();
    }

    public void deleteAllFields()
    {
        onView(withId(R.id.venue_name_final)).perform(replaceText(""));
        onView(withId(R.id.email)).perform(replaceText(""));
        onView(withId(R.id.phone)).perform(replaceText(""));
    }



    public void deleteOneCharFields()
    {
        onView(withId(R.id.venue_name_final)).perform(click()).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.venue_location)).perform(click()).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(click()).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(click()).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(pressKey(KeyEvent.KEYCODE_DEL));
        closeSoftKeyboard();
    }

    public void enterOneCharAllFields()
    {
        onView(withId(R.id.venue_name_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_location)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.email)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.phone)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("0"));
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
        onView(withId(R.id.venue_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.typeLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.type)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        onView(withId(R.id.nameLabel)).check(matches(withText("Venue Name:")));
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.locationLabel)).check(matches(withText("Venue Location:")));
        onView(withId(R.id.venue_location)).check(matches(withText("")));
        onView(withId(R.id.typeLabel)).check(matches(withText("Venue type:")));
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
        onView(withId(R.id.detailsImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_name_final)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_location)).check(matches(isDisplayed()));
        onView(withId(R.id.typeLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.type)).check(matches(isDisplayed()));
        onView(withId(R.id.emailLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.email)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.phone)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeLeftTwice() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsTitle)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.nameLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_name_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.locationLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.typeLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.type)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.emailLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.email)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.phoneLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.phone)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeLeftOnceThenRightOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
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
        onView(withId(R.id.venue_location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.typeLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.type)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.emailLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.email)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.phoneLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.phone)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testSwipeLeftTwiceThenRightOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.nameLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_name_final)).check(matches(isDisplayed()));
        onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_location)).check(matches(isDisplayed()));
        onView(withId(R.id.typeLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.type)).check(matches(isDisplayed()));
        onView(withId(R.id.emailLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.email)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.phone)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(venueData);
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
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));
    }

    @Test
    public void testReinitialiseTabs() throws InterruptedException {

        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
        onView(withId(R.id.email)).check(matches(withText("")));
        onView(withId(R.id.phone)).check(matches(withText("")));

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
    public void testCancelAdvertisement() throws InterruptedException {
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
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.venue_name_final)).check(matches(withText("test name edit")));
        onView(withId(R.id.email)).check(matches(withText("test email edit")));
        onView(withId(R.id.phone)).check(matches(withText("0")));
    }

    @Test
    public void testDataInputSwipeAwaySwipeBack()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterTestData();
        onView(withId(R.id.view_pager)).perform(swipeRight());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.venue_name_final)).check(matches(withText("test name edit")));
        onView(withId(R.id.email)).check(matches(withText("test email edit")));
        onView(withId(R.id.phone)).check(matches(withText("0")));
    }

    @Test
    public void testButtonColourChangeOnValidData()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
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
    public void testButtonColourChangeOnInvalidData() throws InterruptedException {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
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
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        enterOneCharAllFields();
        deleteOneCharFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("a")));
        onView(withId(R.id.email)).check(matches(withText("a")));
        onView(withId(R.id.phone)).check(matches(withText("0")));
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
    public void testDataInputOnceThenDeleteOneChar() throws InterruptedException {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        deleteOneCharFields();
        onView(withId(R.id.venue_name_final)).check(matches(withText("")));
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
        verify(manager,times(0)).postDataToDatabase(any(),any(),any());
        onView(withText("Details not updated.  Ensure all fields are complete and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testButtonColourChangeOnDescriptionValidData()
    {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.venue_description_final)).perform(click()).perform(pressKey(KeyEvent.KEYCODE_MOVE_END)).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).check(matches(withText("test descriptiona")));
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
    public void testButtonColourChangeOnDescriptionInvalidData() throws InterruptedException {
        testRule.getActivity().setViewReferences();
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().onSuccessFromDatabase(venueData);
        testRule.getActivity().saveTabs();
        testRule.getActivity().setViewReferences();
        testRule.getActivity().populateInitialFields();
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        enterOneCharAllFields();
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.venue_description_final)).perform(replaceText(""));
        closeSoftKeyboard();
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
}