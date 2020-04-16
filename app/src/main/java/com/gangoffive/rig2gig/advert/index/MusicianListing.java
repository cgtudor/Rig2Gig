package com.gangoffive.rig2gig.advert.index;

import java.util.ArrayList;

public class MusicianListing {

    private String listingRef;
    private String musicianRef;
    private ArrayList<String> position;

    public MusicianListing() {

    }

    public MusicianListing(String listingRef, String musicianRef, ArrayList<String> position) {
        this.listingRef = listingRef;
        this.musicianRef = musicianRef;
        this.position = position;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getMusicianRef() {
        return musicianRef;
    }

    public ArrayList<String> getPosition() {
        return position;
    }
}
