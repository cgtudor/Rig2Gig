package com.gangoffive.rig2gig.advert.index;

public class BandListing {

    private String listingRef;
    private String bandRef;

    public BandListing() {

    }

    public BandListing(String listingRef, String bandRef) {
        this.listingRef = listingRef;
        this.bandRef = bandRef;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getBandRef() {
        return bandRef;
    }
}
