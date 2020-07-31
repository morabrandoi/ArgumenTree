package com.example.argumentree.models;

public class Vote {
    private String responseRef;
    private String userRef;
    private String direction;

    public Vote() {
    }

    public Vote(String responseRef, String userRef, String direction) {
        this.responseRef = responseRef;
        this.userRef = userRef;
        this.direction = direction;
    }

    public String getResponseRef() {
        return responseRef;
    }

    public void setResponseRef(String responseRef) {
        this.responseRef = responseRef;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
