package com.beintoo.api;

import com.beintoo.wrappers.LogAppBean;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

public interface ILogResource {

    @POST("/log")
    void sendLogReport(@Header("x-beintoo-auth") String token, @Body LogAppBean logAppBean, Callback<?> cb);
}
