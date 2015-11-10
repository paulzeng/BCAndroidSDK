package com.beintoo.wrappers;

import java.util.List;

public class AppVersionsWrapper {
    private List<String> allowed;
    private List<String> optional;

    public List<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

    public List<String> getOptional() {
        return optional;
    }

    public void setOptional(List<String> optional) {
        this.optional = optional;
    }

}
