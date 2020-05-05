package com.gangoffive.rig2gig.advert.index;

public class PerformerListing {

    private String listingRef;
    private String performerRef;
    private String type;

    public PerformerListing(){

    }

    /**
     * Constructor to create a performer advert object to hold required values to display advert elsewhere.
     * @param listingRef the associated unique listing reference String for this advert.
     * @param performerRef the unique musician or band reference String associated to this advert.
     * @param type a String indicating if the advert is for a musician or a band.
     */
    public PerformerListing(String listingRef, String performerRef, String type) {
        this.listingRef = listingRef;
        this.performerRef = performerRef;
        this.type = type;
    }

    /**
     * Accessor
     * @return the associated unique listing reference String for this advert.
     */
    public String getListingRef() {
        return listingRef;
    }

    /**
     *  Accessor
     * @return the unique musician or band reference String associated to this advert.
     */
    public String getPerformerRef() { return performerRef; }

    /**
     *  Accessor
     * @return a String indicating if the advert is for a musician or a band.
     */
    public String getType() { return type; }
}
