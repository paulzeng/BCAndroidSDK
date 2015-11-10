package com.beintoo.api;

import android.os.Build;

import java.util.Locale;

import retrofit.RequestInterceptor;

/**
 * Created by Giulio Bider on 06/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class MemberTokenInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("x-beintoo-app-version", ApiConfiguration.appVersion);
        request.addHeader("Accept-Language", Locale.getDefault().toString());
        request.addHeader("x-beintoo-app-osv", String.valueOf(Build.VERSION.SDK_INT));
    }
}
