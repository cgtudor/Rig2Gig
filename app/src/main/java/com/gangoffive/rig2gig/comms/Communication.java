package com.gangoffive.rig2gig.comms;

public class Communication {

    private String commRef;
    private String userRef;
    private String commType;

    private String sentFromType;
    private String sentFromRef;

    private String sentToType;
    private String sentToRef;

    private String musicianRef;

    public Communication() {

    }

    public Communication(String commRef, String userRef, String commType, String sentFromType, String sentFromRef, String sentToType, String sentToRef) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;

        this.sentFromType = sentFromType;
        this.sentFromRef = sentFromRef;

        this.sentToType = sentToType;
        this.sentToRef = sentToRef;
    }

    public Communication(String commRef, String userRef, String commType, String sentFromType, String sentFromRef, String sentToType, String sentToRef, String musicianRef) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;

        this.sentFromType = sentFromType;
        this.sentFromRef = sentFromRef;

        this.sentToType = sentToType;
        this.sentToRef = sentToRef;

        this.musicianRef = musicianRef;
    }

    public String getCommRef() {
        return commRef;
    }

    public String getUserRef() { return userRef; }

    public String getCommType() { return commType; }

    public String getSentFromType() { return sentFromType; }

    public String getSentFromRef() { return sentFromRef; }

    public String getSentToType() { return sentToType; }

    public String getSentToRef() { return sentToRef; }

    public String getMusicianRef() { return musicianRef; }

    public void setCommType(String commType) {
        this.commType = commType;
    }
}
