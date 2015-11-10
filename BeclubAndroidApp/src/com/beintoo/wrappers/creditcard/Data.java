package com.beintoo.wrappers.creditcard;

import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 02/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class Data {
    private String source;
    private String tos_code;

    public Data(String source, String tos_code) {
        this.source = source;
        this.tos_code = tos_code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTos_code() {
        return tos_code;
    }

    public void setTos_code(String tos_code) {
        this.tos_code = tos_code;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class Builder {
        private String source;
        private String tos_code;

        public Data build() {
            this.source = "BeClub Android";
            this.tos_code = "7";

            return new Data(source, tos_code);
        }
    }
}
