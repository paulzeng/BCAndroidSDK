package com.beintoo.wrappers;

import com.google.gson.Gson;

public class MemberSettingsBean {
    private String appsid;
    private String membersid;
    private String lastmodified;
    private Boolean pushsponsoredmissions;
    private Boolean locationbasedmessages; //Gimbal push
    private Boolean purchasemissionsalert;
    private Boolean beclubnews;
    private Boolean userhighlights; //Happy Birthday, Happy Halloween, Planet Day...

    public String getAppsid() {
        return appsid;
    }

    public void setAppsid(String appsid) {
        this.appsid = appsid;
    }

    public String getMembersid() {
        return membersid;
    }

    public void setMembersid(String membersid) {
        this.membersid = membersid;
    }

    public String getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    public Boolean getPushsponsoredmissions() {
        return pushsponsoredmissions;
    }

    public void setPushsponsoredmissions(Boolean pushsponsoredmissions) {
        this.pushsponsoredmissions = pushsponsoredmissions;
    }

    public Boolean getLocationbasedmessages() {
        return locationbasedmessages;
    }

    public void setLocationbasedmessages(Boolean locationbasedmessages) {
        this.locationbasedmessages = locationbasedmessages;
    }

    public Boolean getPurchasemissionsalert() {
        return purchasemissionsalert;
    }

    public void setPurchasemissionsalert(Boolean purchasemissionsalert) {
        this.purchasemissionsalert = purchasemissionsalert;
    }

    public Boolean getBeclubnews() {
        return beclubnews;
    }

    public void setBeclubnews(Boolean beclubnews) {
        this.beclubnews = beclubnews;
    }

    public Boolean getUserhighlights() {
        return userhighlights;
    }

    public void setUserhighlights(Boolean userhighlights) {
        this.userhighlights = userhighlights;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
