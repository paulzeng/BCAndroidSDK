package com.beintoo.utils;

public class ApiException  extends RuntimeException {
    private static final long serialVersionUID = 1L;
    String msg;
    Integer ID;

    public ApiException() {
        super();
        msg = "unknown";
    }

    public ApiException(String err) {
        super(err);
        msg = err;
    }

    public ApiException(String err, Integer id) {
        super(err);
        msg = err;
        ID = id;
    }

    public String getMessage() {
        return msg;
    }

    public Integer getId(){
        return ID;
    }
}
