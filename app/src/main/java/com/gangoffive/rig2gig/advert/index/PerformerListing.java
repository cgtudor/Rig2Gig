package com.gangoffive.rig2gig.advert.index;

public class PerformerListing {

    private String listingRef;
    private String performerRef;
    private String type;

    public PerformerListing(){

    }

    public PerformerListing(String listingRef, String performerRef, String type) {
        this.listingRef = listingRef;
        this.performerRef = performerRef;
        this.type = type;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getPerformerRef() { return performerRef; }

    public String getType() { return type; }
}
