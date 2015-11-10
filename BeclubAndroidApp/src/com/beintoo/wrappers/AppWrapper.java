package com.beintoo.wrappers;

import com.google.gson.Gson;

public class AppWrapper {
    private String id;
    private String name;
    private AppVersionsWrapper customdata;
    private BePoweredInfoWrapper bepoweredinfo;
    private String spreedlyBasePath;
    private int maxCardsPerUser;

    public String getSpreedlyBasePath() {
        return spreedlyBasePath;
    }

    public void setSpreedlyBasePath(String spreedlyBasePath) {
        this.spreedlyBasePath = spreedlyBasePath;
    }

    public int getMaxCardsPerUser() {
        return maxCardsPerUser;
    }

    public void setMaxCardsPerUser(int maxCardsPerUser) {
        this.maxCardsPerUser = maxCardsPerUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppVersionsWrapper getCustomdata() {
        return customdata;
    }

    public void setCustomdata(AppVersionsWrapper customdata) {
        this.customdata = customdata;
    }

    public BePoweredInfoWrapper getBepoweredinfo() {
        return bepoweredinfo;
    }

    public void setBepoweredinfo(BePoweredInfoWrapper bepoweredinfo) {
        this.bepoweredinfo = bepoweredinfo;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
