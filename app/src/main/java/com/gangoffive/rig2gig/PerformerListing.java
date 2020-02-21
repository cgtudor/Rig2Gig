package com.gangoffive.rig2gig;

public class PerformerListing {

    private String listingRef;
    private String name;
    private String genres;
    private String location;
    private String bandRef;

    public PerformerListing(){

    }

    public PerformerListing(String listingRef, String name, String genres, String location, String bandRef) {
        this.listingRef = listingRef;
        this.name = name;
        this.genres = genres;
        this.location = location;
        this.bandRef = bandRef;
    }

    public String getGenres() {
        return genres;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getBandRef() {
        return bandRef;
    }
}
