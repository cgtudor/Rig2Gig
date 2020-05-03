package com.gangoffive.rig2gig;

import android.app.Instrumentation;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.TextView;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.MusicianSearchActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static junit.framework.TestCase.assertTrue;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MusicianSearchActivityTest {

    private ArrayList<String> musicians, currentMemberRefs, searchRefs, searchNames, names,
            gridRefs, userRefs, bands, genres, noBands;
    private HashMap<String,Object> currentMember, searched1, searched2;
    private ListingManager mockManager;

    @Rule
    public ActivityTestRule<MusicianSearchActivity> testRule = new ActivityTestRule(MusicianSearchActivity.class);

    public void setupCurrentMemberMap()
    {
        currentMember = new HashMap<>();
        currentMember.put("distance", "0");
        currentMember.put("latitude","10");
        currentMember.put("longitude","6");
        genres = new ArrayList<>();
        genres.add("test genre");
        currentMember.put("genres", genres);
        currentMember.put("location", "test location");
        bands = new ArrayList<>();
        bands.add("test band");
        currentMember.put("bands", bands);
        currentMember.put("name", "searched name");
        currentMember.put("rating", "test rating");
        currentMember.put("user-ref", "current member user ref 1");
    }

    public void setupCurrentMember()
    {
        mockManager = mock(ListingManager.class);
        testRule.getActivity().getMusicianManagers().add(mockManager);
        setupCurrentMemberMap();
        gridRefs = new ArrayList<>();
        gridRefs.add("current member ref 1");
        musicians = new ArrayList<>();
        musicians.add("searched name");
        currentMemberRefs = new ArrayList<>();
        currentMemberRefs.add("current member ref 1");
        currentMemberRefs.add("current member ref 2");
        searchRefs = new ArrayList<>();
        searchRefs.add("current member ref 1");
        names = new ArrayList<>();
        testRule.getActivity().setNames(names);
        searchNames = new ArrayList<>();
        searchNames.add("searched name");
        testRule.getActivity().setSearchNames(searchNames);
        setArrays();
    }

    public void setupOneSearchResult()
    {
        if(mockManager == null)
        {
            mockManager = mock(ListingManager.class);
        }
        if(musicians == null)
        {
            musicians = new ArrayList<>();
            musicians.add("searched name");
        }
        if(currentMemberRefs == null)
        {
            currentMemberRefs = new ArrayList<>();
            currentMemberRefs.add("current member ref 1");
            currentMemberRefs.add("current member ref 2");
        }
        if (searchRefs == null)
        {
            searchRefs = new ArrayList<>();
        }
        if(names == null)
        {
            names = new ArrayList<>();
        }
        if (searchNames == null)
        {
            searchNames = new ArrayList<>();
        }
        searchNames.add("searched name");
        searchRefs.add("searched musician 1 ref");
        testRule.getActivity().getMusicianManagers().add(mockManager);
        searched1 = new HashMap<>();
        searched1.put("distance", "0");
        searched1.put("latitude","10");
        searched1.put("longitude","6");
        searched1.put("genres", genres);
        searched1.put("location", "test location");
        if (noBands == null)
        {
            noBands = new ArrayList<>();
            searched1.put("bands", noBands);
        }
        searched1.put("name", "searched name");
        searched1.put("rating", "test rating");
        searched1.put("user-ref", "searched musician 1 user ref");
        if (gridRefs == null)
        {
            gridRefs = new ArrayList<>();
        }
        gridRefs.add("searched musician 1 ref");
        setArrays();
    }

    public void setupThreeMemberHashMaps()
    {
        setupCurrentMember();
        setupOneSearchResult();
        searched2 = new HashMap<>();
        searched2.put("distance", "0");
        searched2.put("latitude","10");
        searched2.put("longitude","6");
        searched2.put("genres", genres);
        searched2.put("location", "test location");
        searched2.put("bands", noBands);
        searched2.put("name", "searched name");
        searched2.put("rating", "test rating");
        searched2.put("user-ref", "searched musician 2 user ref");
    }

    public void setupThreeSearchResults()
    {
        setupThreeMemberHashMaps();
        testRule.getActivity().getMusicianManagers().add(mockManager);
        searchRefs.add("searched musician 2 ref");
        searchNames.add("searched name");
        gridRefs.add("searched musician 2 ref");
        setArrays();
    }

    public void setArrays()
    {
        testRule.getActivity().setMusicians(musicians);
        testRule.getActivity().setCurrentMemberRefs(currentMemberRefs);
        testRule.getActivity().setSearchRefs(searchRefs);
        testRule.getActivity().setSearchNames(searchNames);
        testRule.getActivity().setGridRefs(gridRefs);
        userRefs = new ArrayList<>();
        userRefs.add("searched ref");
        testRule.getActivity().setUserRefs(userRefs);
    }

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

    @Test
    public void testTypeText() throws Throwable {
        runOnUiThread(new Runnable() {
            public void run() {
                testRule.getActivity().getSearchBar().setQuery("test",true);
            }
        });
        assertTrue(testRule.getActivity().getMembersDownloaded() == 0);
        assertTrue(testRule.getActivity().getInvitesChecked() == 0);
        assertTrue(testRule.getActivity().getMusicians().isEmpty());
        assertTrue(testRule.getActivity().getSearchRefs().isEmpty());
        assertTrue(testRule.getActivity().getSearchNames().isEmpty());
        assertTrue(testRule.getActivity().getNames().isEmpty());
        assertTrue(testRule.getActivity().getMusicianManagers().isEmpty());
        assertTrue(testRule.getActivity().getGridRefs().isEmpty());
        assertTrue(testRule.getActivity().getUserRefs().isEmpty());
        assertTrue(testRule.getActivity().getInvitesSent().isEmpty());
    }

    @Test
    public void testTypeNothing() throws Throwable {
        runOnUiThread(new Runnable() {
            public void run() {
                testRule.getActivity().getSearchBar().setQuery("",true);
            }
        });
        assertTrue(testRule.getActivity().getListResults().getAdapter() == null);
    }

    @Test
    public void testBeginResultsGeneration()
    {
        testRule.getActivity().beginResultsGeneration("test name");
        assertTrue(testRule.getActivity().getResultsName().equals("test name"));
        assertTrue(testRule.getActivity().isGeneratingResults() == true);
        assertTrue(testRule.getActivity().isCheckIfInBand() == true);
    }

    @Test
    public void testGenerateSearchResults() {
        setupThreeSearchResults();
        testRule.getActivity().getUserRefs().remove(0);
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        assertTrue(testRule.getActivity().getNames().size() == 3);
        assertTrue(testRule.getActivity().getNames().get(0).equals("searched name"));
        assertTrue(testRule.getActivity().getNames().get(1).equals("searched name"));
        assertTrue(testRule.getActivity().getNames().get(2).equals("searched name"));
        assertTrue(testRule.getActivity().getUserRefs().size() == 3);
        assertTrue(testRule.getActivity().getUserRefs().contains("current member user ref 1"));
        assertTrue(testRule.getActivity().getUserRefs().contains("searched musician 1 user ref"));
        assertTrue(testRule.getActivity().getUserRefs().contains("searched musician 2 user ref"));
        assertTrue(testRule.getActivity().getMembersDownloaded() == 2);
    }

    @Test
    public void  testNavigationPressPhoneBackButtonStillInBand(){
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("test band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        pressBack();
        Assert.assertTrue(testRule.getActivity().isBackClicked());
        Assert.assertTrue(testRule.getActivity().isCheckIfInBand());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        Assert.assertTrue(testRule.getActivity().isFinishing());
        onView(withId(R.id.manageMembersMain)).check(matches(isDisplayed()));
    }

    @Test
    public void  testIntentPressPhoneBackButtonStillInBand(){
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("test band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasExtra("EXTRA_BAND_ID","test band"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        pressBack();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void testNavigationPressMenuBarBackButtonStillInBand()
    {
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("test band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        onView(withContentDescription("Navigate up")).perform(click());
        Assert.assertTrue(testRule.getActivity().isBackClicked());
        Assert.assertTrue(testRule.getActivity().isCheckIfInBand());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        Assert.assertTrue(testRule.getActivity().isFinishing());
        onView(withId(R.id.manageMembersMain)).check(matches(isDisplayed()));
    }

    @Test
    public void  testIntentPressMenuBarBackButtonStillInBand() throws InterruptedException {
        Thread.sleep(2000);
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("test band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(hasExtra("EXTRA_BAND_ID","test band"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withContentDescription("Navigate up")).perform(click());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void  testNavigationPressPhoneBackButtonNotInBand(){
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("not in band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        pressBack();
        Assert.assertTrue(testRule.getActivity().isBackClicked());
        Assert.assertTrue(testRule.getActivity().isCheckIfInBand());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        Assert.assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void  testIntentPressPhoneBackButtonNotInBand(){
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("not in band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(toPackage("com.gangoffive.rig2gig"),
                hasComponent("com.gangoffive.rig2gig.navbar.NavBarActivity"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        pressBack();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void testNavigationPressMenuBarBackButtonNotInBand() throws InterruptedException {
        Thread.sleep(2000);
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("not in band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        onView(withContentDescription("Navigate up")).perform(click());
        Assert.assertTrue(testRule.getActivity().isBackClicked());
        Assert.assertTrue(testRule.getActivity().isCheckIfInBand());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        Assert.assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void  testIntentPressMenuBarBackButtonNotInBand(){
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        testRule.getActivity().setBandRef("not in band");
        closeSoftKeyboard();
        setupThreeSearchResults();
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        testRule.getActivity().onSuccessFromDatabase(searched1);
        testRule.getActivity().onSuccessFromDatabase(searched2);
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(toPackage("com.gangoffive.rig2gig"),
                hasComponent("com.gangoffive.rig2gig.navbar.NavBarActivity"));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withContentDescription("Navigate up")).perform(click());
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void testInviteMemberPreChecksAndIntentContent() throws InterruptedException {
        setupOneSearchResult();
        testRule.getActivity().onSuccessFromDatabase(searched1);
        Thread.sleep(2000);
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        onView(withId(R.id.invite)).perform(click());
        assertTrue(testRule.getActivity().isInvitingMember() == true);
        assertTrue(testRule.getActivity().getConfirmMember().equals("searched name"));
        assertTrue(testRule.getActivity().getConfirmPosition() == 0);
        assertTrue(testRule.getActivity().isConfirmingAdd() == true);
        assertTrue(testRule.getActivity().isCheckIfInBand() == true);
        verify(mockManager,times(1)).getUserInfo(testRule.getActivity());
        setupCurrentMember();
        testRule.getActivity().setBandRef("test band");
        testRule.getActivity().setUserName("user name");
        testRule.getActivity().setBandName("band name");
        testRule.getActivity().setUsersMusicianRef("user musician ref");
        Intents.init();
        Matcher<Intent> expectedIntent = allOf(
                toPackage("com.gangoffive.rig2gig"),
                hasComponent("com.gangoffive.rig2gig.band.management.AddMemberConfirmation"),
                hasExtra("EXTRA_NAME","searched name"),
                hasExtra("EXTRA_POSITION",0),
                hasExtra("EXTRA_MUSICIAN_ID","current member ref 1"),
                hasExtra("EXTRA_BAND_ID","test band"),
                hasExtra("EXTRA_USER_ID","searched ref"),
                hasExtra("EXTRA_INVITER_NAME","user name"),
                hasExtra("EXTRA_BAND_NAME","band name"),
                hasExtra("EXTRA_USER_MUSICIAN_REF","user musician ref")
                );
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        intended(expectedIntent);
        Intents.release();
    }

    @Test
    public void testInviteMemberPopupDisplayCheckAndClickNo() throws InterruptedException {
        setupOneSearchResult();
        testRule.getActivity().onSuccessFromDatabase(searched1);
        Thread.sleep(2000);
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        onView(withId(R.id.invite)).perform(click());
        setupCurrentMember();
        testRule.getActivity().setBandRef("test band");
        testRule.getActivity().setUserName("user name");
        testRule.getActivity().setBandName("band name");
        testRule.getActivity().setUsersMusicianRef("user musician ref");
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));
        Thread.sleep(2000);
        onView(withId(R.id.no)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.searchWindowMain)).check(matches(isDisplayed()));
        onView(withText("searched name")).check(matches(isDisplayed()));
    }

    @Test
    public void testInviteMemberPopupDisplayCheckAndClickYes() throws InterruptedException {
        setupOneSearchResult();
        testRule.getActivity().onSuccessFromDatabase(searched1);
        Thread.sleep(2000);
        mockManager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(mockManager);
        onView(withId(R.id.invite)).perform(click());
        setupCurrentMember();
        testRule.getActivity().setBandRef("test band");
        testRule.getActivity().setUserName("user name");
        testRule.getActivity().setBandName("band name");
        testRule.getActivity().setUsersMusicianRef("user musician ref");
        testRule.getActivity().onSuccessFromDatabase(currentMember);
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));
        onView(withId(R.id.yes)).perform(click());
        onView(withId(R.id.confirmationMainWindow)).check(matches(isDisplayed()));
    }
}