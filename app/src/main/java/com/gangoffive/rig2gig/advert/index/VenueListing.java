package com.gangoffive.rig2gig.advert.index;

public class VenueListing {

    private String listingRef;
    private String venueRef;

    public VenueListing() {

    }

    public VenueListing(String listingRef, String venueRef) {
        this.listingRef = listingRef;
        this.venueRef = venueRef;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getVenueRef() {
        return venueRef;
    }
}
