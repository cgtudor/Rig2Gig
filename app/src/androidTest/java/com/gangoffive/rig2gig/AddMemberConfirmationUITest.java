package com.gangoffive.rig2gig;

import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.AddMemberConfirmation;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AddMemberConfirmationUITest {

    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;


    @Rule
    public ActivityTestRule<AddMemberConfirmation> testRule = new ActivityTestRule<AddMemberConfirmation>(AddMemberConfirmation.class);



    @After
    public void tearDown() throws Exception {
    }

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
                "invite this person to your band?")));
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

    @Test
    public void testInitVariables()
    {
        testRule.getActivity().initVariables();
        expectedPosition = -1;
        assertThat(testRule.getActivity().getPosition(),is(equalTo(expectedPosition)));
        expectedName = null;
        assertThat(testRule.getActivity().getName(),is(equalTo(expectedName)));
        expectedMusicianRef = null;
        assertThat(testRule.getActivity().getMusicianRef(),is(equalTo(expectedMusicianRef)));
        expectedBandRef = null;
        assertThat(testRule.getActivity().getBandRef(),is(equalTo(expectedBandRef)));
        expectedUserRef = null;
        assertThat(testRule.getActivity().getUserRef(),is(equalTo(expectedUserRef)));
        expectedBandName = null;
        assertThat(testRule.getActivity().getBandName(),is(equalTo(expectedBandName)));
        expectedInviterName = null;
        assertThat(testRule.getActivity().getInviterName(),is(equalTo(expectedInviterName)));
        expectedUsersMusicianRef = null;
        assertThat(testRule.getActivity().getUsersMusicianRef(),is(equalTo(expectedUsersMusicianRef)));
        assertThat(testRule.getActivity().getMusicManager(),is(not(equalTo(null))));
        expectedSendingInvite = false;
        assertThat(testRule.getActivity().isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = false;
        assertThat(testRule.getActivity().isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
    }

    @Test
    public void testOnSuccessFromDatabaseUserStillInBand()
    {
        testRule.getActivity().setCheckIfInBand(true);
        Map<String,Object> testMap = new HashMap();
        testRule.getActivity().setBandRef("bandRef");
        testRule.getActivity().setSendingInvite(true);
        List bandRefs = new ArrayList();
        bandRefs.add("bandRef");
        testMap.put("bands",bandRefs);
        testRule.getActivity().onSuccessFromDatabase(testMap);
        assertThat(testRule.getActivity().isCheckIfInBand(),is(equalTo(false)));
        assertThat(testRule.getActivity().isSendingInvite(),is(equalTo(false)));
    }


}