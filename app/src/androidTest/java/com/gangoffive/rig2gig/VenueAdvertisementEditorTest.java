package com.gangoffive.rig2gig;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.management.VenueAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VenueAdvertisementEditorTest {

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
        onView(withId(R.id.firstName)).check(matches(isDisplayed()));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
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
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
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
        onView(withId(R.id.firstName)).check(matches(isDisplayed()));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venueAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd(){
        testRule.getActivity().onSuccessFromDatabase(venueData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }

    @Test
    public void testOnSuccessFromDatabaseExistingAd() {
        testRule.getActivity().onSuccessFromDatabase(venueData, adData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }

    @Test
    public void testPopulateInitialFieldsNoAd(){
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(venueData.get("name").toString())));
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
    }

    @Test
    public void testPopulateInitialFieldsExistingAd(){
        testRule.getActivity().setVenue(venueData);
        testRule.getActivity().setPreviousListing(adData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(venueData.get("name").toString())));
        onView(withId(R.id.createVenueAdMain)).perform(swipeLeft());
        onView(withId(R.id.venue_description_final)).check(matches(withText(venueData.get("description").toString())));
    }

    @Test
    public void testOnDataBaseResultListingFailure() throws InterruptedException {
        Thread.sleep(1000);
        Enum result = ListingManager.CreationResult.LISTING_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Advertisement edit failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultImageFailure()
    {
        Enum result = ListingManager.CreationResult.IMAGE_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Advertisement edit failed.  Check your connection and try again"))
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