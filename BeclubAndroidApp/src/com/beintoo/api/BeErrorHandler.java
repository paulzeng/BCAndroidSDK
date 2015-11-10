package com.beintoo.api;

import com.beintoo.utils.ApiException;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.TokenExpiredException;
import com.beintoo.wrappers.ResponseWrapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by Giulio Bider on 16/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class BeErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        if(cause.isNetworkError()) {
            return cause;
        }
        try {
            ResponseWrapper response = new Gson().fromJson(new InputStreamReader(cause.getResponse().getBody().in()), ResponseWrapper.class);
            if (response.getCode() == -19) {
                DebugUtility.showLog("TOKEN IS EXPIRED");
                return new TokenExpiredException();
            } else if(response.getCode() == -4) {
                return new ApiException("Error: not available", -4);
            } else if(response.getCode() == ApiConfiguration.BANNED_DEVICE_CODE) {
                return new ApiException("Device banned!", ApiConfiguration.BANNED_DEVICE_CODE);
            } else if(response.getCode() == ApiConfiguration.EMAIL_NOT_VALID_CODE) {
                return new ApiException("Email not valid", ApiConfiguration.EMAIL_NOT_VALID_CODE);
            } else if(response.getCode() == -25) {
                return new ApiException("Reward not available", -25);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cause;
    }
}
