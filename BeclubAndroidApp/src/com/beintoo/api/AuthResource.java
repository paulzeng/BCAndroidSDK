package com.beintoo.api;

import com.beintoo.utils.TokenExpiredException;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.ChangePasswordBean;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.MemberSignupBeClubBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

public class AuthResource {

    public interface IAuthResource {

        @GET("/auth/me")
        Response me();

        @GET("/auth/me")
        Response me(@Header("x-beintoo-auth") String token);

        @POST("/auth/members/signup/beclub")
        AuthBean signupMember(@Body MemberSignupBeClubBean signupBeClubBean);

        @POST("/auth/members/login/beclub")
        AuthBean loginMember(@Body GeoAndDeviceWrapper loginMemberWrapper);

        @PUT("/auth/me/logout")
        Response logoutMember(@Header("x-beintoo-auth") String token);

        @GET("/auth/members/forgotpassword/beclub")
        void forgotPassword(@Query("email") String email, Callback<Response> cb);

        @POST("/auth/members/signin/fbms")
        MemberBean facebookLogin(@Body MemberBean signupBeClubBean);

        @POST("/auth/members/changepassword/beclub")
        void changePassword(@Header("x-beintoo-auth") String token, @Body ChangePasswordBean changePasswordBean, Callback<AuthBean> cb);

        @GET("/auth/send/emailverification")
        void sendVerificationEmail(@Header("x-beintoo-auth") String token, Callback<?> cb);

        @POST("/auth/onappupdate")
        Response onAppUpdate(@Header("x-beintoo-auth") String token, @Body GeoAndDeviceWrapper geoAndDeviceWrapper);
    }

    public MeBean me(String token) throws Exception {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IAuthResource service = restAdapter.create(IAuthResource.class);

        Response response = service.me(token);
        MeBean meBean = new Gson().fromJson(new InputStreamReader(response.getBody().in()), MeBean.class);

        if (meBean == null || (meBean.getRole().equals("PUBLIC"))) {
            throw new TokenExpiredException();
        }

        return meBean;
    }
}