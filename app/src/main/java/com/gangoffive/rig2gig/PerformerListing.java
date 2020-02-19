package com.gangoffive.rig2gig;

public class PerformerListing {

    private String bandRef;
    private String chargePerHour;
    private String description;
    private String distance;
    private String genres;
    private String location;
    private String name;

    public PerformerListing(){

    }

    public PerformerListing(String bandRef, String chargePerHour, String description, String distance, String genres, String location, String name) {
        this.bandRef = bandRef;
        this.chargePerHour = chargePerHour;
        this.description = description;
        this.distance = distance;
        this.genres = genres;
        this.location = location;
        this.name = name;
    }

    public String getBandRef() {
        return bandRef;
    }

    public String getChargePerHour() {
        return chargePerHour;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
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
