package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.TabStatePreserver;
import com.gangoffive.rig2gig.venue.management.VenueDetailsEditor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VenueDetailsEditorTest {

    private VenueDetailsEditor confirmationClass, mockClass;
    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;
    private FirebaseAuth firebaseAuth;
    private CollectionReference received;
    private Task<DocumentReference> task;
    private HashMap request;
    private HashMap<String, Object> venueData;

    @Before
    public void setUp()
    {
        confirmationClass = new VenueDetailsEditor()
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
        mockClass = mock(VenueDetailsEditor.class);
        venueData = new HashMap();
        venueData.put("genres","test genres");
        venueData.put("location","test location");
        venueData.put("name","test name");
        venueData.put("phone-number","123");
        venueData.put("rating","test rating");
        venueData.put("user-ref","test id");
    }

    @Test
    public void testOnSuccessFromDatabaseNoAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(venueData);
        assertThat(confirmationClass.getVenue(),is(equalTo(venueData)));
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
    public void testValidateDataMapEmptyField()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setVenue(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testValidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        listing.put("email-address", "valid@email.com");
        confirmationClass.setVenue(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }
}