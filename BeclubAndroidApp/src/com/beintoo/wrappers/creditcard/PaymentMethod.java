package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 02/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class PaymentMethod {
    private CreditCard credit_card;
    private Data data;

    public PaymentMethod(CreditCard creditCard, Data data) {
        this.credit_card = creditCard;
        this.data = data;
    }

    public CreditCard getCredit_card() {
        return credit_card;
    }

    public void setCredit_card(CreditCard credit_card) {
        this.credit_card = credit_card;
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

    public static class Builder {
        private CreditCard credit_card;
        private Data data;

        public Builder setCreditCard(CreditCard creditCard) {
            this.credit_card = creditCard;
            return this;
        }

        public Builder setData(Data data) {
            this.data = data;
            return this;
        }

        public PaymentMethod build() {
            return new PaymentMethod(this.credit_card, this.data);
        }
    }
}
