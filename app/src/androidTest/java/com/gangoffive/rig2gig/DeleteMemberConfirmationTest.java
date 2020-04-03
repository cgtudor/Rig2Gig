package com.gangoffive.rig2gig;

import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.DeleteMemberConfirmation;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class DeleteMemberConfirmationTest {
    @Rule
    public ActivityTestRule<DeleteMemberConfirmation> testRule =
            new ActivityTestRule<DeleteMemberConfirmation>(DeleteMemberConfirmation.class);

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.confirmationText)).check(matches(isDisplayed()));
        onView(withId(R.id.yes)).check(matches(isDisplayed()));
        onView(withId(R.id.no)).check(matches(isDisplayed()));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(withId(R.id.confirmationText)).check(matches(withText("Are you sure you want to " +
                "remove this person from your band?")));
        onView(withId(R.id.yes)).check(matches(withText("Yes")));
        onView(withId(R.id.no)).check(matches(withText("No")));
    }

    @Test
    public void testClickNo()
    {
        onView(withId(R.id.no)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testClickYesNavigation()
    {
        onView(withId(R.id.yes)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }
}