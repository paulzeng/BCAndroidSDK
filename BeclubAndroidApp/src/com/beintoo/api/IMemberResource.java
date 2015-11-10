package com.beintoo.api;

import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.ImageUploadWrapper;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.MemberSettingsBean;
import com.beintoo.wrappers.MembersBedollarsWrapper;
import com.beintoo.wrappers.PaginatedList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface IMemberResource {

    @PUT("/members/{memberID}/rewards/{rewardID}/{action}")
    void archiveReward(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Path("rewardID") String rewardID, @Path("action") String action, Callback<?> cb);

    @GET("/members/{memberID}/rewards")
    void getMemberWallet(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Query("type") String type, @Query("lastkey") String lastKey, @Query("rows") Integer rows, Callback<PaginatedList<BuyWrapper>> cb);

    @GET("/members/{memberID}/balance")
    void getMemberHistory(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Query("lastkey") String lastKey, @Query("rows") Integer rows, @Query("expandfields") String expandFields, Callback<PaginatedList<MembersBedollarsWrapper>> cb);

    @PUT("/members/{memberID}?b=m")
    MemberBean updateMember(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Body MemberBean memberBean);

    @Headers("Content-Type: image/jpeg")
    @POST("/members/{memberID}/image")
    ImageUploadWrapper uploadMemberProfileImage(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Body TypedFile photo);

    @POST("/members/{memberID}/settings/me")
    void updateMemberSettings(@Header("x-beintoo-auth") String token, @Path("memberID") String memberID, @Body MemberSettingsBean memberSettingsBean, Callback<?> cb);
}