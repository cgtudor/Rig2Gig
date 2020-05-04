package com.gangoffive.rig2gig.advert.index;

public class BandListing {

    private String listingRef;
    private String bandRef;

    public BandListing() {

    }

    /**
     *  Constructor to create a band advert object to hold required values to display advert elsewhere.
     * @param listingRef the associated unique listing reference String for this advert.
     * @param bandRef the unique band reference String associated to this advert.
     */
    public BandListing(String listingRef, String bandRef) {
        this.listingRef = listingRef;
        this.bandRef = bandRef;
    }

    /**
     *  Accessor
     * @return the associated unique listing reference String for this advert.
     */
    public String getListingRef() {
        return listingRef;
    }

    /**
     *  Accessor
     * @return the unique band reference String associated to this advert.
     */
    public String getBandRef() {
        return bandRef;
    }
}
