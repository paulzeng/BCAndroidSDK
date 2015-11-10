package com.beintoo.api;

import com.beintoo.wrappers.EventNotificationWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IPushNotificationResource {

    @POST("/members/{memberID}/push/{action}")
    Response pushNotifications(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Path("action") String action, @Body GeoAndDeviceWrapper gaw);

    @POST("/push/track/{action}")
    Response trackNotificationClicked(@Header("x-beintoo-auth") String token, @Path("action") String action, @Body EventNotificationWrapper notificationWrapper);

    @GET("/push/dictionary/pushlocal/dynamic")
    Response getNotificationABCopy(@Header("x-beintoo-auth") String token, @Query("lastversion") String lastVersion);
}
