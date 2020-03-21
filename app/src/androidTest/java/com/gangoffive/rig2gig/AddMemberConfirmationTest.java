package com.gangoffive.rig2gig;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class AddMemberConfirmationTest {


    @Rule
    public ActivityTestRule<AddMemberConfirmation> testRule = new ActivityTestRule<AddMemberConfirmation>(AddMemberConfirmation.class);

    @Before
    public void setUp() throws Exception {
        //create activity
        //ActivityScenario activityScenario = ActivityScenario.launch(AddMemberConfirmation.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.mainWindow)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.confirmationText)).check(matches(isDisplayed()));
        onView(withId(R.id.yes)).check(matches(isDisplayed()));
        onView(withId(R.id.no)).check(matches(isDisplayed()));

        //alternatively
        onView(withId(R.id.no)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(withId(R.id.confirmationText)).check(matches(withText("Are you sure you want to invite this person to your band?")));
        onView(withId(R.id.yes)).check(matches(withText("Yes")));
        onView(withId(R.id.no)).check(matches(withText("No")));
    }

    @Test
    public void clickYesNavigation()
    {
        onView(withId(R.id.yes)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void clickNoNavigation()
    {
        onView(withId(R.id.no)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }
}