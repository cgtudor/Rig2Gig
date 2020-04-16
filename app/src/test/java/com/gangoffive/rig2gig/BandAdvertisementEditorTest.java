package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.advert.management.BandAdvertisementEditor;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BandAdvertisementEditorTest {

    private BandAdvertisementEditor confirmationClass, mockClass;
    private HashMap<String, Object> bandData, adData;


    @Before
    public void setUp()
    {
        confirmationClass = new BandAdvertisementEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void setInitialColours() {}

            @Override
            public void setupGridView() {}

            @Override
            public void setSearchHintInvisible() {}

            @Override
            public void initialiseSearchBar() {}

            @Override
            public void populateInitialFields() {}

            @Override
            public void saveTabs() {}
        };
        mockClass = mock(BandAdvertisementEditor.class);
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
        adData = new HashMap();
        adData.put("band-ref","test ref");
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
        confirmationClass.onSuccessFromDatabase(bandData);
        assertThat(confirmationClass.getBand(),is(equalTo(bandData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testOnSuccessFromDatabaseWithAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(bandData, adData);
        assertThat(confirmationClass.getBand(),is(equalTo(bandData)));
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
