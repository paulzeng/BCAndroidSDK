package com.beintoo.utils;

import java.util.Locale;

public class UnitLocale {
    public static UnitLocale Imperial = new UnitLocale();
    public static UnitLocale Metric = new UnitLocale();

    static final double MILES_PER_KILOMETER = 0.621;

    public static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }
    public static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry();
        if ("US".equals(countryCode)) return Imperial; // USA
        if ("LR".equals(countryCode)) return Imperial; // liberia
        if ("MM".equals(countryCode)) return Imperial; // burma
        if ("UK".equals(countryCode)) return Imperial; // UK
        return Metric;
    }

    public static Double metersToFeet(Float meters){
        return (meters*3.2808*10000)/10000.0;
    }

    public static Double metersToMiles(Float meters){
        return roundToTwoDecimals((meters / 1000) * MILES_PER_KILOMETER);
    }

    public static Double metersToKilometers(Float meters){
        return roundToTwoDecimals(meters/1000.0);
    }

    public static Double roundToTwoDecimals(Double input){
        return Math.round(input * 100.0) / 100.0;
    }
}