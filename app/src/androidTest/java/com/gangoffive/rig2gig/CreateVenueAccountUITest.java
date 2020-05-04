package com.gangoffive.rig2gig;

import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.account.TabbedVenueActivity;
import com.gangoffive.rig2gig.musician.management.TabbedMusicianActivity;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class CreateVenueAccountUITest
{
    private HashMap<String, Object> performerData, adData;

    @Rule
    public ActivityTestRule<TabbedVenueActivity> testRule
            = new ActivityTestRule<TabbedVenueActivity>(TabbedVenueActivity.class);

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.accountInformation)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.emailReset)).check(matches(isDisplayed()));
        onView(withId(R.id.registerConfirmEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.registerPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.registerConfirmPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description_final)).check(matches(isDisplayed()));
        onView(withId(R.id.nameFirst)).check(matches(isDisplayed()));
        onView(withId(R.id.location)).check(matches(isDisplayed()));
        onView(withId(R.id.cPhoneNumber)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_name)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_location)).check(matches(isDisplayed()));
        onView(withId(R.id.venue_description)).check(matches(isDisplayed()));
    }

    @Test
    public void enterTestData()
    {
        onView(withId(R.id.emailReset)).perform(typeText("Test@Test.com"));
        closeSoftKeyboard();
        onView(withId(R.id.registerConfirmEmail)).perform(typeText("Test@Test.com"));
        closeSoftKeyboard();
        onView(withId(R.id.registerPassword)).perform(typeText("Password123"));
        closeSoftKeyboard();
        onView(withId(R.id.registerConfirmPassword)).perform(typeText("Password123"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description_final)).perform(typeText("Username"));
        closeSoftKeyboard();
        onView(withId(R.id.nameFirst)).perform(typeText("Test"));
        closeSoftKeyboard();
        onView(withId(R.id.location)).perform(typeText("Example"));
        closeSoftKeyboard();
        onView(withId(R.id.cPhoneNumber)).perform(typeText("000000000"));
        closeSoftKeyboard();
    }

    @Test
    public void deleteAllFields()
    {
        onView(withId(R.id.emailReset)).perform(replaceText(""));
        onView(withId(R.id.registerConfirmEmail)).perform(replaceText(""));
        onView(withId(R.id.registerPassword)).perform(replaceText(""));
        onView(withId(R.id.registerConfirmPassword)).perform(replaceText(""));
        onView(withId(R.id.venue_description_final)).perform(replaceText(""));
    }

    @Test
    public void testSwipeLeftOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
    }

    @Test
    public void testRightOnce() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.view_pager)).perform(swipeRight());
    }

    @Test
    public void enterFieldsMusician() throws InterruptedException {
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        onView(withId(R.id.venue_name)).perform(typeText("Example Name"));
        closeSoftKeyboard();
        onView(withId(R.id.venue_description)).perform(typeText("Venue"));
        closeSoftKeyboard();
    }
}