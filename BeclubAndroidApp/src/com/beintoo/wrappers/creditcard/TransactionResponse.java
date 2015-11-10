package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 03/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class TransactionResponse {
    private boolean succeeded;
    private PaymentMethodResponse payment_method;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public PaymentMethodResponse getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethodResponse payment_method) {
        this.payment_method = payment_method;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
