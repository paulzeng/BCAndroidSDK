package com.beintoo.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Giulio Bider on 23/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class LocationUtils {

    public static Map<String, Object> getLocationParams(Context context) {
        HashMap<String, Object> queryParams = new HashMap<String, Object>();

        Location location = null;
        try {
            location = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation("network");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(location == null) {
            return null;
        }

        queryParams.put("latitude", "40.760995");
        queryParams.put("longitude", "-73.975906");
        queryParams.put("alt", location.getAltitude());
        queryParams.put("ha", location.getAccuracy());

        return queryParams;

    }
}
