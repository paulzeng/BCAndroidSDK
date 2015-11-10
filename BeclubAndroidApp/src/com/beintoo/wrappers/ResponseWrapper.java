package com.beintoo.wrappers;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 07/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class ResponseWrapper {
    protected Integer code;
    protected String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
