package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 25/08/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class DynamicTextBean {
    private String pushcode;
    private String title;
    private String message;
    private String dynamicCode;

    public String getDynamicCode() {
        return dynamicCode;
    }

    public void setDynamicCode(String dynamicCode) {
        this.dynamicCode = dynamicCode;
    }

    public String getPushcode() {
        return pushcode;
    }

    public void setPushcode(String pushcode) {
        this.pushcode = pushcode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
