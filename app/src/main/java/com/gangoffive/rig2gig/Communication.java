package com.gangoffive.rig2gig;

public class Communication {

    private String commRef;
    private String userRef;
    private String commType;
    private String bandRef;
    private String musicianRef;

    public Communication() {

    }

    public Communication(String commRef, String userRef, String commType) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;
    }

    public Communication(String commRef, String userRef, String commType, String bandRef, String musicianRef) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;
        this.bandRef = bandRef;
        this.musicianRef = musicianRef;
    }

    public String getCommRef() {
        return commRef;
    }

    public String getUserRef() { return userRef; }

    public String getCommType() { return commType; }

    public String getBandRef() { return bandRef; }

    public String getMusicianRef() { return musicianRef; }

    public void setCommType(String commType) {
        this.commType = commType;
    }
}
