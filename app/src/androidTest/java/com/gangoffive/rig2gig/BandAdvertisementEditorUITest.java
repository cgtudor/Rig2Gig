package com.gangoffive.rig2gig;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.management.BandAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BandAdvertisementEditorUITest {

    private HashMap<String, Object> bandData, adData;

    @Rule
    public ActivityTestRule<BandAdvertisementEditor> testRule = new ActivityTestRule<BandAdvertisementEditor>(BandAdvertisementEditor.class);

    @Before
    public void setUp() throws Exception {
        bandData = new HashMap();
        bandData.put("availability","test availability");
        bandData.put("charge","test charge");
        bandData.put("description","test description");
        bandData.put("distance","test distance");
        bandData.put("email","test@email.com");
        bandData.put("genres","test genres");
        bandData.put("location","test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member");
        bandData.put("members",members);
        bandData.put("name","test name");
        bandData.put("phone-number","123");
        bandData.put("rating","test rating");
        testRule.getActivity().onSuccessFromDatabase(bandData);
        adData = new HashMap();
        adData.put("band-ref","test ref");
        adData.put("description","test description");
        adData.put("expiry-data","test data");
        ArrayList<String> positions = new ArrayList<>();
        members.add("Drums");
        members.add("Clarinet");
        adData.put("position",positions);
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.createBandAdMain)).check(matches(isDisplayed()));
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
        onView(withId(R.id.bandAdImageMain)).check(matches(isDisplayed()));
        onView(withId(R.id.positionsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageButtonLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.positionsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.positionsScroll)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.gridView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.search_bar_holder)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.search_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.list_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.searchHint)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(withId(R.id.title)).check(matches(withText("Advertise yourself to musicians")));
        onView(withId(R.id.cancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.createListing)).check(matches(withText("Confirm")));
        onView(withId(R.id.galleryImage)).check(matches(withText("Gallery")));
        onView(withId(R.id.takePhoto)).check(matches(withText("Camera")));
        onView(withId(R.id.searchHint)).check(matches(withText("Select desired band positions below")));
    }

    @Test
    public void testSwipeLeftOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.bandAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.positionsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.positionsScroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.list_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.searchHint)).check(matches(isDisplayed()));
        onView(withId(R.id.bandAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        onView(withId(R.id.positionsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.positionsScroll)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.gridView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.search_bar_holder)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.search_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.list_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.searchHint)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.bandAdDetailsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeLeftTwiceThenRightOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.bandAdImageMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imageButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.galleryImage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.takePhoto)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.positionsMain)).check(matches(isDisplayed()));
        onView(withId(R.id.positionsScroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.list_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.searchHint)).check(matches(isDisplayed()));
        onView(withId(R.id.bandAdDetailsMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.descriptionLabel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.venue_description_final)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(bandData);
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
        testRule.getActivity().onSuccessFromDatabase(bandData, adData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-5855578, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }

    @Test
    public void testPopulateInitialFieldsNoAd(){
        testRule.getActivity().setBand(bandData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().setViewReferences();
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
    }

    @Test
    public void testPopulateInitialFieldsExistingAd(){
        testRule.getActivity().setBand(bandData);
        testRule.getActivity().setPreviousListing(adData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().setViewReferences();
        onView(withId(R.id.venue_description_final)).check(matches(withText(bandData.get("description").toString())));
    }

    @Test
    public void testReinitialiseTabsNoAd(){
        testRule.getActivity().setBand(bandData);
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.firstName)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().setViewReferences();
        onView(withId(R.id.venue_description_final)).check(matches(withText("")));
    }

    @Test
    public void testReinitialiseExistingAd(){
        testRule.getActivity().setBand(bandData);
        testRule.getActivity().setPreviousListing(adData);
        testRule.getActivity().reinitialiseTabs();
        onView(withId(R.id.firstName)).check(matches(withText(bandData.get("name").toString())));
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        testRule.getActivity().setViewReferences();
        onView(withId(R.id.venue_description_final)).check(matches(withText(bandData.get("description").toString())));
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
    public void testOnDataBaseResultImageFailure() throws InterruptedException {
        Thread.sleep(1000);
        Enum result = ListingManager.CreationResult.IMAGE_FAILURE;
        testRule.getActivity().handleDatabaseResponse(result);
        onView(withText("Advertisement edit failed.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testOnDataBaseResultSuccess() throws InterruptedException {
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setListingManager(manager);
        when(manager.getListingRef()).thenReturn("testRef");
        Enum result = ListingManager.CreationResult.SUCCESS;
        testRule.getActivity().handleDatabaseResponse(result);
        assertTrue(testRule.getActivity().isFinishing());
        onView(withId(R.id.bandListingDetailsMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelAdvertisement()
    {
        onView(withId(R.id.cancel)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }



}
