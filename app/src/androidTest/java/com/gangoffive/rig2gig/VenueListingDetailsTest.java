package com.gangoffive.rig2gig;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.details.VenueListingDetailsActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VenueListingDetailsTest {

    @Rule
    public ActivityTestRule<VenueListingDetailsActivity> testRule = new ActivityTestRule<VenueListingDetailsActivity>(VenueListingDetailsActivity.class);

    @Before
    public void setUp() throws Exception {
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
        when(venueDoc.get("user-ref")).thenReturn("test");
        when(venueDoc.get("venue-rating")).thenReturn("N/A");
        when(venueTask.getResult()).thenReturn(venueDoc);
        testRule.getActivity().onSuccessVenueData(venueTask, new Timestamp(calendar.getTime()));
        Thread.sleep(2000);
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.venueListingDetailsMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentDisplayed()
    {
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
        onView(withId(R.id.my_venue_rating)).check(matches(isDisplayed()));
        onView(withId(R.id.rating_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.unrated)).check(matches(isDisplayed()));
        onView(withId(R.id.findUsHere)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.google_map)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

}
