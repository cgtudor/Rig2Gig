package com.gangoffive.rig2gig;


import com.gangoffive.rig2gig.band.management.AddMemberConfirmation;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AddMemberConfirmationTest {

    private AddMemberConfirmation confirmationClass, mockClass;
    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;
    private FirebaseAuth firebaseAuth;
    private CollectionReference received;
    private Task< DocumentReference > task;
    private HashMap request;

    @Before
    public void setUp()
    {
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


    @Test
    public void testInitialValues() throws InterruptedException {

        expectedPosition = 0;
        assertThat(confirmationClass.getPosition(),is(equalTo(expectedPosition)));
        expectedName = null;
        assertThat(confirmationClass.getName(),is(equalTo(expectedName)));
        expectedMusicianRef = null;
        assertThat(confirmationClass.getMusicianRef(),is(equalTo(expectedMusicianRef)));
        expectedBandRef = null;
        assertThat(confirmationClass.getBandRef(),is(equalTo(expectedBandRef)));
        expectedUserRef = null;
        assertThat(confirmationClass.getUserRef(),is(equalTo(expectedUserRef)));
        expectedBandName = null;
        assertThat(confirmationClass.getBandName(),is(equalTo(expectedBandName)));
        expectedInviterName = null;
        assertThat(confirmationClass.getInviterName(),is(equalTo(expectedInviterName)));
        expectedUsersMusicianRef = null;
        assertThat(confirmationClass.getUsersMusicianRef(),is(equalTo(expectedUsersMusicianRef)));
        expectedMusicManager = null;
        assertThat(confirmationClass.getMusicManager(),is(equalTo(expectedMusicManager)));
        expectedSendingInvite = false;
        assertThat(confirmationClass.isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = false;
        assertThat(confirmationClass.isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
        assertThat(confirmationClass.getMusicManager(),is(equalTo(null)));
    }

    @Test
    public void testBeginSendInvite()
    {
        confirmationClass.beginSendInvite();
        expectedSendingInvite = true;
        assertThat(confirmationClass.isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = true;
        assertThat(confirmationClass.isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
    }

    @Test
    public void testCheckIfInBand()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setMusicManager(manager);
        confirmationClass.checkIfInBand();
        expectedSendingInvite = false;
        assertThat(confirmationClass.isSendingInvite(),is(equalTo(expectedSendingInvite)));
        expectedCheckIfInBand = true;
        assertThat(confirmationClass.isCheckIfInBand(),is(equalTo(expectedCheckIfInBand)));
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
        confirmationClass.setBandRef("testBandRef");
        confirmationClass.setMusicianRef("testMusicianRef");
        confirmationClass.setInviterName("testInviterName");
        confirmationClass.setBandName("testBandName");
        HashMap actual = confirmationClass.generateInvite();
        assertThat(actual.get("type"),is(equalTo("join-request")));
        assertThat(actual.get("band-ref"),is(equalTo("testBandRef")));
        assertThat(actual.get("musician-ref"),is(equalTo("testMusicianRef")));
        assertThat(actual.get("notification-title"),is(equalTo("You have been invited to join a band!")));
        assertThat(actual.get("notification-message"),
                is(equalTo(confirmationClass.getInviterName() +
                        " would like you to join their band " +
                        confirmationClass.getBandName() + ".")));
    }

    @Test
    public void testGenerateLoggedInvite()
    {
        confirmationClass.setBandRef("testBandRef");
        confirmationClass.setMusicianRef("testMusicianRef");
        confirmationClass.setInviterName("testInviterName");
        confirmationClass.setBandName("testBandName");
        HashMap actual = confirmationClass.generateLoggedInvite();
        assertThat(actual.get("type"),is(equalTo("join-request")));
        assertThat(actual.get("sent-from-ref"),is(equalTo("testBandRef")));
        assertThat(actual.get("sent-to-ref"),is(equalTo("testMusicianRef")));
        assertThat(actual.get("notification-title"),is(equalTo("You have been invited to join a band!")));
        assertThat(actual.get("notification-message"),
                is(equalTo(confirmationClass.getInviterName() +
                        " would like you to join their band " +
                        confirmationClass.getBandName() + ".")));
    }


    @Test
    public void testOnSuccessFromDatabaseUserStillInBand()
    {
        FirebaseFirestore db = mock(FirebaseFirestore.class);
        confirmationClass.setDb(db);
        when(db.collection(any())).thenReturn(mock(CollectionReference.class));
        when(db.collection(any()).document(any())).thenReturn(mock(DocumentReference.class));
        when(db.collection(any()).document(any()).collection(any())).thenReturn(mock(CollectionReference.class));
        confirmationClass.setCheckIfInBand(true);
        Map<String,Object> testMap = new HashMap();
        confirmationClass.setBandRef("bandRef");
        confirmationClass.setSendingInvite(true);
        List bandRefs = new ArrayList();
        bandRefs.add("bandRef");
        testMap.put("bands",bandRefs);
        confirmationClass.onSuccessFromDatabase(testMap);
        assertThat(confirmationClass.isCheckIfInBand(),is(equalTo(false)));
        assertThat(confirmationClass.isSendingInvite(),is(equalTo(false)));
    }

}