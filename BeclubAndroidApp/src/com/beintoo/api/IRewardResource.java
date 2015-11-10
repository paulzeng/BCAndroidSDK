package com.beintoo.api;

import com.beintoo.wrappers.RewardWrapper;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IRewardResource {

    @GET("/rewards/{rewardID}")
    void getReward(@Header("x-beintoo-auth") String token, @Path("rewardID") String rewardID, @Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("alt") Double alt, @Query("ha") Float ha, Callback<RewardWrapper> cb);
}
