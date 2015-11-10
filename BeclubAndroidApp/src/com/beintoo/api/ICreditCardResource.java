package com.beintoo.api;

import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.creditcard.PaymentMethodResponse;
import com.beintoo.wrappers.creditcard.SpreedlyRegisterCCWrapper;
import com.beintoo.wrappers.creditcard.SpreedlyResponse;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Giulio Bider on 27/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public interface ICreditCardResource {

    @GET("/cc")
    PaginatedList<CardsInfoResponse> getUserCreditCards(@Header("x-beintoo-auth") String token);

    @POST("/cc")
    Response registerCardTokenOnBeintoo(@Header("x-beintoo-auth") String token, @Body PaymentMethodResponse paymentMethodResponse);

    @DELETE("/cc/{cardToken}")
    Response deleteCreditCard(@Header("x-beintoo-auth") String token, @Path("cardToken") String cardToken);

    @POST("payment_methods.json")
    SpreedlyResponse registerCardOnSpreedly(@Body SpreedlyRegisterCCWrapper cc);
}
