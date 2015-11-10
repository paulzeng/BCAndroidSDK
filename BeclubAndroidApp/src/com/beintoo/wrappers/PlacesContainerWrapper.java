package com.beintoo.wrappers;


import java.util.List;

public class PlacesContainerWrapper  {
    private PlaceWrapper places;
    private List<BrandsContainer> brands;
    private Float distance; // internal
    private boolean isEnabled = true; // internal

    public PlaceWrapper getPlaces() {
        return places;
    }

    public void setPlaces(PlaceWrapper places) {
        this.places = places;
    }

    public List<BrandsContainer> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandsContainer> brands) {
        this.brands = brands;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
