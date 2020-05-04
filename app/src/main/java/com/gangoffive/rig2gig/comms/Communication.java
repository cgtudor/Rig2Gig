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

    /**
     * Constructor to create a communication object to hold required values to display communication elsewhere.
     * @param commRef the associated unique communication reference String for this advert.
     * @param userRef the communication's recieving user's unique user reference.
     * @param commType the associated communication type String for this advert.
     * @param sentFromType the entity type String associated to the communication's sender.
     * @param sentFromRef the unique entity reference String associated to the communication's sender.
     * @param sentToType the entity type String associated to the communication's receiver.
     * @param sentToRef the entity type String associated to the communication's receiver.
     */
    public Communication(String commRef, String userRef, String commType, String sentFromType, String sentFromRef, String sentToType, String sentToRef) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;

        this.sentFromType = sentFromType;
        this.sentFromRef = sentFromRef;

        this.sentToType = sentToType;
        this.sentToRef = sentToRef;
    }

    /**
     * Constructor to create a communication object to hold required values to display communication elsewhere.
     * @param commRef the associated unique communication reference String for this advert.
     * @param userRef the communication's recieving user's unique user reference.
     * @param commType the associated communication type String for this advert.
     * @param sentFromType the entity type String associated to the communication's sender.
     * @param sentFromRef the unique entity reference String associated to the communication's sender.
     * @param sentToType the entity type String associated to the communication's receiver.
     * @param sentToRef the entity type String associated to the communication's receiver.
     * @param musicianRef the unique musician reference String associated to the communication.
     */
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

    /**
     *  Accessor
     * @return the associated unique communication reference String for this advert.
     */
    public String getCommRef() {
        return commRef;
    }

    /**
     *  Accessor
     * @return the communication's recieving user's unique user reference.
     */
    public String getUserRef() { return userRef; }

    /**
     *  Accessor
     * @return the communication type String for this advert.
     */
    public String getCommType() { return commType; }

    /**
     *  Accessor
     * @return the entity type String associated to the communication's sender.
     */
    public String getSentFromType() { return sentFromType; }

    /**
     *  Accessor
     * @return the unique entity reference String associated to the communication's sender.
     */
    public String getSentFromRef() { return sentFromRef; }

    /**
     *  Accessor
     * @return the entity type String associated to the communication's receiver.
     */
    public String getSentToType() { return sentToType; }

    /**
     *  Accessor
     * @return the unique entity reference String associated to the communication's receiver.
     */
    public String getSentToRef() { return sentToRef; }

    /**
     *  Accessor
     * @return the unique musician reference String associated to the communication.
     */
    public String getMusicianRef() { return musicianRef; }

    /**
     *  Mutator
     * @param commType the communication type String for this advert.
     */
    public void setCommType(String commType) {
        this.commType = commType;
    }
}
