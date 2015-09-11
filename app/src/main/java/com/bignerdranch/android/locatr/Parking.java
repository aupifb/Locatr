package com.bignerdranch.android.locatr;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aupifb on 09/09/2015.
 */
public class Parking {
    private String spaceCoordinates;
    private String creatorUser;
    private Double lat, lng;

    public Parking() {

    }

    public Parking(String spaceCoordinates, String creatorUser, Double lat, Double lng) {
        this.spaceCoordinates = spaceCoordinates;
        this.creatorUser = creatorUser;
        this.lat = lat;
        this.lng = lng;
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
