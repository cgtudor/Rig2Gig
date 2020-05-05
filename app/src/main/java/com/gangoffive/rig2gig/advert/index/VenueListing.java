package com.gangoffive.rig2gig.advert.index;

public class VenueListing {

    private String listingRef;
    private String venueRef;

    public VenueListing() {

    }

    /**
     * Constructor to create a venue advert object to hold required values to display advert elsewhere.
     * @param listingRef the associated unique listing reference String for this advert.
     * @param venueRef the unique venue reference String associated to this advert.
     */
    public VenueListing(String listingRef, String venueRef) {
        this.listingRef = listingRef;
        this.venueRef = venueRef;
    }

    /**
     * Accessor
     * @return the associated unique listing reference String for this advert.
     */
    public String getListingRef() {
        return listingRef;
    }

    /**
     * Accessor
     * @return the unique venue reference String associated to this advert.
     */
    public String getVenueRef() {
        return venueRef;
    }
}
