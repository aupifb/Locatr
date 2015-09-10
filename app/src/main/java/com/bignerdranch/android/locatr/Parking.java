package com.bignerdranch.android.locatr;

/**
 * Created by aupifb on 09/09/2015.
 */
public class Parking {
    private String spaceCoordinates;
    private String creatorUser;

    public Parking() {

    }

    public Parking(String spaceCoordinates, String creatorUser) {
        this.spaceCoordinates = spaceCoordinates;
        this.creatorUser = creatorUser;
    }

    public String getSpaceCoordinates() {
        return spaceCoordinates;
    }

    public void setSpaceCoordinates(String spaceCoordinates) {
        this.spaceCoordinates = spaceCoordinates;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }
}
