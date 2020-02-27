package com.gangoffive.rig2gig;

public class MusicianListing {

    private String listingRef;
    private String musicianRef;
    private String position;

    public MusicianListing() {

    }

    public MusicianListing(String listingRef, String musicianRef, String position) {
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

    public String getPosition() {
        return position;
    }
}
