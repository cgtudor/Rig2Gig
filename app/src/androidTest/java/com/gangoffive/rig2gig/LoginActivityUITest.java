package com.gangoffive.rig2gig;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.account.LoginActivity;

import org.junit.Before;
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

    @Test
    public void enterTestData()
    {
        onView(withId(R.id.loginEmail)).perform(typeText("Test@Test.com"));
        closeSoftKeyboard();
        onView(withId(R.id.loginPassword)).perform(typeText("Password123"));
        closeSoftKeyboard();
    }

    @Test
    public void deleteAllFields()
    {
        onView(withId(R.id.loginEmail)).perform(replaceText(""));
        onView(withId(R.id.loginPassword)).perform(replaceText(""));
    }

    @Test
    public void login()
    {
        onView(withId(R.id.loginEmail)).perform(typeText("test@test.com"));
        closeSoftKeyboard();
        onView(withId(R.id.loginPassword)).perform(typeText("Password123"));
        closeSoftKeyboard();
        onView(withId(R.id.registerLoginBtn)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void invalidLogin()
    {
        onView(withId(R.id.loginEmail)).perform(typeText("invalid@test.com"));
        closeSoftKeyboard();
        onView(withId(R.id.loginPassword)).perform(typeText("Password123"));
        closeSoftKeyboard();
        onView(withId(R.id.registerLoginBtn)).perform(click());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}