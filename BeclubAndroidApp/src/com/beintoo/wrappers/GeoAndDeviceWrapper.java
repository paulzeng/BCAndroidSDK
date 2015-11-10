package com.beintoo.wrappers;

import android.content.Context;
import android.location.Location;

import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RootChecker;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

import java.util.List;

public class GeoAndDeviceWrapper {
    // login information
    private String email;
    private String password;
    // gps information
    private Double latitude;
    private Double longitude;
    private Double haccuracy;
    private Double vaccuracy;
    private Double altitude;
    // device information
    private String locale;
    private String idfa;
    private Boolean idfaenabled;
    private String androidid;
    private String udid;
    private String imei;
    private String macaddress;
    private String devicetype; // IOS, ANDROID
    private String matid;
    // push notification token
    private String pushplatform = "GCM";
    private String pushtoken;
    // opted-out from push in this app?
    private Boolean optedoutpush;
    // gimbal placeID
    private String fencename;
    private List<String> subtypes;
    // picture mission
    private String postid;
    private String fbaccesstoken;
    private String pictureurl;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getFbaccesstoken() {
        return fbaccesstoken;
    }

    public void setFbaccesstoken(String fbaccesstoken) {
        this.fbaccesstoken = fbaccesstoken;
    }

    public String getPictureurl() {
        return pictureurl;
    }

    public void setPictureurl(String pictureurl) {
        this.pictureurl = pictureurl;
    }

    private String googleadvid;
    private Boolean googleadvidenabled;
    private Boolean deviceunlocked;

    public String getGoogleadvid() {
        return googleadvid;
    }

    public void setGoogleadvid(String googleadvid) {
        this.googleadvid = googleadvid;
    }

    public Boolean getGoogleadvidenabled() {
        return googleadvidenabled;
    }

    public void setGoogleadvidenabled(Boolean googleadvidenabled) {
        this.googleadvidenabled = googleadvidenabled;
    }

    public Boolean getDeviceunlocked() {
        return deviceunlocked;
    }

    public void setDeviceunlocked(Boolean deviceunlocked) {
        this.deviceunlocked = deviceunlocked;
    }

    public List<String> getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(List<String> subtypes) {
        this.subtypes = subtypes;
    }

    public String getFencename() {
        return fencename;
    }

    public void setFencename(String fencename) {
        this.fencename = fencename;
    }

    public String getMatid() {
        return matid;
    }

    public void setMatid(String matid) {
        this.matid = matid;
    }

    public Double getHaccuracy() {
        return haccuracy;
    }

    public void setHaccuracy(Double haccuracy) {
        this.haccuracy = haccuracy;
    }

    public Double getVaccuracy() {
        return vaccuracy;
    }

    public void setVaccuracy(Double vaccuracy) {
        this.vaccuracy = vaccuracy;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public Boolean getIdfaenabled() {
        return idfaenabled;
    }

    public void setIdfaenabled(Boolean idfaenabled) {
        this.idfaenabled = idfaenabled;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getPushplatform() {
        return pushplatform;
    }

    public void setPushplatform(String pushplatform) {
        this.pushplatform = pushplatform;
    }

    public String getPushtoken() {
        return pushtoken;
    }

    public void setPushtoken(String pushtoken) {
        this.pushtoken = pushtoken;
    }

    public Boolean getOptedoutpush() {
        return optedoutpush;
    }

    public void setOptedoutpush(Boolean optedoutpush) {
        this.optedoutpush = optedoutpush;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static GeoAndDeviceWrapper build(Context context, Location location) {
        GeoAndDeviceWrapper geoAndDevice = new GeoAndDeviceWrapper();
        try {
            Location loc;
            if (location != null) {
                loc = location;
            } else {
                loc = BeLocationManager.getInstance(context).getLastKnowLocation(context);
            }

            if (loc != null) {
                geoAndDevice.setLatitude(loc.getLatitude());
                geoAndDevice.setLongitude(loc.getLongitude());
                geoAndDevice.setAltitude(loc.getAltitude());
                geoAndDevice.setHaccuracy((double) loc.getAccuracy());
            }
            geoAndDevice.setAndroidid(DeviceId.getAndroidId(context));
            geoAndDevice.setImei(DeviceId.getImei(context));
            geoAndDevice.setMacaddress(DeviceId.getMACAddress(context));
            geoAndDevice.setMatid(MobileAppTracker.getInstance().getMatId());
            geoAndDevice.setGoogleadvid(MemberAuthStore.advertiseID);
            geoAndDevice.setGoogleadvidenabled(MemberAuthStore.advertiseDisable);
            geoAndDevice.setDeviceunlocked(RootChecker.isRooted());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return geoAndDevice;
    }
}
