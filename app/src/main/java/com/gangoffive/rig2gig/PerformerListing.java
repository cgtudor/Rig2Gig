package com.gangoffive.rig2gig;

public class PerformerListing {

    private String listingRef;
    private String name;
    private String genres;
    private String location;

    public PerformerListing(){

    }

    public PerformerListing(String listingRef, String name, String genres, String location) {
        this.listingRef = listingRef;
        this.name = name;
        this.genres = genres;
        this.location = location;
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
}
