package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.Set;

/**
 * Created by Giulio Bider on 25/08/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class DynamicDictionaryBean {

    private String version;
    private Set<DynamicTextBean> payload;
    private long creationDate;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<DynamicTextBean> getPayload() {
        return payload;
    }

    public void setPayload(Set<DynamicTextBean> payload) {
        this.payload = payload;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public DynamicTextBean getTextFromPushCode(String pushCode) {
        for(DynamicTextBean bean : payload) {
            if(bean.getPushcode().equals(pushCode)) {
                return bean;
            }
        }
        return null;
    }
}
