package com.gangoffive.rig2gig;

import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;
import com.gangoffive.rig2gig.firebase.ListingManager;

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

public class PerformerAdvertisementEditorTest {

    private PerformerAdvertisementEditor confirmationClass, mockClass;
    private HashMap<String, Object> performerData, adData;

    @Before
    public void setUp() throws Exception {
        confirmationClass = new PerformerAdvertisementEditor()
        {
            @Override
            public void setViewReferences() {}

            @Override
            public void populateInitialFields() {}

        };
        mockClass = mock(PerformerAdvertisementEditor.class);
        performerData = new HashMap();
        performerData.put("genres","test genres");
        performerData.put("location","test location");
        ArrayList<String> bands = new ArrayList<>();
        bands.add("test band");
        performerData.put("bands",bands);
        performerData.put("name","test name");
        performerData.put("phone-number","123");
        performerData.put("rating","test rating");
        performerData.put("user-ref","test id");
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
        confirmationClass.onSuccessFromDatabase(performerData);
        assertThat(confirmationClass.getPerformer(),is(equalTo(performerData)));
        verify(manager,times(1)).getImage(any());
    }

    @Test
    public void testOnSuccessFromDatabaseWithAdvert()
    {
        ListingManager manager = mock(ListingManager.class);
        confirmationClass.setListingManager(manager);
        confirmationClass.onSuccessFromDatabase(performerData, adData);
        assertThat(confirmationClass.getPerformer(),is(equalTo(performerData)));
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