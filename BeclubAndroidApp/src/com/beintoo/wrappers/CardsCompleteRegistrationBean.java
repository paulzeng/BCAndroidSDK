package com.beintoo.wrappers;

import com.google.gson.Gson;

public class CardsCompleteRegistrationBean {

    private String publisher_id;
    private String security_token;
    private String hmac;
    private Long timestamp;
    private String user_id;
    private String card_number;
    private Boolean test_mode;

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getSecurity_token() {
        return security_token;
    }

    public void setSecurity_token(String security_token) {
        this.security_token = security_token;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public Boolean getTest_mode() {
        return test_mode;
    }

    public void setTest_mode(Boolean test_mode) {
        this.test_mode = test_mode;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
