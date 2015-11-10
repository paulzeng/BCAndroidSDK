package com.beintoo.wrappers;

public class MeAndAuthWrapper{
    MeBean me;
    AuthBean auth;

    public MeAndAuthWrapper(MeBean me, AuthBean auth) {
        this.me = me;
        this.auth = auth;
    }

    public MeBean getMe() {
        return me;
    }

    public void setMe(MeBean me) {
        this.me = me;
    }

    public AuthBean getAuth() {
        return auth;
    }

    public void setAuth(AuthBean auth) {
        this.auth = auth;
    }
}