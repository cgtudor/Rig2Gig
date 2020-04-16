package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.musician.management.MusicianDetailsEditor;
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

public class MusicianDetailsEditorTest
{
    private MusicianDetailsEditor confirmationClass, mockClass;
    private int expectedPosition;
    private String expectedName, expectedMusicianRef, expectedBandRef, expectedUserRef,
            expectedBandName, expectedInviterName, expectedUsersMusicianRef;
    private boolean expectedSendingInvite, expectedCheckIfInBand;
    private ListingManager expectedMusicManager;
    private FirebaseAuth firebaseAuth;
    private CollectionReference received;
    private Task<DocumentReference> task;
    private HashMap request;
    private HashMap<String, Object> musicianData;

    @Before
    public void setUp()
    {
        confirmationClass = new MusicianDetailsEditor()
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
        mockClass = mock(MusicianDetailsEditor.class);
        musicianData = new HashMap();
        musicianData.put("availability","test availability");
        musicianData.put("charge","test charge");
        musicianData.put("description","test description");
        musicianData.put("distance","test distance");
        musicianData.put("email","test@email.com");
        musicianData.put("genres","test genres");
        musicianData.put("location","test location");
        ArrayList<String> bands = new ArrayList<>();
        bands.add("test band");
        musicianData.put("members",bands);
        musicianData.put("name","test name");
        musicianData.put("phone-number","123");
        musicianData.put("rating","test rating");
    }

    @Test
    public void testOnSuccessFromDatabaseNoAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(musicianData);
        assertThat(confirmationClass.getMusician(),is(equalTo(musicianData)));
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
        confirmationClass.setMusician(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        confirmationClass.setMusician(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }
}