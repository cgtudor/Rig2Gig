package com.gangoffive.rig2gig;

public class PerformerListing {

    private String listingRef;
    private String performerRef;
    private String type;
    private String name;
    private String genres;
    private String location;
    private String rating;

    public PerformerListing(){

    }

    public PerformerListing(String listingRef, String performerRef, String type, String name, String genres, String location, String rating) {
        this.listingRef = listingRef;
        this.performerRef = performerRef;
        this.type = type;
        this.name = name;
        this.genres = genres;
        this.location = location;
        this.rating = rating;
    }

    public PerformerListing(String listingRef, String bandRef, String type) {
        this.listingRef = listingRef;
        this.performerRef = bandRef;
        this.type = type;
    }

    public String getListingRef() {
        return listingRef;
    }

    public String getPerformerRef() { return performerRef; }

    public String getType() { return type; }

    public String getGenres() {
        return genres;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public void setName(String name) {
        this.name = name;
    }
}
