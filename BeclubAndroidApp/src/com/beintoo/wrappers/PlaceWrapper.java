package com.beintoo.wrappers;


import android.graphics.Bitmap;

public class PlaceWrapper {
    private String id;

    private String name;
    private String description;
    private String siteurl;
    private String imageurl;
    private String phone;
    private String address; //street + numero
    private String city;
    private String state;
    private String country;
    private String provider;
    private String provideruid;
    private Double latitude;
    private Double longitude;
    private String creationdate;
    private Integer timezone;
    private String timing;
    private Double radius;
    private String imagemarker;
    private Bitmap imageMarkerBitmap;

    public Bitmap getImageMarkerBitmap() {
        return imageMarkerBitmap;
    }

    public void setImageMarkerBitmap(Bitmap imageMarkerBitmap) {
        this.imageMarkerBitmap = imageMarkerBitmap;
    }

    public String getImagemarker() {
        return imagemarker;
    }

    public void setImagemarker(String imagemarker) {
        this.imagemarker = imagemarker;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public void setSiteurl(String siteurl) {
        this.siteurl = siteurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProvideruid() {
        return provideruid;
    }

    public void setProvideruid(String provideruid) {
        this.provideruid = provideruid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
