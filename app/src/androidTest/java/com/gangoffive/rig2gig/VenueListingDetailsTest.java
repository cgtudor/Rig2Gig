package com.gangoffive.rig2gig;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Looper;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.details.VenueListingDetailsActivity;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VenueListingDetailsTest {

    @Rule
    public ActivityTestRule<VenueListingDetailsActivity> testRule = new ActivityTestRule<VenueListingDetailsActivity>(VenueListingDetailsActivity.class);

    @Before
    public void setUp() throws Exception {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    @Test
    public void testActivityInView() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("IzBSmyKhTC3cp9ga9P4A");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        onView(withId(R.id.venueListingDetailsMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentDisplayedDefault() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("IzBSmyKhTC3cp9ga9P4A");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.venuePhoto)).check(matches(isDisplayed()));
        onView(withId(R.id.venueName)).check(matches(isDisplayed()));
        onView(withId(R.id.venueNameBar)).check(matches(isDisplayed()));
        onView(withId(R.id.location)).check(matches(isDisplayed()));
        onView(withId(R.id.locationBar)).check(matches(isDisplayed()));
        onView(withId(R.id.description)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionBar)).check(matches(isDisplayed()));
        onView(withId(R.id.contact)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.publish)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.my_venue_rating)).check(matches(isDisplayed()));
        onView(withId(R.id.rating_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.unrated)).check(matches(isDisplayed()));
        onView(withId(R.id.findUsHere)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.google_map)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testComponentDisplayedOwnAd() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("IzBSmyKhTC3cp9ga9P4A");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        onView(withId(R.id.contact)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testComponentDisplayedOwnAdNotPublished() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("IzBSmyKhTC3cp9ga9P4A");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        onView(withId(R.id.publish)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testTextDefault() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test-ref");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        Thread.sleep(2000);

        onView(withId(R.id.noInternet)).check(matches(withText("No Internet Connection.")));
        onView(withId(R.id.venueName)).check(matches(withText("test")));
        onView(withId(R.id.location)).check(matches(withText("test")));
        onView(withId(R.id.description)).check(matches(withText("test")));
        onView(withId(R.id.contact)).check(matches(withText("Request Contact")));
        onView(withId(R.id.profile)).check(matches(withText("Profile")));
        onView(withId(R.id.my_venue_rating)).check(matches(withText("Our Venue Rating")));
        onView(withId(R.id.findUsHere)).check(matches(withText("Find us here!")));
        onView(withId(R.id.unrated)).check(matches(withText("Not enough ratings gathered yet")));
        assertEquals(testRule.getActivity().getSupportActionBar().getTitle(), "test");
    }

    @Test
    public void testTextOwnAd() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        assertEquals(testRule.getActivity().getSupportActionBar().getTitle(), "My Advert");
    }

    @Test
    public void testTextOwnAdNotPublished() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        when(venueTask.getResult()).thenReturn(venueDoc);
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);

        assertEquals(testRule.getActivity().getSupportActionBar().getTitle(), "My Advert Preview");
    }

    @Test
    public void testRequestContactClickedDefault() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);

        onView(withId(R.id.contact)).perform(click());
        Task<QuerySnapshot> musiciansTask = mock(Task.class);
        when(musiciansTask.isSuccessful()).thenReturn(true);
        QuerySnapshot musiciansQuery = mock(QuerySnapshot.class);
        when(musiciansQuery.isEmpty()).thenReturn(false);
        DocumentSnapshot musicianDoc = mock(DocumentSnapshot.class);
        when(musicianDoc.get("name")).thenReturn("test");
        ArrayList<DocumentSnapshot> musiciansList = new ArrayList<>();
        musiciansList.add(musicianDoc);
        when(musiciansQuery.getDocuments()).thenReturn(musiciansList);
        when(musiciansTask.getResult()).thenReturn(musiciansQuery);
        testRule.getActivity().onSuccessBandContact(musiciansTask);
        testRule.getActivity().onSuccessAddRequest();

        Thread.sleep(2000);

        onView(withId(R.id.contact)).check(matches(withText("Contact request sent")));
        onView(withId(R.id.contact)).check(matches(not(isClickable())));
    }

    @Test
    public void testRequestContactClickedMusician() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);

        testRule.getActivity().setCurrentUserType("musicians");
        onView(withId(R.id.contact)).perform(click());
        Task<QuerySnapshot> musiciansTask = mock(Task.class);
        when(musiciansTask.isSuccessful()).thenReturn(true);
        QuerySnapshot musiciansQuery = mock(QuerySnapshot.class);
        when(musiciansQuery.isEmpty()).thenReturn(false);
        DocumentSnapshot musicianDoc = mock(DocumentSnapshot.class);
        when(musicianDoc.get("name")).thenReturn("test");
        ArrayList<DocumentSnapshot> musiciansList = new ArrayList<>();
        musiciansList.add(musicianDoc);
        when(musiciansQuery.getDocuments()).thenReturn(musiciansList);
        when(musiciansTask.getResult()).thenReturn(musiciansQuery);
        testRule.getActivity().onSuccessMusicianContact(musiciansTask);
        testRule.getActivity().onSuccessAddRequest();

        Thread.sleep(2000);

        onView(withId(R.id.contact)).check(matches(withText("Contact request sent")));
        onView(withId(R.id.contact)).check(matches(not(isClickable())));
    }

    @Test
    public void testStarIfFavourited() throws InterruptedException {
        Thread.sleep(4000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test-ref");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref-ref-f");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        Task<DocumentSnapshot> starTask = mock(Task.class);
        DocumentSnapshot starDoc = mock(DocumentSnapshot.class);
        when(starDoc.exists()).thenReturn(true);
        when(starTask.getResult()).thenReturn(starDoc);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessFavouriteVenues(starTask);
        Thread.sleep(4000);

        assertEquals(testRule.getActivity().getDrawable(R.drawable.ic_empty_star).getConstantState(), testRule.getActivity().getActivityMenu().findItem(R.id.saveButton).getIcon().getConstantState());
    }

    @Test
    public void testStarIfNotFavourited() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test-ref-nf");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref-ref-nf");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        Task<DocumentSnapshot> starTask = mock(Task.class);
        DocumentSnapshot starDoc = mock(DocumentSnapshot.class);
        when(starDoc.exists()).thenReturn(false);
        when(starTask.getResult()).thenReturn(starDoc);
        Thread.sleep(2000);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        Thread.sleep(2000);
        testRule.getActivity().onSuccessFavouriteVenues(starTask);
        Thread.sleep(5000);

        assertEquals(testRule.getActivity().getDrawable(R.drawable.ic_empty_star).getConstantState(), testRule.getActivity().getActivityMenu().findItem(R.id.saveButton).getIcon().getConstantState());
    }

    @Test
    public void testUnfavourite() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test-ref");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessFavouriteVenues(venueTask);

        onView(withId(R.id.saveButton)).perform(click());
        HashMap<String, Object> listing = new HashMap<>();
        listing.put("description", "test");
        listing.put("expiry-date", new Timestamp(calendar.getTime()));
        listing.put("venue-ref", "test-ref");
        Task<DocumentSnapshot> starTask = mock(Task.class);
        DocumentSnapshot starDoc = mock(DocumentSnapshot.class);
        when(starDoc.exists()).thenReturn(true);
        when(starTask.getResult()).thenReturn(starDoc);
        testRule.getActivity().onSuccessItemSelectedAd(starTask, testRule.getActivity().getActivityMenu().findItem(R.id.saveButton), listing);
        testRule.getActivity().onSuccessUnfavourited(testRule.getActivity().getActivityMenu().findItem(R.id.saveButton));

        Thread.sleep(2000);

        assertEquals(testRule.getActivity().getDrawable(R.drawable.ic_empty_star).getConstantState(), testRule.getActivity().getActivityMenu().findItem(R.id.saveButton).getIcon().getConstantState());
    }
    @Test
    public void testFavourite() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test-ref");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessFavouriteVenues(venueTask);

        onView(withId(R.id.saveButton)).perform(click());
        HashMap<String, Object> listing = new HashMap<>();
        listing.put("description", "test");
        listing.put("expiry-date", new Timestamp(calendar.getTime()));
        listing.put("venue-ref", "test-ref");
        Task<DocumentSnapshot> starTask = mock(Task.class);
        DocumentSnapshot starDoc = mock(DocumentSnapshot.class);
        when(starDoc.exists()).thenReturn(false);
        when(starTask.getResult()).thenReturn(starDoc);
        testRule.getActivity().onSuccessItemSelectedAd(starTask, testRule.getActivity().getActivityMenu().findItem(R.id.saveButton), listing);
        testRule.getActivity().onSuccessFavourited(testRule.getActivity().getActivityMenu().findItem(R.id.saveButton));

        Thread.sleep(2000);

        assertEquals(testRule.getActivity().getDrawable(R.drawable.ic_full_star).getConstantState(), testRule.getActivity().getActivityMenu().findItem(R.id.saveButton).getIcon().getConstantState());
    }

    @Test
    public void testPublishClickCancelled() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        when(venueTask.getResult()).thenReturn(venueDoc);
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));

        PayPalPayment payment = new PayPalPayment(new BigDecimal("5"), "GBP", "30-days Advert",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent resultData = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);
        Intents.init();
        intending(hasComponent(PaymentActivity.class.getName())).respondWith(result);
        onView(withId(R.id.publish)).perform(click());   //however you trigger the intent in your code
        intended(hasComponent(PaymentActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testPublishClickSuccessful() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        when(venueTask.getResult()).thenReturn(venueDoc);
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));

        PayPalPayment payment = new PayPalPayment(new BigDecimal("5"), "GBP", "30-days Advert",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent resultData = new Intent();
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.init();
        intending(hasComponent(PaymentActivity.class.getName())).respondWith(result);
        onView(withId(R.id.publish)).perform(click());
        testRule.getActivity().paymentConfirmed();
        assertTrue(testRule.getActivity().isFinishing());
        intended(hasComponent(PaymentActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void testPressBackVenue() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);

        Task<DocumentSnapshot> backTask = mock(Task.class);
        DocumentSnapshot backDoc = mock(DocumentSnapshot.class);
        when(backDoc.exists()).thenReturn(true);
        when(backDoc.get("user-type")).thenReturn("Venue");
        when(backTask.getResult()).thenReturn(backDoc);

        Intents.init();
        pressBack();
        testRule.getActivity().onSuccessBackPressed(backTask);
        assertTrue(testRule.getActivity().isFinishing());
        intended(hasComponent(NavBarActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testPressBackMusician() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);

        Task<DocumentSnapshot> backTask = mock(Task.class);
        DocumentSnapshot backDoc = mock(DocumentSnapshot.class);
        when(backDoc.exists()).thenReturn(true);
        when(backDoc.get("user-type")).thenReturn("Musician");
        when(backTask.getResult()).thenReturn(backDoc);

        pressBack();
        testRule.getActivity().onSuccessBackPressed(backTask);
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testPressSupportActionBack() throws InterruptedException {
        Thread.sleep(2000);
        Task<DocumentSnapshot> adTask = mock(Task.class);
        DocumentSnapshot adDoc = mock(DocumentSnapshot.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        when(adDoc.exists()).thenReturn(true);
        when(adDoc.get("expiry-date")).thenReturn(new Timestamp(calendar.getTime()));
        when(adDoc.get("venue-ref")).thenReturn("test");
        when(adDoc.get("description")).thenReturn("test");
        when(adTask.getResult()).thenReturn(adDoc);
        testRule.getActivity().onSuccessAdData(adTask);
        Task<DocumentSnapshot> venueTask = mock(Task.class);
        DocumentSnapshot venueDoc = mock(DocumentSnapshot.class);
        when(venueDoc.exists()).thenReturn(true);
        when(venueDoc.get("name")).thenReturn("test");
        when(venueDoc.get("location")).thenReturn("test");
        when(venueDoc.get("latitude")).thenReturn("50");
        when(venueDoc.get("longitude")).thenReturn("50");
        when(venueDoc.get("user-ref")).thenReturn("test-ref");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        when(adTask.isSuccessful()).thenReturn(true);
        when(venueTask.isSuccessful()).thenReturn(true);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        testRule.getActivity().onSuccessItemsAd(adTask);
        testRule.getActivity().onSuccessItemsVenue(venueTask);
        testRule.getActivity().onSuccessMapAd(adTask);
        testRule.getActivity().onSuccessMapVenue(venueTask);

        Task<DocumentSnapshot> backTask = mock(Task.class);
        DocumentSnapshot backDoc = mock(DocumentSnapshot.class);
        when(backDoc.exists()).thenReturn(true);
        when(backDoc.get("user-type")).thenReturn("Musician");
        when(backTask.getResult()).thenReturn(backDoc);

        onView(withContentDescription("Navigate up")).perform(click());
        testRule.getActivity().onSuccessBackPressed(backTask);

        assertTrue(testRule.getActivity().isFinishing());
    }
}
