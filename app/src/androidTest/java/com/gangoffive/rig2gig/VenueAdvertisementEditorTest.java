package com.gangoffive.rig2gig;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VenueAdvertisementEditorTest {

    private VenueAdvertisementEditor confirmationClass, mockClass;
    private HashMap<String, Object> venueData, adData;

    @Rule
    public ActivityTestRule<VenueAdvertisementEditor> testRule = new ActivityTestRule<VenueAdvertisementEditor>(VenueAdvertisementEditor.class);
    
    @Before
    public void setUp() throws Exception {
        venueData = new HashMap();
        venueData.put("email-address","test email");
        venueData.put("location","test location");
        venueData.put("name","test name");
        venueData.put("phone-number","123");
        venueData.put("rating","test rating");
        venueData.put("user-ref","test id");
        venueData.put("description","test description");
        adData = new HashMap();
        adData.put("venue-ref","test ref");
        adData.put("expiry-date","test date");
        adData.put("description","test description");
        testRule.getActivity().setViewReferences();
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.createVenueAdMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(isDisplayed()));
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(withId(R.id.title)).check(matches(withText("Advertise yourself to performers")));
        onView(withId(R.id.cancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.createListing)).check(matches(withText("Confirm")));
        onView(withId(R.id.galleryImage)).check(matches(withText("Gallery")));
        onView(withId(R.id.takePhoto)).check(matches(withText("Camera")));

    }

    @Test
    public void testSwipeLeftOnce() throws InterruptedException {
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.description)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.detailView)).check(matches(isDisplayed()));

    }

    @Test
    public void testSwipeLeftTwice() throws InterruptedException {
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.description)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.detailView)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeLeftThenRightOnce() throws InterruptedException {
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.createVenueAdMain)).perform(swipeRight());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.name)).check(matches(isDisplayed()));
        onView(withId(R.id.imageView)).check(matches(isDisplayed()));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.detailView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(venueData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5062987, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-11711154, intColour);
    }

    @Test
    public void testOnSuccessFromDatabaseExistingAd() {
        testRule.getActivity().onSuccessFromDatabase(venueData, adData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-16743049, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }

    @Test
    public void testPopulateInitialFieldsNoAd(){
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.name)).check(matches(withText(venueData.get("name").toString())));
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.description)).check(matches(withText("")));
    }

    @Test
    public void testPopulateInitialFieldsExistingAd(){
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().setPreviousListing(adData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.name)).check(matches(withText(venueData.get("name").toString())));
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.description)).check(matches(withText(venueData.get("description").toString())));
    }

    @Test
    public void testOnDataBaseResultListingFailure()
    {
        Enum result = ListingManager.CreationResult.LISTING_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Listing creation failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultImageFailure()
    {
        Enum result = ListingManager.CreationResult.IMAGE_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Listing creation failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultSuccess()
    {
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setListingManager(manager);
        testRule.getActivity().setListingRef("");
        when(manager.getListingRef()).thenReturn("testRef");
        Enum result = ListingManager.CreationResult.SUCCESS;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Advertisement created successfully"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        assertTrue(testRule.getActivity().isFinishing());
        onView(withId(R.id.venueListingDetailsMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelAdvertisement()
    {
        onView(withId(R.id.cancel)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }



}