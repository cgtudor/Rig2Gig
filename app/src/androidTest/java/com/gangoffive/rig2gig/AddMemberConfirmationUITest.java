package com.gangoffive.rig2gig;

import android.os.Looper;

import androidx.test.rule.ActivityTestRule;

import com.gangoffive.rig2gig.band.management.AddMemberConfirmation;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddMemberConfirmationUITest {

    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;
    private AddMemberConfirmation confirmationClass, mockClass;
    private FirebaseAuth firebaseAuth;
    private CollectionReference received;
    private Task< DocumentReference > task;
    private HashMap request;

    @Before
    public void setUp()
    {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
        confirmationClass = new AddMemberConfirmation()
        {    @Override
        public String getUserId()
        {
            return("testUserId");
        }};
        received = mock(CollectionReference.class);
        task = mock(Task.class);
        firebaseAuth = mock(FirebaseAuth.class);
        request = mock(HashMap.class);
        mockClass = mock(AddMemberConfirmation.class);
    }

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

    
    //new tests
    @Test
    public void testInitialValues() throws InterruptedException {

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
        expectedMusicManager = null;
        assertThat(testRule.getActivity().getMusicManager(),is(not(equalTo(expectedMusicManager))));
        expectedSendingInvite = false;
        assertThat(testRule.getActivity().isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = false;
        assertThat(testRule.getActivity().isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
    }


    @Test
    public void testCheckIfInBand()
    {
        ListingManager manager = mock(ListingManager.class);
        testRule.getActivity().setMusicManager(manager);
        testRule.getActivity().checkIfInBand();
        expectedSendingInvite = false;
        assertThat(testRule.getActivity().isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = true;
        assertThat(testRule.getActivity().isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
        verify(manager,times(1)).getUserInfo(any());
    }

    @Test
    public void testSendInvite()
    {
        when(mockClass.generateInvite()).thenReturn(new HashMap<String, Object>());
        when(request.put(eq("sent-from"),any())).thenReturn(true);
        when(task.isSuccessful()).thenReturn(true);
        when(received.add(any(HashMap.class))).thenReturn(task);
        mockClass.sendInvite();
    }

    @Test
    public void testGenerateInvite()
    {
        testRule.getActivity().setBandRef("testBandRef");
        testRule.getActivity().setMusicianRef("testMusicianRef");
        testRule.getActivity().setInviterName("testInviterName");
        testRule.getActivity().setBandName("testBandName");
        HashMap actual = testRule.getActivity().generateInvite();
        assertThat(actual.get("type"),is(equalTo("join-request")));
        assertThat(actual.get("band-ref"),is(equalTo("testBandRef")));
        assertThat(actual.get("musician-ref"),is(equalTo("testMusicianRef")));
        assertThat(actual.get("notification-title"),is(equalTo("You have been invited to join a band!")));
        assertThat(actual.get("notification-message"),
                is(equalTo(testRule.getActivity().getInviterName() +
                        " would like you to join their band " +
                        testRule.getActivity().getBandName() + ".")));
    }

    @Test
    public void testGenerateLoggedInvite()
    {
        testRule.getActivity().setBandRef("testBandRef");
        testRule.getActivity().setMusicianRef("testMusicianRef");
        testRule.getActivity().setInviterName("testInviterName");
        testRule.getActivity().setBandName("testBandName");
        HashMap actual = testRule.getActivity().generateLoggedInvite();
        assertThat(actual.get("type"),is(equalTo("join-request")));
        assertThat(actual.get("sent-from-ref"),is(equalTo("testBandRef")));
        assertThat(actual.get("sent-to-ref"),is(equalTo("testMusicianRef")));
        assertThat(actual.get("notification-title"),is(equalTo("You have been invited to join a band!")));
        assertThat(actual.get("notification-message"),
                is(equalTo(testRule.getActivity().getInviterName() +
                        " would like you to join their band " +
                        testRule.getActivity().getBandName() + ".")));
    }


    @Test
    public void testOnSuccessFromDatabaseUserStillInBandSecondaryTest()
    {
        FirebaseFirestore db = mock(FirebaseFirestore.class);
        testRule.getActivity().setDb(db);
        when(db.collection(any())).thenReturn(mock(CollectionReference.class));
        when(db.collection(any()).document(any())).thenReturn(mock(DocumentReference.class));
        when(db.collection(any()).document(any()).collection(any())).thenReturn(mock(CollectionReference.class));
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