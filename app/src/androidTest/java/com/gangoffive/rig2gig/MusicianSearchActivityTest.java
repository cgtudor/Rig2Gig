package com.gangoffive.rig2gig;

import android.widget.TextView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.MusicianSearchActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class MusicianSearchActivityTest {

    @Rule
    public ActivityTestRule<MusicianSearchActivity> testRule = new ActivityTestRule(MusicianSearchActivity.class);

    @Test
    public void testActivityInView() {
        onView(withId(R.id.searchWindowMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraint)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar_holder)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.list_results)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
    }

    @Test
    public void testTextOfComponents()  {
        onView(allOf(isAssignableFrom(TextView.class), withParent(withResourceName("toolbar")))).check(matches(withText("Invite musicians")));
    }

/*    @Test
    public void testTypeTextInSearchView() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("a"));
        onView(withId(R.id.search_bar)).check(matches(withText("a")));
    }*/
}