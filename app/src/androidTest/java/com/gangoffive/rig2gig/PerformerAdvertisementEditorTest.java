package com.gangoffive.rig2gig;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.widget.Button;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.junit.Before;
import org.junit.BeforeClass;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PerformerAdvertisementEditorTest {

    private HashMap<String, Object> performerData, adData;
    private PerformerAdvertisementEditor confirmationClass, mockClass;

    @Rule
    public ActivityTestRule<PerformerAdvertisementEditor> testRule = new ActivityTestRule<PerformerAdvertisementEditor>(PerformerAdvertisementEditor.class);

    @BeforeClass
    public static void setup() {

        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    @Before
    public void setUp() throws Exception {
        confirmationClass = new PerformerAdvertisementEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void populateInitialFields() {}

        };
        mockClass = mock(PerformerAdvertisementEditor.class);
        performerData = new HashMap();
        performerData.put("availability","test availability");
        performerData.put("charge","test charge");
        performerData.put("description","test description");
        performerData.put("distance","0");
        performerData.put("email","test@email.com");
        performerData.put("genres","test genres");
        performerData.put("location","test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member");
        performerData.put("members",members);
        performerData.put("name","test name");
        performerData.put("phone-number","123");
        performerData.put("rating","test rating");
        testRule.getActivity().onSuccessFromDatabase(performerData);
        adData = new HashMap();
        adData.put("band-ref","test ref");
        adData.put("description","test description");
        adData.put("expiry-data","test data");
        adData.put("distance","0");
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.editPerformerAdMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel2)).check(matches(isDisplayed()));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(withId(R.id.title)).check(matches(withText("Advertise yourself to venues")));
        onView(withId(R.id.cancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.createListing)).check(matches(withText("Confirm")));
        onView(withId(R.id.galleryImage)).check(matches(withText("Gallery")));
        onView(withId(R.id.takePhoto)).check(matches(withText("Camera")));
    }

    @Test
    public void testSwipeLeftOnce() throws InterruptedException {
        onView(withId(R.id.editPerformerAdMain)).perform(swipeLeft());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel2)).check(matches(isDisplayed()));
    }


    @Test
    public void testSwipeRightOnce() throws InterruptedException {
        onView(withId(R.id.editPerformerAdMain)).perform(swipeRight());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.button_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
        onView(withId(R.id.createListing)).check(matches(isDisplayed()));
        onView(withId(R.id.firstName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.imageLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.galleryImage)).check(matches(isDisplayed()));
        onView(withId(R.id.takePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.distanceLabel2)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSuccessFromDatabaseNoAd() throws InterruptedException {
        testRule.getActivity().onSuccessFromDatabase(performerData);
        Button confirm = testRule.getActivity().findViewById(R.id.createListing);
        ColorDrawable colour = (ColorDrawable)confirm.getBackground();
        int intColour = colour.getColor();
        assertEquals(-15547671, intColour);
        ColorStateList textcolour = confirm.getTextColors();
        intColour = textcolour.getDefaultColor();
        assertEquals(-1, intColour);
    }

    @Test
    public void testOnSuccessFromDatabaseExistingAd() {
        testRule.getActivity().onSuccessFromDatabase(performerData, adData);
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
        testRule.getActivity().setPerformer(performerData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(performerData.get("name").toString())));
        onView(withId(R.id.venue_description_final)).check(matches(withText(performerData.get("distance").toString())));
    }

    @Test
    public void testPopulateInitialFieldsExistingAd(){
        testRule.getActivity().setPerformer(performerData);
        testRule.getActivity().setPreviousListing(adData);
        testRule.getActivity().populateInitialFields();
        onView(withId(R.id.firstName)).check(matches(withText(performerData.get("name").toString())));
        onView(withId(R.id.venue_description_final)).check(matches(withText(adData.get("distance").toString())));
    }


    @Test
    public void testOnDataBaseResultListingFailure() throws InterruptedException {
        Thread.sleep(2000);
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
        when(manager.getListingRef()).thenReturn("testRef");
        Enum result = ListingManager.CreationResult.SUCCESS;
        testRule.getActivity().handleDatabaseResponse(result);
        assertTrue(testRule.getActivity().isFinishing());
        onView(withId(R.id.performanceListingDetailsMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelAdvertisement()
    {
        onView(withId(R.id.cancel)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testOnSuccessFromDatabaseNoAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(performerData);
        assertThat(confirmationClass.getPerformer(),is(equalTo(performerData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testOnSuccessFromDatabaseWithAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(performerData, adData);
        assertThat(confirmationClass.getPerformer(),is(equalTo(performerData)));
        assertThat(confirmationClass.getPreviousListing(),is(equalTo(adData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testOnSuccessfulImageDownload()
    {
        mockClass.onSuccessfulImageDownload();
    }

    @Test
    public void testvalidateDataMapEmptyField()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }


    @Test
    public void testvalidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }

}
