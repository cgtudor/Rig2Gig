package com.gangoffive.rig2gig;

import android.app.Instrumentation;
import android.content.Intent;
import android.widget.TextView;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.ManageBandMembersActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

public class ManageBandMembersActivityTest
{
    private Map<String,Object> bandData, musician1, musician2, musician3;
    private  Map<String,Object> bigBand;
    private ArrayList<Map<String,Object>> bigBandMembers;

    @Before
    public void setup()
    {
        bandData = new HashMap<>();
        bandData.put("availability", "test availability");
        bandData.put("charge", "test charge");
        bandData.put("description", "test description");
        bandData.put("distance", "test distance");
        bandData.put("email", "test@email.com");
        bandData.put("latitude","10");
        bandData.put("longitude","6");
        bandData.put("genres", "test genres");
        bandData.put("location", "test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member1");
        members.add("test member2");
        members.add("test member3");
        bandData.put("members", members);
        bandData.put("name", "test name");
        bandData.put("phone-number", "123");
        bandData.put("rating", "test rating");
        musician1 = new HashMap<>();
        musician1.put("name", "musician name 1");
        musician1.put("distance", "musician distance 1");
        musician1.put("genres", "musician genres 1");
        musician1.put("phone-number", "musician number 1");
        musician1.put("user-ref", "");
        musician1.put("rating", "musician rating 1");
        musician1.put("location", "musician location 1");
        musician2 = new HashMap<>();
        musician2.put("name", "musician name 2");
        musician2.put("distance", "musician distance 2");
        musician2.put("genres", "musician genres 2");
        musician2.put("phone-number", "musician number 2");
        musician2.put("user-ref", "");
        musician2.put("rating", "musician rating 2");
        musician2.put("location", "musician location 2");
        musician3 = new HashMap<>();
        musician3.put("name", "musician name 3");
        musician3.put("distance", "musician distance 3");
        musician3.put("genres", "musician genres 3");
        musician3.put("phone-number", "musician number 3");
        musician3.put("user-ref", "");
        musician3.put("rating", "musician rating 3");
        musician3.put("location", "musician location 3");
    }

    @Rule
    public ActivityTestRule<ManageBandMembersActivity> testRule = new ActivityTestRule<ManageBandMembersActivity>(ManageBandMembersActivity.class);

    public void setupBand(int size)
    {
        bigBand = new HashMap<>();
        bigBand.put("availability", "test availability");
        bigBand.put("charge", "test charge");
        bigBand.put("description", "test description");
        bigBand.put("distance", "test distance");
        bigBand.put("email", "test@email.com");
        bigBand.put("latitude","10");
        bigBand.put("longitude","6");
        bigBand.put("genres", "test genres");
        bigBand.put("location", "test location");
        bigBand.put("name", "test name");
        bigBand.put("phone-number", "123");
        bigBand.put("rating", "test rating");
        ArrayList<String> members = new ArrayList<>();
        bigBandMembers = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            members.add("test member " + i);
            HashMap<String,Object> musician = new HashMap<>();
            musician.put("name", "musician name " + i);
            musician.put("distance", "musician distance " + i);
            musician.put("genres", "musician genres " + i);
            musician.put("phone-number", "musician number " + i);
            musician.put("user-ref", "test member " + i);
            musician.put("rating", "musician rating " + i);
            musician.put("location", "musician location " + i);
            bigBandMembers.add(musician);
        }
        bigBand.put("members",members);
    }

    @Test
    public void testActivityInView()
    {
        onView(withId(R.id.manageMembersMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testComponentVisibility()
    {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
    }

    @Test
    public void testTextOfComponents()
    {
        onView(allOf(isAssignableFrom(TextView.class), withParent(withResourceName("toolbar")))).check(matches(withText("Manage your band")));
        onView(withId(R.id.addMemberText)).check(matches(withText("  Add members by:")));
    }

    @Test
    public void testOnSuccessFromDatabaseOneMusician() throws InterruptedException {
        ArrayList<String> members = new ArrayList<>();
        members.add("test member1");
        bandData.put("members", members);
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        onView(withId(R.id.name)).check(matches(isDisplayed()));
        onView(withId(R.id.linearCardLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.musicianCard)).check(matches(isDisplayed()));
        onView(withId(R.id.relativeLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.bandMemberImage)).check(matches(isDisplayed()));
        onView(withId(R.id.remove)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSuccessFromDatabaseMultipleMusicians() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testClickMusician() {
        setupBand(50);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(withText("musician name 50")).check(doesNotExist());
        onView(withText("musician name 1")).check(matches(isDisplayed()));
    }

    @Test
    public void testClick()
    {
        setupBand(5);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(withText("musician name 1")).perform(click());
        onView(withId(R.id.bandMemberDetails)).check(matches(isDisplayed()));
    }

    @Test
    public void testScroll()
    {
        setupBand(15);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(ViewMatchers.withId(R.id.scroll)).perform(swipeUp());
        onView(withText("musician name 14")).check(matches(isDisplayed()));
    }

    @Test
    public void testRemove()
    {
        setupBand(1);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(withText("Remove")).perform(click());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));

    }

    @Test
    public void testRemoveIntent()
    {
        setupBand(1);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(withText("Remove")).perform(click());
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasExtra("EXTRA_NAME","musician name 0"),hasExtra("EXTRA_POSITION",0));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        intended(expectedIntent);
        Intents.release();


    }

    @Test
    public void testRemoveCompletely() throws InterruptedException {
        setupBand(1);
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        onView(withText("Remove")).perform(click());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));
        onView(withId(R.id.yes)).perform(click());
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testSearchForMembersByName()
    {
        setupBand(1);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        onView(withId(R.id.add_by_name)).perform(click());
        assertTrue(testRule.getActivity().isCheckIfInBand());
    }
}