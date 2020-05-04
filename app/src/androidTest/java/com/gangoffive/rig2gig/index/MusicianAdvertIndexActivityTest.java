package com.gangoffive.rig2gig.index;

import android.os.Looper;
import android.widget.TextView;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.index.MusicianAdvertIndexActivity;
import com.gangoffive.rig2gig.band.management.ManageBandMembersActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

public class MusicianAdvertIndexActivityTest
{
/*    private IntentsTestRule intentsTestRule = new IntentsTestRule(MusicianAdvertIndexActivity.class);

    @BeforeClass
    public static void setupClass() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

    @Before
    public void setup()
    {

    }

    @Rule
    public ActivityTestRule<MusicianAdvertIndexActivity> testRule = new ActivityTestRule<>(MusicianAdvertIndexActivity.class);

    @Test
    public void testActivityInView()
    {
        onView(ViewMatchers.withId(R.id.musicianAdvertIndexMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.viewMusiciansMain)).check(matches(isDisplayed()));
        onView(withId(R.id.viewSwipeContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.viewRecyclerView)).check(matches(isDisplayed()));
        *//*onView(withId(R.id.savedMusiciansMain)).check(matches(isDisplayed()));
        onView(withId(R.id.savedSwipeContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.savedRecyclerView)).check(matches(isDisplayed()));*//*
    }

    @Test
    public void testTextOfComponents()
    {
        onView(allOf(isAssignableFrom(TextView.class), withParent(withResourceName("toolbar")))).check(matches(withText("Musician Adverts")));
    }

    @Test
    public void testOnSuccessFromDatabaseOneAdvert() throws InterruptedException {

    }

    @Test
    public void testOnSuccessFromDatabaseMultipleAdverts() {

    }

    @Test
    public void testClickAdvert() {

    }

    @Test
    public void testScroll()
    {

    }

    @Test
    public void testPressPhoneBackButton()
    {
        pressBack();
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testPressMenuBarBackButton()
    {
        onView(withContentDescription("Navigate up")).perform(click());
        assertTrue(testRule.getActivity().isBackClicked());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testSwipeToRefresh() {

    }

    @Test
    public void testOnActivityResultFavouritedAdvert() throws InterruptedException {

    }*/
}