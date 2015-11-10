package com.beintoo.wrappers;


import java.util.List;

public class BrandWrapper {
    private String id;
    private String name;
    private String description;
    private String imageicon;
    private String imagebig;
    private String imagesmall;
    private String sponsorurl;
    private Double balance;
    private boolean onlyonline;
    private List<EarnBedollarsWrapper.MissionActionTypeEnum> sponsoredactiontype;

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

    public String getImagebig() {
        return imagebig;
    }

    public void setImagebig(String imagebig) {
        this.imagebig = imagebig;
    }

    public String getImagesmall() {
        return imagesmall;
    }

    public void setImagesmall(String imagesmall) {
        this.imagesmall = imagesmall;
    }

    public String getImageicon() {
        return imageicon;
    }

    public void setImageicon(String imageicon) {
        this.imageicon = imageicon;
    }

    public String getSponsorurl() {
        return sponsorurl;
    }

    public void setSponsorurl(String sponsorurl) {
        this.sponsorurl = sponsorurl;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isOnlyonline() {
        return onlyonline;
    }

    public void setOnlyonline(boolean onlyonline) {
        this.onlyonline = onlyonline;
    }

    public List<EarnBedollarsWrapper.MissionActionTypeEnum> getSponsoredactiontype() {
        return sponsoredactiontype;
    }

    public void setSponsoredactiontype(List<EarnBedollarsWrapper.MissionActionTypeEnum> sponsoredactiontype) {
        this.sponsoredactiontype = sponsoredactiontype;
    }
}
