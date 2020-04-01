package com.gangoffive.rig2gig;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class LoginActivityUITest
{
    private HashMap<String, Object> performerData, adData;

    @Rule
    public ActivityTestRule<LoginActivity> testRule
            = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.LoginActivity)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.forgotPasswordBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.view2)).check(matches(isDisplayed()));
        onView(withId(R.id.view)).check(matches(isDisplayed()));
        onView(withId(R.id.view3)).check(matches(isDisplayed()));
        onView(withId(R.id.view4)).check(matches(isDisplayed()));
        onView(withId(R.id.registerLoginBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.loginEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.loginPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        onView(withId(R.id.loginRegisterBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.textView4)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
        onView(withId(R.id.fb_loginBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void testTextHintOfComponents()
    {
        onView(withId(R.id.loginEmail)).check(matches(withHint("Email Address")));
        onView(withId(R.id.loginPassword)).check(matches(withHint("Password")));
    }
}