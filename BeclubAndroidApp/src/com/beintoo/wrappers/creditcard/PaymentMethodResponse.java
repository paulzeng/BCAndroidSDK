package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 03/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class PaymentMethodResponse {
    private String token;
    private Data data;
    private String card_type;

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
