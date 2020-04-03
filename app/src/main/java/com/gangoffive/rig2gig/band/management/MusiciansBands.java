package com.gangoffive.rig2gig.band.management;

public class MusiciansBands
{
    private String reference;
    private String bandName;

    public MusiciansBands(String reference, String bandName)
    {
        this.reference = reference;
        this.bandName = bandName;
    }

    public String getReference()
    {
        return this.reference;
    }

    public String getBandName()
    {
        return bandName;
    }
}
