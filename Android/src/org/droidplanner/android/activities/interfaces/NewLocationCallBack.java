package org.droidplanner.android.activities.interfaces;

import org.droidplanner.android.singleton.SignletonLatLng;

public interface NewLocationCallBack {
    void onNewLocation(SignletonLatLng location);
}
