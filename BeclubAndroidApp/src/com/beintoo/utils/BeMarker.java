package com.beintoo.utils;

import com.beintoo.wrappers.PlacesContainerWrapper;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by gbider on 23/05/14.
 */
public class BeMarker {

    private Marker mMarker;
    private PlacesContainerWrapper mPlace;

    public BeMarker(Marker marker, PlacesContainerWrapper place) {
        this.mMarker = marker;
        this.mPlace = place;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public PlacesContainerWrapper getPlace() {
        return mPlace;
    }
}
