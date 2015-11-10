package com.beintoo.wrappers;

import com.google.gson.Gson;

public class EventNotificationWrapper {

    private String type;
    private String entityid;
    private String title;
    private String message;
    private String pushcode;
    private String eventtype;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
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

    public String getPushcode() {
        return pushcode;
    }

    public void setPushcode(String pushcode) {
        this.pushcode = pushcode;
    }

    public String getEventtype() {
        return eventtype;
    }

    public void setEventtype(String eventtype) {
        this.eventtype = eventtype;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
