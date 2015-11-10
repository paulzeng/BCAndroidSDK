package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 03/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class SpreedlyResponse {
    private TransactionResponse transaction;

    public TransactionResponse getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionResponse transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
