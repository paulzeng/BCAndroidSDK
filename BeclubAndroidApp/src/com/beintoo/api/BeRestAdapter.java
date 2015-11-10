package com.beintoo.api;

import android.content.Context;
import android.preference.PreferenceManager;

import retrofit.RestAdapter;

/**
 * Created by Giulio Bider on 06/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class BeRestAdapter {

    public static RestAdapter getAuthKeyRestAdapter() {
        return new RestAdapter.Builder()
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ApiConfiguration.apiBaseUrl)
                .setRequestInterceptor(new AuthKeyInterceptor())
                .setErrorHandler(new BeErrorHandler())
                .build();
    }

    public static RestAdapter getMemberTokenRestAdapter() {
        return new RestAdapter.Builder()
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ApiConfiguration.apiBaseUrl)
                .setRequestInterceptor(new MemberTokenInterceptor())
                .setErrorHandler(new BeErrorHandler())
                .build();
    }

    public static RestAdapter getSpreedlyRestAdapter(Context context) {
        return new RestAdapter.Builder()
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(PreferenceManager.getDefaultSharedPreferences(context).getString("spreedlyBasePath", "https://core.spreedly.com/v1/"))
                .build();
    }
}
