package com.beintoo.wrappers;

import com.google.gson.Gson;

public class MeBean extends ResponseWrapper {

    private MemberBean member;
    private AppWrapper app;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AppWrapper getApp() {
        return app;
    }

    public void setApp(AppWrapper app) {
        this.app = app;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
