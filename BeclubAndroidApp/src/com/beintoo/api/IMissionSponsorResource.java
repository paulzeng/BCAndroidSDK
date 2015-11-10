package com.beintoo.api;

import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.MissionNearMeWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.OnlineProductBean;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PictureAcceptMissionWrapper;
import com.beintoo.wrappers.PlacesContainerWrapper;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by Giulio Bider on 23/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public interface IMissionSponsorResource {

    @GET("/missions/sponsor/brands")
    void getBrands(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("addsubtype") Boolean addSubType, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("subtype")List<String> subTypes, @Query("brandid") String brandID, Callback<PaginatedList<EarnBedollarsWrapper>> cb);

    @GET("/missions/sponsor/mission")
    void getBrandMissions(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("subtype")List<String> subTypes, @Query("brandsid") String brandID, @Query("placesid") String placeID, Callback<PaginatedList<MissionWrapper>> cb);

    @GET("/missions/sponsor/mission")
    PaginatedList<MissionWrapper> getBrandMissions(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("subtype")List<String> subTypes, @Query("brandsid") String brandID, @Query("placesid") String placeID);

    @GET("/missions/sponsor/places")
    void getBrandPlaces(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("subtype")List<String> subTypes, @Query("brandsid") String brandID, @Query("missionsid") String missionID, Callback<PaginatedList<PlacesContainerWrapper>> cb);

    @GET("/missions/sponsor/places")
    PaginatedList<PlacesContainerWrapper> getBrandPlaces(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("subtype")List<String> subTypes, @Query("brandsid") String brandID, @Query("missionsid") String missionID);

    @GET("/missions/sponsor/{missionID}/details")
    AssignedMissionWrapper getMissionDetails(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Path("missionID") String missionID);

    @GET("/missions/sponsor/{missionID}/details")
    void getMissionDetails(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Path("missionID") String missionID, Callback<AssignedMissionWrapper> cb);

    @GET("/missions/sponsor/{missionID}")
    void getMission(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, @Path("missionID") String missionID, @Query("lastkey") String lastKey, @Query("rows") Integer rows, Callback<MissionWrapper> cb);

    @GET("/missions/sponsor/nearme")
    void getMissionNearMe(@Header("x-beintoo-auth") String token, @QueryMap Map<String, Object> location, Callback<MissionNearMeWrapper> cb);

    @PUT("/missions/sponsor/{missionID}/place/{placeID}/complete")
    AssignedMissionWrapper completeMission(@Header("x-beintoo-auth") String token, @Path("missionID") String missionID, @Path("placeID") String placeID, @Body GeoAndDeviceWrapper gaw);

    @PUT("/missions/sponsor/{missionID}/accept")
    void acceptMission(@Header("x-beintoo-auth") String token, @Path("missionID") String missionID, @Body PictureAcceptMissionWrapper pamw, Callback<Response> cb);

    @POST("/missions/sponsor/fences/in")
    void geoFenceIn(@Header("x-beintoo-auth") String token, @Body GeoAndDeviceWrapper gaw, Callback<MissionNearMeWrapper> cb);

    @POST("/missions/sponsor/fences/out")
    void geoFenceOut(@Header("x-beintoo-auth") String token, @Body GeoAndDeviceWrapper gaw, Callback<MissionNearMeWrapper> cb);

    @POST("/missions/sponsor/{missionID}/email")
    void sendOnlineMissionEmail(@Header("x-beintoo-auth") String token, @Path("missionID") String missionID, @Body OnlineProductBean productBean, Callback<?> cb);
}
