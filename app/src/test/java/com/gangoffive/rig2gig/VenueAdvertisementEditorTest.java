package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.advert.management.VenueAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class VenueAdvertisementEditorTest {

    private VenueAdvertisementEditor confirmationClass, mockClass;
    private HashMap<String, Object> venueData, adData;

    @Before
    public void setUp() throws Exception {
        confirmationClass = new VenueAdvertisementEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void setInitialColours(){}

            @Override
            public void populateInitialFields() {}

        };
        mockClass = mock(VenueAdvertisementEditor.class);
        venueData = new HashMap();
        venueData.put("genres","test genres");
        venueData.put("location","test location");
        venueData.put("name","test name");
        venueData.put("phone-number","123");
        venueData.put("rating","test rating");
        venueData.put("user-ref","test id");
        adData = new HashMap();
        adData.put("performer-ref","test ref");
        adData.put("performer-type","test type");
        adData.put("expiry-date","test date");
        adData.put("distance","test distance");
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
    public void testOnSuccessFromDatabaseWithAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(venueData, adData);
        assertThat(confirmationClass.getVenue(),is(equalTo(venueData)));
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
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(false)));
    }


    @Test
    public void testvalidateDataMapWithValidData()
    {
        HashMap<String, Object> listing = new HashMap();
        listing.put("valid field", "valid");
        listing.put("empty field", "also valid");
        confirmationClass.setListing(listing);
        assertThat(confirmationClass.validateDataMap(),is(equalTo(true)));
    }

    @Test
    public void testBeginTabPreservation()
    {

    }
}