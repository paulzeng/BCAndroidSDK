package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 23/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class PictureAcceptMissionWrapper {

    private String postid;
    private String pictureurl;
    private String fbaccesstoken;

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
