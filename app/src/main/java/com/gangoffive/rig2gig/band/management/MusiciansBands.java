package com.gangoffive.rig2gig.band.management;

/**
 * This class is used to make objects of Bands that a musician is in.
 * @author Ben souch
 * @version #0.3b
 * @since #0.2b
 */
public class MusiciansBands
{
    private String reference;
    private String bandName;

    /**
     * Constructor used to set up the reference and bandName variables.
     * @param reference References the Firebase UID of a band.
     * @param bandName References the name of the band the musician is in.
     * @since #0.2b
     */
    public MusiciansBands(String reference, String bandName)
    {
        if(reference == null || reference.equals("") || bandName == null || bandName.equals(""))
        {
            throw new IllegalArgumentException("Please ensure reference and/or band name is not empty or null");
        }

        this.reference = reference;
        this.bandName = bandName;
    }

    /**
     * This method is used to get the reference of the band the musician is in.
     * @return Returns the reference of the band the musician is in as a String.
     * @since #0.2b
     */
    public String getReference()
    {
        return this.reference;
    }

    /**
     * This method is used to get the name of the band the musician is in.
     * @return Returns the name of the band that the musician is in as a String.
     * @since #0.2b
     */
    public String getBandName()
    {
        return bandName;
    }
}
