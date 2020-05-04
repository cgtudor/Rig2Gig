package com.gangoffive.rig2gig;

import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.account.AccountPurposeActivity;
import com.gangoffive.rig2gig.account.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class AccountTypeUITest
{
    private HashMap<String, Object> performerData, adData;

    @Rule
    public ActivityTestRule<AccountPurposeActivity> testRule
            = new ActivityTestRule<AccountPurposeActivity>(AccountPurposeActivity.class);

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.accountPurpose)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.musicianBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.venueBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testMusicianBtn()
    {
        onView(withId(R.id.musicianBtn)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVenueBtn()
    {
        onView(withId(R.id.venueBtn)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}