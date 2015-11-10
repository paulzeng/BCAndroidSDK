package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 02/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class CreditCard {
    private String first_name;
    private String last_name;
    private String month;
    private String year;
    private String number;
    private String email;

    public CreditCard(String first_name, String last_name, String month, String year, String number, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.month = month;
        this.year = year;
        this.number = number;
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class Builder {
        private String first_name;
        private String last_name;
        private String month;
        private String year;
        private String number;
        private String email;

        public Builder setFirstName(String firstName) {
            this.first_name = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.last_name = lastName;
            return this;
        }

        public Builder setMonth(String month) {
            this.month = month;
            return this;
        }

        public Builder setYear(String year) {
            this.year = year;
            return this;
        }

        public Builder setNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public CreditCard build() {
            return new CreditCard(first_name, last_name, month, year, number, email);
        }
    }
}
