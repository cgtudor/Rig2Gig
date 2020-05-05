package com.gangoffive.rig2gig.advert.index;

import java.util.ArrayList;

public class MusicianListing {

    private String listingRef;
    private String musicianRef;
    private ArrayList<String> position;

    public MusicianListing() {

    }

    /**
     * Constructor to create a Musician advert object to hold required values to display advert elsewhere.
     * @param listingRef the associated unique listing reference String for this advert.
     * @param musicianRef the unique musician reference String associated to this advert.
     * @param position an arraylist of strings containing the positions the musician is looking to fill in a band.
     */
    public MusicianListing(String listingRef, String musicianRef, ArrayList<String> position) {
        this.listingRef = listingRef;
        this.musicianRef = musicianRef;
        this.position = position;
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
     * @return the unique musician reference String associated to this advert.
     */
    public String getMusicianRef() {
        return musicianRef;
    }

    /**
     *  Accessor
     * @return an arraylist of strings containing the positions the musician is looking to fill in a band.
     */
    public ArrayList<String> getPosition() {
        return position;
    }
}
