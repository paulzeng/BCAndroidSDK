package com.beintoo.wrappers;

public class CoverageBean {

    private boolean card;
    private boolean ism;
    private boolean supported;
    private boolean beclubapp;
    private String basepath;
    private boolean gimbalbeacon;
    private boolean gimbalfence;

    public boolean isCard() {
        return card;
    }

    public void setCard(boolean card) {
        this.card = card;
    }

    public boolean isIsm() {
        return ism;
    }

    public void setIsm(boolean ism) {
        this.ism = ism;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public boolean isBeclubapp() {
        return beclubapp;
    }

    public void setBeclubapp(boolean beclubapp) {
        this.beclubapp = beclubapp;
    }

    public String getBasepath() {
        return basepath;
    }

    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }

    public boolean isGimbalbeacon() {
        return gimbalbeacon;
    }

    public void setGimbalbeacon(boolean gimbalbeacon) {
        this.gimbalbeacon = gimbalbeacon;
    }

    public boolean isGimbalfence() {
        return gimbalfence;
    }

    public void setGimbalfence(boolean gimbalfence) {
        this.gimbalfence = gimbalfence;
    }
}
