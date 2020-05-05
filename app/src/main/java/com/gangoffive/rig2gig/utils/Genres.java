package com.gangoffive.rig2gig.utils;

/**
 * A list of genres used by the pop out window for selecting a genre as a musician account creation
 */
public class Genres {

    private final static String [] genres = new String[]
    {
            "Blues",
            "Classical",
            "Country",
            "Dance",
            "Folk",
            "Hip Hop",
            "Indie",
            "Jazz",
            "Metal",
            "Pop",
            "Punk",
            "R&B",
            "Rap",
            "Reggae",
            "Rock",
            "Soul"
    };

    /**
     * @return genres
     */
    static public String [] getGenres()
    {
        return genres;
    }
}
