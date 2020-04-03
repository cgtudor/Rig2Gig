package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.advert.management.MusicianAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.TabStatePreserver;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MusicianAdvertisementEditorTest {

    private MusicianAdvertisementEditor confirmationClass, mockClass;
    private HashMap<String, Object> musicianData, adData;

    @Before
    public void setUp()
    {

        confirmationClass = new MusicianAdvertisementEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void setupGridView() {}

            @Override
            public void initialiseSearchBar() {}

            @Override
            public void populateInitialFields() {}

            @Override
            public void saveTabs() {}
        };


        mockClass = mock(MusicianAdvertisementEditor.class);
        musicianData = new HashMap();
        musicianData.put("genres","test genres");
        musicianData.put("location","test location");
        ArrayList<String> bands = new ArrayList<>();
        bands.add("test band");
        musicianData.put("bands",bands);
        musicianData.put("name","test name");
        musicianData.put("phone-number","123");
        musicianData.put("rating","test rating");
        musicianData.put("user-ref","test id");
        adData = new HashMap();
        adData.put("musician-ref","test ref");
        adData.put("description","test description");
        adData.put("expiry-date","test date");
        ArrayList<String> positions = new ArrayList<>();
        positions.add("Drums");
        positions.add("Clarinet");
        adData.put("position",positions);
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
    public void testOnSuccessFromDatabaseWithAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(musicianData, adData);
        assertThat(confirmationClass.getMusician(),is(equalTo(musicianData)));
        assertThat(confirmationClass.getPreviousListing(),is(equalTo(adData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testOnSuccessfulImageDownload()
    {
        mockClass.onSuccessfulImageDownload();
    }

    @Test
    public void testvalidateDataMapEmptyField()
    {
        ArrayList<String> positions = new ArrayList<>();
        positions.add("Drums");
        confirmationClass.setBandPositions(positions);
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapNullPositions()
    {
        ArrayList<String> positions = null;
        confirmationClass.setBandPositions(positions);
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapEmptyPositions()
    {
        ArrayList<String> positions = new ArrayList<>();
        confirmationClass.setBandPositions(positions);
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }

    @Test
    public void testvalidateDataMapWithValidData()
    {
        ArrayList<String> positions = new ArrayList<>();
        positions.add("Drums");
        confirmationClass.setBandPositions(positions);
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }

    @Test
    public void testBeginTabPreservation()
    {
        TabStatePreserver tabPreserver = mock(TabStatePreserver.class);
        confirmationClass.setTabPreserver(tabPreserver);
        confirmationClass.beginTabPreservation();
    }
}
