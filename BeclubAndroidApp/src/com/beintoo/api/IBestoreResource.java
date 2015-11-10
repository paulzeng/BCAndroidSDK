package com.beintoo.api;

import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.RewardWrapper;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Giulio Bider on 27/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public interface IBestoreResource {

    @GET("/bestore")
    void getBestore(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("lastkey") String lastKey, @Query("rows") Integer rows, Callback<PaginatedList<RewardWrapper>> cb);

    @POST("/bestore/buy/rewards/{rewardID}/members/{memberID}")
    BuyWrapper buyReward(@Header("x-beintoo-auth") String token, @Path("rewardID") String rewardID, @Path("memberID") String memberID, @Body GeoAndDeviceWrapper gaw);
}
