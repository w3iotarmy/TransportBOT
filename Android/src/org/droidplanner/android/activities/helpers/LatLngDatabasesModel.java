package org.droidplanner.android.activities.helpers;

import java.util.ArrayList;

public class LatLngDatabasesModel {
    private double lat;
    private double lng;
    private int alt;

    public LatLngDatabasesModel(double lat, double lng, int alt) {
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
    }

    public void setAlt(int alt) {
        this.alt = alt;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getAlt() {
        return alt;
    }
}
