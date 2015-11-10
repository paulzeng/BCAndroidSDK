package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 22/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class PictureAssignedMission {

    private String bememberId;
    private String uniqueId;
    private String missionsid;
    private Double bedollars;
    private String status;
    private String creationdate;
    private String appsid;
    private String postid;
    private String pictureurl;
    private String fbaccesstoken;

    public String getBememberId() {
        return bememberId;
    }

    public void setBememberId(String bememberId) {
        this.bememberId = bememberId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMissionsid() {
        return missionsid;
    }

    public void setMissionsid(String missionsid) {
        this.missionsid = missionsid;
    }

    public Double getBedollars() {
        return bedollars;
    }

    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String creationdate) {
        this.creationdate = creationdate;
    }

    public String getAppsid() {
        return appsid;
    }

    public void setAppsid(String appsid) {
        this.appsid = appsid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    public String getFbaccesstoken() {
        return fbaccesstoken;
    }

    public void setFbaccesstoken(String fbaccesstoken) {
        this.fbaccesstoken = fbaccesstoken;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
