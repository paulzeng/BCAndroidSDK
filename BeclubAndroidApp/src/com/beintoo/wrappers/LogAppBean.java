package com.beintoo.wrappers;

import com.google.gson.Gson;

public class LogAppBean {

    private String message;
    private String statusCode;
    private String stackTrace;
    private String eventType;
    private String memberId;
    private GeoAndDeviceWrapper geoAndDeviceWrapper;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public GeoAndDeviceWrapper getGeoAndDeviceWrapper() {
        return geoAndDeviceWrapper;
    }

    public void setGeoAndDeviceWrapper(GeoAndDeviceWrapper geoAndDeviceWrapper) {
        this.geoAndDeviceWrapper = geoAndDeviceWrapper;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the stackTrace
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * @param stackTrace the stackTrace to set
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
