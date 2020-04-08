package com.gangoffive.rig2gig.utils;

public class VenueTypes {
    private final static String[] types = new String[]
            {
                    "Bar",
                    "Club",
                    "Function Room",
                    "Hotel",
                    "Pub"
            };

    static public String[] getTypes() {
        return types;
    }
}
