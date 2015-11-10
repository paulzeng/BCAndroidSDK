package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.List;

public class CardsInfoResponse {

    private String zip;
    private String cardtype;
    private String cardSpringToken;
    private String linkableToken;
    private String fourdigit;
    private String token;
    private int month;
    private int year;
    private int tos;
    private String cardimageurl;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getCardSpringToken() {
        return cardSpringToken;
    }

    public void setCardSpringToken(String cardSpringToken) {
        this.cardSpringToken = cardSpringToken;
    }

    public String getLinkableToken() {
        return linkableToken;
    }

    public void setLinkableToken(String linkableToken) {
        this.linkableToken = linkableToken;
    }

    public String getFourdigit() {
        return fourdigit;
    }

    public void setFourdigit(String fourdigit) {
        this.fourdigit = fourdigit;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTos() {
        return tos;
    }

    public void setTos(int tos) {
        this.tos = tos;
    }

    public String getCardimageurl() {
        return cardimageurl;
    }

    public void setCardimageurl(String cardimageurl) {
        this.cardimageurl = cardimageurl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
