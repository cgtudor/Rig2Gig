package com.gangoffive.rig2gig.Console;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.navbar.BandConsoleActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Rule;
import org.junit.Test;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BandConsoleActivityUITest
{
    @Rule
    public ActivityTestRule<BandConsoleActivity> testRule = new ActivityTestRule<BandConsoleActivity>(BandConsoleActivity.class);

    @Test
    public void testComponentVisibilityPerformerHasAd()
    {
        onView(ViewMatchers.withId(R.id.general)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_Venues)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_musicians)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_manage_members)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_band)).check(matches(isDisplayed()));

        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> list = mock(List.class);
        DocumentSnapshot docSnap = mock(DocumentSnapshot.class);
        when(snapshot.getDocuments()).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(docSnap.getId()).thenReturn("TestID");
        when(list.get(anyInt())).thenReturn(docSnap);

        testRule.getActivity().getPerformerSuccessListener().onSuccess(snapshot);

        onView(withId(R.id.performer_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_performer_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_performer_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_delete_performer_advert)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibilityPerformerNoAd()
    {
        onView(withId(R.id.general)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_Venues)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_musicians)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_manage_members)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_band)).check(matches(isDisplayed()));

        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> list = mock(List.class);
        DocumentSnapshot docSnap = mock(DocumentSnapshot.class);
        when(snapshot.getDocuments()).thenReturn(list);
        when(list.isEmpty()).thenReturn(true);
        when(docSnap.getId()).thenReturn("TestID");
        when(list.get(anyInt())).thenReturn(docSnap);

        testRule.getActivity().getPerformerSuccessListener().onSuccess(snapshot);

        onView(withId(R.id.performer_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_create_performer_advert)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibilityBandHasAd()
    {
        onView(withId(R.id.general)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_Venues)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_musicians)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_manage_members)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_band)).check(matches(isDisplayed()));

        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> list = mock(List.class);
        DocumentSnapshot docSnap = mock(DocumentSnapshot.class);
        when(snapshot.getDocuments()).thenReturn(list);
        when(list.isEmpty()).thenReturn(false);
        when(docSnap.getId()).thenReturn("TestID");
        when(list.get(anyInt())).thenReturn(docSnap);

        testRule.getActivity().getBandSuccessListener().onSuccess(snapshot);

        onView(withId(R.id.band_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_band_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_band_band_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_delete_band_advert)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testComponentVisibilityBandNoAd()
    {
        onView(withId(R.id.general)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_Venues)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_view_musicians)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_manage_members)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_edit_band)).check(matches(isDisplayed()));

        QuerySnapshot snapshot = mock(QuerySnapshot.class);
        List<DocumentSnapshot> list = mock(List.class);
        DocumentSnapshot docSnap = mock(DocumentSnapshot.class);
        when(snapshot.getDocuments()).thenReturn(list);
        when(list.isEmpty()).thenReturn(true);
        when(docSnap.getId()).thenReturn("TestID");
        when(list.get(anyInt())).thenReturn(docSnap);

        testRule.getActivity().getBandSuccessListener().onSuccess(snapshot);

        onView(withId(R.id.band_advert)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view_create_band_advert)).check(matches(isDisplayed()));
    }
}