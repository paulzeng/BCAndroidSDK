package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.List;

public class MemberBean extends ResponseWrapper {

    public final static String defaultImage = "https://s3-eu-west-1.amazonaws.com/static.beintoo.com/beclub-app/default-profile-picture/xhdpi.jpg";

    private String id;
    private String name;
    private String email;
    private String address;
    private String gender;
    private String phone;
    private String imagesmall = defaultImage;
    private String imagebig = defaultImage;
    private Double bedollars;
    private String country;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Float haccuracy;
    private String locale;
    private String meta;
    private String dob;
    private Long dobUnix;
    private String surname;
    private Boolean emailverified = false;
    private Integer regularrewardsnumber;
    private String fbtoken;
    private AuthBean auth;
    private String androidid;
    private String udid;
    private String imei;
    private String macaddress;
    private String devicetype;
    private String password;
    private String matid;
    private List<MemberSettingsBean> memberssettings;
    private Boolean cardspringactivated;
    private Boolean hascctokens;
    private Boolean hasfbprovider;

    public Boolean getHasfbprovider() {
        return hasfbprovider;
    }

    public void setHasfbprovider(Boolean hasfbprovider) {
        this.hasfbprovider = hasfbprovider;
    }

    public Boolean getHascctokens() {
        return hascctokens;
    }

    public void setHascctokens(Boolean hascctokens) {
        this.hascctokens = hascctokens;
    }

    public Boolean getCardspringactivated() {
        return cardspringactivated;
    }

    public void setCardspringactivated(Boolean cardspringactivated) {
        this.cardspringactivated = cardspringactivated;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getHaccuracy() {
        return haccuracy;
    }

    public void setHaccuracy(Float haccuracy) {
        this.haccuracy = haccuracy;
    }

    public String getMatid() {
        return matid;
    }

    public void setMatid(String matid) {
        this.matid = matid;
    }

    public List<MemberSettingsBean> getMemberssettings() {
        return memberssettings;
    }

    public void setMemberssettings(List<MemberSettingsBean> memberssettings) {
        this.memberssettings = memberssettings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImagesmall() {
        return imagesmall;
    }

    public void setImagesmall(String imagesmall) {
        this.imagesmall = imagesmall;
    }

    public String getImagebig() {
        return imagebig;
    }

    public void setImagebig(String imagebig) {
        this.imagebig = imagebig;
    }

    public Double getBedollars() {
        return bedollars;
    }

    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Long getDobUnix() {
        return dobUnix;
    }

    public void setDobUnix(Long dobUnix) {
        this.dobUnix = dobUnix;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean isEmailverified() {
        return emailverified;
    }

    public void setEmailverified(Boolean emailverified) {
        this.emailverified = emailverified;
    }

    public String toString(){
        return new Gson().toJson(this);
    }

    public Integer getRegularRewardsNumber() {
        return regularrewardsnumber;
    }

    public void setRegularRewardsNumber(Integer regularRewardsNumber) {
        this.regularrewardsnumber = regularRewardsNumber;
    }

    public String getFbtoken() {
        return fbtoken;
    }

    public void setFbtoken(String fbtoken) {
        this.fbtoken = fbtoken;
    }

    public static String getDefaultImage() {
        return defaultImage;
    }

    public Integer getRegularrewardsnumber() {
        return regularrewardsnumber;
    }

    public void setRegularrewardsnumber(Integer regularrewardsnumber) {
        this.regularrewardsnumber = regularrewardsnumber;
    }

    public AuthBean getAuth() {
        return auth;
    }

    public void setAuth(AuthBean auth) {
        this.auth = auth;
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
}
