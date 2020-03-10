package com.gangoffive.rig2gig;

public class Communication {

    private String commRef;
    private String userRef;
    private String commType;

    public Communication() {

    }

    public Communication(String commRef, String userRef, String commType) {
        this.commRef = commRef;
        this.userRef = userRef;
        this.commType = commType;
    }

    public String getCommRef() {
        return commRef;
    }

    public String getUserRef() { return userRef; }

    public String getCommType() { return commType; }

    public void setCommType(String commType) {
        this.commType = commType;
    }
}
