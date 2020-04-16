package com.gangoffive.rig2gig;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.advert.management.SearchedMusicianDetails;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class SearchedMusicianDetailsTest {

    private static HashMap<String,Object> musicianData, userData;

        @Rule
        public ActivityTestRule<SearchedMusicianDetails> testRule = new ActivityTestRule<SearchedMusicianDetails>(SearchedMusicianDetails.class);

        @BeforeClass
        public static void setup()
        {
            musicianData = new HashMap<>();
            musicianData.put("name", "musician name");
            musicianData.put("distance", "musician distance");
            musicianData.put("genres", "musician genres");
            musicianData.put("phone-number", "musician number");
            musicianData.put("user-ref", "musician ref");
            musicianData.put("rating", "musician rating");
            musicianData.put("location", "musician location");
            userData = new HashMap<>();
            userData.put("family-name", "user family name");
            userData.put("given-name", "user given name");
            userData.put("phone-number", "user number");
            userData.put("email-address", "user email");
            userData.put("user-type", "Musician");
            userData.put("username", "user username");
        }

        @Test
        public void testActivityInView()
        {
            onView(withId(R.id.bandMemberDetails)).check(matches(isDisplayed()));
        }

        @Test
        public void testComponentVisibility()
        {
            onView(withId(R.id.name_label)).check(matches(isDisplayed()));
            onView(withId(R.id.name)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.userNameLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.userName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.locationLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.location)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.ratingLabel)).check(matches(isDisplayed()));
            onView(withId(R.id.rating)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.ok)).check(matches(isDisplayed()));
        }

        @Test
        public void testTextOfComponents()
        {
            onView(withId(R.id.name_label)).check(matches(withText("Name:")));
            onView(withId(R.id.name)).check(matches(withText("")));
            onView(withId(R.id.userNameLabel)).check(matches(withText("User name:")));
            onView(withId(R.id.userName)).check(matches(withText("")));
            onView(withId(R.id.locationLabel)).check(matches(withText("Location:")));
            onView(withId(R.id.location)).check(matches(withText("")));
            onView(withId(R.id.ratingLabel)).check(matches(withText("Rating:")));
            onView(withId(R.id.rating)).check(matches(withText("")));
            onView(withId(R.id.ok)).check(matches(withText("OK")));
        }

        @Test
        public void testOnSuccessFromDatabase() throws InterruptedException {
            testRule.getActivity().onSuccessFromDatabase(musicianData);
            testRule.getActivity().onSuccessFromDatabase(userData);
            onView(withId(R.id.name_label)).check(matches(withText("Name:")));
            onView(withId(R.id.name)).check(matches(withText("musician name")));
            onView(withId(R.id.userNameLabel)).check(matches(withText("User name:")));
            onView(withId(R.id.userName)).check(matches(withText("user username")));
            onView(withId(R.id.locationLabel)).check(matches(withText("Location:")));
            onView(withId(R.id.location)).check(matches(withText("musician location")));
            onView(withId(R.id.ratingLabel)).check(matches(withText("Rating:")));
            onView(withId(R.id.rating)).check(matches(withText("musician rating")));
            onView(withId(R.id.ok)).check(matches(withText("OK")));
        }

        @Test
        public void testOnFailFromDatabaseClickOk() {
            onView(withId(R.id.ok)).perform(click());
            assertTrue(testRule.getActivity().isFinishing());
        }

        @Test
        public void testOnSuccessFromDatabaseClickOk() {
            testRule.getActivity().onSuccessFromDatabase(musicianData);
            testRule.getActivity().onSuccessFromDatabase(userData);
            onView(withId(R.id.ok)).perform(click());
            assertTrue(testRule.getActivity().isFinishing());
        }
}