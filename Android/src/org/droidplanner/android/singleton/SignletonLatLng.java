package org.droidplanner.android.singleton;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.droidplanner.android.activities.helpers.LatLngDatabasesModel;

import java.util.ArrayList;
import java.util.List;

public class SignletonLatLng {
    private static SignletonLatLng projectSingleton = null;
    private Context context;
    private double lat;
    private double lon;
    private String dummyLat;
    private String dummyLon;
    private Activity activity;
    private List<LatLngDatabasesModel> databasesModels = null;

    public String getDummyLat() {
        return dummyLat;
    }

    public void setDummyLat(String dummyLat) {
        this.dummyLat = dummyLat;
    }

    public String getDummyLon() {
        return dummyLon;
    }

    public void setDummyLon(String dummyLan) {
        this.dummyLon = dummyLan;
    }

    public void setLatLngModel(List<LatLngDatabasesModel> databasesModels) {
        this.databasesModels = databasesModels;

    }

    public List<LatLngDatabasesModel> getLatLngModel() {
        return this.databasesModels;
    }


    private SignletonLatLng(Context context) {
        this.context = context;
        lat = 22.823068;
        lon = 89.5525247;
        dummyLat = null;
        dummyLon = null;
    }

    public static SignletonLatLng getInstance(Context context) {
        if (projectSingleton == null) {
            projectSingleton = new SignletonLatLng(context);
        }
        return projectSingleton;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double value) {
        this.lat = value;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double value) {
        this.lon = value;
    }
}

