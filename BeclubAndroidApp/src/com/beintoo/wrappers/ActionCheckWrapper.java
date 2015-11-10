package com.beintoo.wrappers;


public class ActionCheckWrapper {
    private EarnBedollarsWrapper.MissionActionTypeEnum subtype;
    private String scannumber;
    private Integer multiplenumber;
    private Double latitude;
    private Double longitude;
    // only for buy, like x B for every y $
    private String currency;
    private String bedollars;
    private String cash;
    private String url;
    private TimingWrapper timing;
    private Integer radius;
    private ProductWrapper product;

    public EarnBedollarsWrapper.MissionActionTypeEnum getSubtype() {
        return subtype;
    }

    public void setSubtype(EarnBedollarsWrapper.MissionActionTypeEnum subtype) {
        this.subtype = subtype;
    }

    public String getScannumber() {
        return scannumber;
    }

    public void setScannumber(String scannumber) {
        this.scannumber = scannumber;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBedollars() {
        return bedollars;
    }

    public void setBedollars(String bedollars) {
        this.bedollars = bedollars;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public Integer getMultiplenumber() {
        return multiplenumber;
    }

    public void setMultiplenumber(Integer multiplenumber) {
        this.multiplenumber = multiplenumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TimingWrapper getTiming() {
        return timing;
    }

    public void setTiming(TimingWrapper timing) {
        this.timing = timing;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public ProductWrapper getProduct() {
        return product;
    }

    public void setProduct(ProductWrapper product) {
        this.product = product;
    }
}
