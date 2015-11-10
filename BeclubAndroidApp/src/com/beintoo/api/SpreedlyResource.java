package com.beintoo.api;

import android.content.Context;

import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.creditcard.PaymentMethodResponse;
import com.beintoo.wrappers.creditcard.SpreedlyRegisterCCWrapper;
import com.beintoo.wrappers.creditcard.SpreedlyResponse;

import retrofit.RestAdapter;
import retrofit.client.Response;

public class SpreedlyResource {

    public PaginatedList<CardsInfoResponse> getUserDetails(Context context) throws Exception {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        ICreditCardResource creditCardService = restAdapter.create(ICreditCardResource.class);

        return creditCardService.getUserCreditCards(MemberAuthStore.getAuth(context).getToken());
    }
    public String getBalanceForCard(Context context, String cardToken) throws Exception {
//        IgnitedHttp http = new IgnitedHttp(context);
//        http.setGzipEncodingEnabled(true);
//        AuthBean auth = MemberAuthStore.getAuth(context);
//        MemberBean me = MemberAuthStore.getMember(context);
//
//        http.setDefaultHeader("x-beintoo-auth", auth.getToken());
//        http.setDefaultHeader("x-beintoo-app-version", ApiConfiguration.appVersion);
//        http.setDefaultHeader("Accept-Language", Locale.getDefault().toString());
//        http.setDefaultHeader("x-beintoo-app-osv", String.valueOf(Build.VERSION.SDK_INT));
//
//        Uri.Builder b = Uri.parse(ApiConfiguration.apiBaseUrl + "cardspring/users/" + me.getId() + "/cards/" + cardToken + "/balance").buildUpon();
//
//        String apiUrl = b.build().toString();
//
//        IgnitedHttpResponse resp = http.get(apiUrl).retries(3).send();
//
//        String json = resp.getResponseBodyAsString();
//        DebugUtility.showLog("called " + apiUrl + " " + resp.getStatusCode() + " RESPONSE " + json);
//
//        if (resp.getStatusCode() >= 200 && resp.getStatusCode() < 300) {
//            return json;
//        }
        return null;
    }

    public PaginatedList getTransactionsForCard(Context context, String cardToken, String lastKey, Integer rows) throws Exception {
//        IgnitedHttp http = new IgnitedHttp(context);
//        http.setGzipEncodingEnabled(true);
//        AuthBean auth = MemberAuthStore.getAuth(context);
//        MemberBean me = MemberAuthStore.getMember(context);
//
//        http.setDefaultHeader("x-beintoo-auth", auth.getToken());
//        http.setDefaultHeader("x-beintoo-app-version", ApiConfiguration.appVersion);
//        http.setDefaultHeader("Accept-Language", Locale.getDefault().toString());
//        http.setDefaultHeader("x-beintoo-app-osv", String.valueOf(Build.VERSION.SDK_INT));
//
//        Uri.Builder b = Uri.parse(ApiConfiguration.apiBaseUrl + "cardspring/users/" + me.getId() + "/cards/" + cardToken).buildUpon();
//
//        if (lastKey != null) {
//            b.appendQueryParameter("lastkey", lastKey);
//        }
//
//        if (rows != null) {
//            b.appendQueryParameter("rows", rows.toString());
//        }
//
//        String apiUrl = b.build().toString();
//
//        IgnitedHttpResponse resp = http.get(apiUrl).retries(3).send();
//
//        String json = resp.getResponseBodyAsString();
//        DebugUtility.showLog("called " + apiUrl + " " + resp.getStatusCode() + " RESPONSE " + json);
//
//        Type type = new TypeToken<PaginatedList<CardTransactionBean>>() {
//        }.getType();
//
//        PaginatedList<CardTransactionBean> results = new Gson().fromJson(json, type);
        return null;
    }

    public SpreedlyResponse registerCardOnSpreedly(Context context, SpreedlyRegisterCCWrapper cardInfo) throws Exception {
        RestAdapter restAdapter = BeRestAdapter.getSpreedlyRestAdapter(context);
        ICreditCardResource creditCardService = restAdapter.create(ICreditCardResource.class);

        SpreedlyResponse response = creditCardService.registerCardOnSpreedly(cardInfo);
        if(response.getTransaction() != null && response.getTransaction().isSucceeded()) {
            return response;
        }
        return null;
    }

    public boolean registerCardTokenOnBeintoo(Context context, SpreedlyResponse spreedlyResponse) throws Exception {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        ICreditCardResource creditCardService = restAdapter.create(ICreditCardResource.class);

        PaymentMethodResponse token = new PaymentMethodResponse();
        token.setToken(spreedlyResponse.getTransaction().getPayment_method().getToken());

        Response response = creditCardService.registerCardTokenOnBeintoo(MemberAuthStore.getAuth(context).getToken(), token);

        return response.getStatus() >= 200 && response.getStatus() < 300;
    }

    public boolean deleteCard(Context context, String cardToken) throws Exception {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        ICreditCardResource creditCardService = restAdapter.create(ICreditCardResource.class);

        Response response = creditCardService.deleteCreditCard(MemberAuthStore.getAuth(context).getToken(), cardToken);

        return response.getStatus() >= 200 && response.getStatus() < 300;
    }
}
