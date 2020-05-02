package com.gangoffive.rig2gig;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.TextView;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.DeleteMemberConfirmation;
import com.gangoffive.rig2gig.band.management.ManageBandMembersActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManageBandMembersActivityTest
{
    private Map<String,Object> bandData, musician1, musician2, musician3;
    private  Map<String,Object> bigBand;
    private ArrayList<Map<String,Object>> bigBandMembers;
    private IntentsTestRule intentsTestRule = new IntentsTestRule(ManageBandMembersActivity.class);

    @BeforeClass
    public static void setupClass() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
    }

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
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setBandInfoManager(manager);
        Intents.init();
        Matcher<Intent> intent = allOf(
                hasExtra ("EXTRA_CURRENT_MEMBERS",testRule.getActivity().getMemberRefs()),
                hasExtra ("EXTRA_BAND_ID",null),
                hasExtra ("EXTRA_BAND_NAME","test name"),
                hasExtra ("EXTRA_USER_NAME","musician name 0"),
                hasExtra ("EXTRA_USERS_MUSICIAN_ID","test member 0")
        );
        onView(withId(R.id.add_by_name)).perform(click());
        assertTrue(testRule.getActivity().isSearchingByName());
        assertTrue(testRule.getActivity().isCheckIfInBand());
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        intended(intent);
        Intents.release();
        onView(withId(R.id.searchWindowMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchForMembersByEmail()
    {
        setupBand(1);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setBandInfoManager(manager);
        Intents.init();
        Matcher<Intent> intent = allOf(
                hasExtra ("EXTRA_CURRENT_MEMBERS",testRule.getActivity().getMemberRefs()),
                hasExtra ("EXTRA_BAND_ID",null),
                hasExtra ("EXTRA_BAND_NAME","test name"),
                hasExtra ("EXTRA_USER_NAME","musician name 0"),
                hasExtra ("EXTRA_USERS_MUSICIAN_ID","test member 0")
        );
        onView(withId(R.id.add_by_email)).perform(click());
        assertTrue(testRule.getActivity().isSearchingByEmail());
        assertTrue(testRule.getActivity().isCheckIfInBand());
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        intended(intent);
        Intents.release();
        onView(withId(R.id.emailSearchWindowMain)).check(matches(isDisplayed()));
    }

    @Test
    public void testPressPhoneBackButton()
    {
        setupBand(1);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setBandInfoManager(manager);
        pressBack();
        assertTrue(testRule.getActivity().isBackClicked());
        assertTrue(testRule.getActivity().isCheckIfInBand());
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testPressMenuBarBackButton()
    {
        setupBand(1);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setBandInfoManager(manager);
        onView(withContentDescription("Navigate up")).perform(click());
        assertTrue(testRule.getActivity().isBackClicked());
        assertTrue(testRule.getActivity().isCheckIfInBand());
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        assertTrue(testRule.getActivity().isFinishing());
    }

    @Test
    public void testHandleDatabaseResponseSuccess() throws InterruptedException {
        Thread.sleep(2000);
        setupBand(2);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        ListingManager manager = mock(ListingManager.class);
        Query firstQuery = mock(Query.class);
        Query secondQuery = mock(Query.class);
        Task<QuerySnapshot> taskOne = mock(Task.class);
        Task<QuerySnapshot> taskTwo = mock(Task.class);
        when(taskOne.addOnCompleteListener(any())).thenReturn(taskTwo);
        when(secondQuery.get()).thenReturn(taskOne);
        when(firstQuery.whereEqualTo(anyString(), any())).thenReturn(secondQuery);
        CollectionReference joinedBand = mock(CollectionReference.class);
        when(joinedBand.whereEqualTo("type", "accepted-invite")).thenReturn(firstQuery);
        testRule.getActivity().setJoinedBand(joinedBand);
        testRule.getActivity().setBandInfoManager(manager);
        testRule.getActivity().setFirstDeletion(false);
        testRule.getActivity().setRemovedRef("test member 1");
        testRule.getActivity().setPosition(1);
        testRule.getActivity().handleDatabaseResponse(ListingManager.CreationResult.SUCCESS);
        verify(joinedBand,times(1)).whereEqualTo("type", "accepted-invite");
        verify(firstQuery,times(1)).whereEqualTo(anyString(), any());
        verify(secondQuery,times(1)).get();
        verify(taskOne,times(1)).addOnCompleteListener(any());
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
        assertFalse(testRule.getActivity().getNames().contains("test member 1"));
        onView(withText("musician name 1 has been removed"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        assertTrue(testRule.getActivity().getBand() == null);
        assertTrue(testRule.getActivity().getGridView() == null);
        assertTrue(testRule.getActivity().getPosition() == -1);
        assertTrue(testRule.getActivity().getMembersDownloaded() == 0);
        Thread.sleep(2000);

    }

    @Test
    public void testHandleDatabaseResponseFailureScenarioOne() throws InterruptedException {
        Thread.sleep(2000);
        setupBand(2);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        testRule.getActivity().setFirstDeletion(true);
        testRule.getActivity().setRemovedRef("test member 1");
        testRule.getActivity().setPosition(1);
        testRule.getActivity().getMemberRefs().remove(1);
        testRule.getActivity().handleDatabaseResponse(ListingManager.CreationResult.LISTING_FAILURE);
        assertTrue(testRule.getActivity().getMemberRefs().get(1).equals("test member 1"));
        onView(withText("Failed to remove musician name 1 from band.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

    @Test
    public void testHandleDatabaseResponseFailureScenarioTwo() throws InterruptedException {
        Thread.sleep(2000);
        setupBand(2);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        testRule.getActivity().setRemovedRef("test member 1");
        testRule.getActivity().setPosition(1);
        testRule.getActivity().setFirstDeletion(true);
        testRule.getActivity().handleDatabaseResponse(ListingManager.CreationResult.SUCCESS);
        assertTrue(testRule.getActivity().getMemberRefs().get(1).equals("test member 1"));

    }

    @Test
    public void testHandleDatabaseResponseFailureScenarioThree() throws InterruptedException {
        Thread.sleep(2000);
        setupBand(2);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        testRule.getActivity().setFirstDeletion(false);
        testRule.getActivity().setRemovedRef("test member 1");
        testRule.getActivity().setPosition(1);
        testRule.getActivity().getMemberRefs().remove(1);
        testRule.getActivity().handleDatabaseResponse(ListingManager.CreationResult.LISTING_FAILURE);
        assertTrue(testRule.getActivity().getMemberRefs().get(1).equals("test member 1"));
        onView(withText("Failed to remove musician name 1 from band.  Check your connection and try again"))
                .inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

    @Test
    public void testHandleDatabaseResponseSuccessRemovingSelf() throws InterruptedException {
        setupBand(2);
        testRule.getActivity().setuID("test member 0");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        ListingManager manager = mock(ListingManager.class);
        Query firstQuery = mock(Query.class);
        Query secondQuery = mock(Query.class);
        Task<QuerySnapshot> taskOne = mock(Task.class);
        Task<QuerySnapshot> taskTwo = mock(Task.class);
        when(taskOne.addOnCompleteListener(any())).thenReturn(taskTwo);
        when(secondQuery.get()).thenReturn(taskOne);
        when(firstQuery.whereEqualTo(anyString(), any())).thenReturn(secondQuery);
        CollectionReference joinedBand = mock(CollectionReference.class);
        when(joinedBand.whereEqualTo("type", "accepted-invite")).thenReturn(firstQuery);
        testRule.getActivity().setJoinedBand(joinedBand);
        testRule.getActivity().setBandInfoManager(manager);
        testRule.getActivity().setFirstDeletion(false);
        testRule.getActivity().setRemovedRef("test member 0");
        testRule.getActivity().setPosition(0);
        testRule.getActivity().handleDatabaseResponse(ListingManager.CreationResult.SUCCESS);
        verify(joinedBand,times(1)).whereEqualTo("type", "accepted-invite");
        verify(firstQuery,times(1)).whereEqualTo(anyString(), any());
        verify(secondQuery,times(1)).get();
        verify(taskOne,times(1)).addOnCompleteListener(any());
        assertFalse(testRule.getActivity().getNames().contains("test member 0"));
        assertTrue(testRule.getActivity().isFinishing());

    }

    @Test
    public void testAcceptedInvites()
    {
        testRule.getActivity().setRemoveeUserRef("test member 0");
        DocumentSnapshot docSnap = mock(DocumentSnapshot.class);
        when(docSnap.getId()).thenReturn("test doc ref");
        Iterator<DocumentSnapshot> iterator = mock(Iterator.class);
        when(iterator.hasNext()).thenReturn(true,false);
        when(iterator.next()).thenReturn(docSnap);
        List<DocumentSnapshot> docSnaps = mock(List.class);
        when(docSnaps.iterator()).thenReturn(iterator);
        QuerySnapshot querySnap = mock(QuerySnapshot.class);
        when(querySnap.isEmpty()).thenReturn(false);
        Task<QuerySnapshot> task = mock(Task.class);
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(querySnap);
        when(querySnap.getDocuments()).thenReturn(docSnaps);
        testRule.getActivity().getAcceptedInvites().onComplete(task);
        assertTrue(testRule.getActivity().getDocRef().equals("test doc ref"));
        verify(task,times(1)).isSuccessful();
        verify(task,times(2)).getResult();
        verify(querySnap,times(1)).getDocuments();
        verify(docSnap,times(1)).getId();
        verify(iterator,times(2)).hasNext();
        verify(iterator,times(1)).next();
        verify(docSnaps,times(1)).iterator();
        verify(querySnap,times(1)).isEmpty();
    }

    @Test
    public void testUpdateReceiverCommSuccess()
    {
        Task<QuerySnapshot> task = mock(Task.class);
        when(task.isSuccessful()).thenReturn(true);
        testRule.getActivity().getUpdateReceiverComm().onComplete(task);
        verify(task,times(1)).isSuccessful();
    }

    @Test
    public void testOnSuccessfulImageDownloadDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().onSuccessfulImageDownload();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testSetViewReferencesDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().setViewReferences();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testCreateAdvertisementDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().createAdvertisement();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testCancelAdvertisementDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().cancelAdvertisement();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testListingDataMapDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().listingDataMap();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testValidateDataMapDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        assertTrue(testRule.getActivity().validateDataMap() == false);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testOnSuccessFromDatabaseSecondVariantDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        testRule.getActivity().onSuccessFromDatabase(null,null);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }

    @Test
    public void testGetImageViewDoesNothing() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        assertTrue(testRule.getActivity().getImageView() == null);
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar2)).check(matches(isDisplayed()));
        onView(withId(R.id.addImage)).check(matches(isDisplayed()));
        onView(withId(R.id.addMemberText)).check(matches(isDisplayed()));
        onView(withId(R.id.scroll)).check(matches(isDisplayed()));
        onView(withId(R.id.gridView)).check(matches(isDisplayed()));
        onView(withText("musician name 1")).check(matches(isDisplayed()));
        onView(withText("musician name 2")).check(matches(isDisplayed()));
        onView(withText("musician name 3")).check(matches(isDisplayed()));
    }



    @Test
    public void testSwipeToRefresh() {
        testRule.getActivity().onSuccessFromDatabase(bandData);
        testRule.getActivity().onSuccessFromDatabase(musician1);
        testRule.getActivity().onSuccessFromDatabase(musician2);
        testRule.getActivity().onSuccessFromDatabase(musician3);
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setBandInfoManager(manager);
        onView(withId(R.id.swipeContainer)).perform(swipeDown());
        assertTrue(testRule.getActivity().getPosition() == -1);
        assertTrue(testRule.getActivity().getMembersDownloaded() == 0);
        assertTrue(testRule.getActivity().isFirstDeletion() == false);
        assertTrue(testRule.getActivity().isBackClicked() == false);
        assertTrue(testRule.getActivity().isCheckIfInBand() == false);
        assertTrue(testRule.getActivity().isSearchingByName() == false);
        assertTrue(testRule.getActivity().isSearchingByEmail() == false);
        assertTrue(testRule.getActivity().isRemovingMember() == false);
        assertTrue(testRule.getActivity().getBand() == null);
        assertTrue(testRule.getActivity().getSwipeLayout().isRefreshing() == false);
        verify(manager,times(1)).getUserInfo(testRule.getActivity());
    }

    @Test
    public void testEmptyBand() throws InterruptedException {
        setupBand(0);
        testRule.getActivity().setNames(new ArrayList<String>());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        assertFalse(testRule.getActivity().getGridView() == null);
    }

    @Test
    public void testOnActivityResultRemoveMember() throws InterruptedException {
        setupBand(1);
        testRule.getActivity().setuID("test member 1");
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        for (Map<String,Object> musician: bigBandMembers) {
            testRule.getActivity().onSuccessFromDatabase(musician);
        }
        testRule.getActivity().setUsersMusicianRef("test member 0");
        testRule.getActivity().setRemovedRef("test member 0");
        onView(withId(R.id.remove)).perform(click());
        testRule.getActivity().onSuccessFromDatabase(bigBand);
        onView(withId(R.id.yes)).perform(click());
        Thread.sleep(2000);
    }
}