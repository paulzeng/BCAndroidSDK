package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 26/08/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class BePoweredInfoWrapper {
    private String urischemaios;
    private String urischemaandroid;
    private String title;
    private String description;
    private String imgurlsmall;
    private String imgurlbig;

    public String getUrischemaios() {
        return urischemaios;
    }

    public void setUrischemaios(String urischemaios) {
        this.urischemaios = urischemaios;
    }

    public String getUrischemaandroid() {
        return urischemaandroid;
    }

    public void setUrischemaandroid(String urischemaandroid) {
        this.urischemaandroid = urischemaandroid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgurlsmall() {
        return imgurlsmall;
    }

    public void setImgurlsmall(String imgurlsmall) {
        this.imgurlsmall = imgurlsmall;
    }

    public String getImgurlbig() {
        return imgurlbig;
    }

    public void setImgurlbig(String imgurlbig) {
        this.imgurlbig = imgurlbig;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
