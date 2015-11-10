package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 21/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class ChangePasswordBean extends ResponseWrapper {

    private String oldPassword;
    private String password;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
