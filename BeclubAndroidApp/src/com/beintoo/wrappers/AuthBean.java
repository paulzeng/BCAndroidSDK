package com.beintoo.wrappers;

import com.google.gson.Gson;

public class AuthBean extends ResponseWrapper {

    private String membersId;
    private String token;
    private String customersId;
    private String ghostsId;
    private String appsId;
    private InfoBean infobean;

    public String getMembersId() {
        return membersId;
    }

    public void setMembersId(String membersId) {
        this.membersId = membersId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomersId() {
        return customersId;
    }

    public void setCustomersId(String customersId) {
        this.customersId = customersId;
    }

    public String getGhostsId() {
        return ghostsId;
    }

    public void setGhostsId(String ghostsId) {
        this.ghostsId = ghostsId;
    }

    public String getAppsId() {
        return appsId;
    }

    public void setAppsId(String appsId) {
        this.appsId = appsId;
    }

    public InfoBean getInfobean() {
        return infobean;
    }

    public void setInfobean(InfoBean infobean) {
        this.infobean = infobean;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
