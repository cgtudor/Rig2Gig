package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.band.management.BandDetailsEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.TabStatePreserver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BandDetailsEditorTest {

    private BandDetailsEditor confirmationClass, mockClass;
    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;
    private FirebaseAuth firebaseAuth;
    private CollectionReference received;
    private Task<DocumentReference> task;
    private HashMap request;
    private HashMap<String, Object> bandData;

    @Before
    public void setUp()
    {
        confirmationClass = new BandDetailsEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void populateInitialFields() {}

            @Override
            public void saveTabs() {}

        };

        received = mock(CollectionReference.class);
        task = mock(Task.class);
        firebaseAuth = mock(FirebaseAuth.class);
        request = mock(HashMap.class);
        mockClass = mock(BandDetailsEditor.class);
        bandData = new HashMap();
        bandData.put("availability","test availability");
        bandData.put("charge","test charge");
        bandData.put("description","test description");
        bandData.put("distance","test distance");
        bandData.put("email","test@email.com");
        bandData.put("genres","test genres");
        bandData.put("location","test location");
        ArrayList<String> members = new ArrayList<>();
        members.add("test member");
        bandData.put("members",members);
        bandData.put("name","test name");
        bandData.put("phone-number","123");
        bandData.put("rating","test rating");
    }

    @Test
    public void testOnSuccessFromDatabaseNoAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(bandData);
        assertThat(confirmationClass.getBand(),is(equalTo(bandData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testBeginTabPreservation()
    {
        TabStatePreserver tabPreserver = mock(TabStatePreserver.class);
        confirmationClass.setTabPreserver(tabPreserver);
        confirmationClass.beginTabPreservation();
        verify(tabPreserver,times(1)).preserveTabState();
    }

    @Test
    public void testvalidateDataMapEmptyField()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setBand(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        listing.put("email", "valid@email.com");
        confirmationClass.setBand(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }
}