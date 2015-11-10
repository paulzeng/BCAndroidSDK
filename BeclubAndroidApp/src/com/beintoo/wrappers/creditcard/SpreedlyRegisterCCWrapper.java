package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 02/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class SpreedlyRegisterCCWrapper {
    private String environment_key;
    private PaymentMethod payment_method;

    public SpreedlyRegisterCCWrapper(String environment_key, PaymentMethod payment_method) {
        this.environment_key = environment_key;
        this.payment_method = payment_method;
    }

    public String getEnvironment_key() {
        return environment_key;
    }

    public void setEnvironment_key(String environment_key) {
        this.environment_key = environment_key;
    }

    public PaymentMethod getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class Builder {
        private String environment_key;
        private PaymentMethod payment_method;

        public Builder setPaymentMethod(PaymentMethod paymentMethod) {
            this.payment_method = paymentMethod;
            return this;
        }

        public SpreedlyRegisterCCWrapper build() {
            this.environment_key = "5uPidpmiwpSYIIQmQ3DngmhOqTr";
            return new SpreedlyRegisterCCWrapper(this.environment_key, this.payment_method);
        }
    }
}
