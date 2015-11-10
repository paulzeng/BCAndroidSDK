package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberSignupBeClubBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.UnknownHostException;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by Giulio Bider on 15/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class MemberSignupTask extends AsyncTask<Void, Integer, MeBean> {

    public interface OnSignupListener {
        public void onSignupSucceed(String email);

        public void onSignupFailed(AsyncTasks.Result errorResult);
    }

    private Context mContext;
    private MemberSignupBeClubBean mMemberSignupBean;
    private ProgressDialog mProgressDialog;
    private OnSignupListener mCallback;
    private AsyncTasks.Result statusResult;

    public MemberSignupTask(Context context, MemberSignupBeClubBean memberSignupBeClubBean, OnSignupListener callback) {
        mContext = context;
        mMemberSignupBean = memberSignupBeClubBean;
        mCallback = callback;
        mProgressDialog = ProgressDialog.show(context, "Signup", "Please wait...", true, false);
    }

    @Override
    protected MeBean doInBackground(Void... data) {
        try {

            RestAdapter restAdapter = BeRestAdapter.getAuthKeyRestAdapter();
            AuthResource.IAuthResource service = restAdapter.create(AuthResource.IAuthResource.class);

            AuthBean auth = service.signupMember(mMemberSignupBean);

            if (auth.getToken() != null) {
                MemberAuthStore.setAuth(mContext, auth);

                restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                service = restAdapter.create(AuthResource.IAuthResource.class);
                Response response = service.me(auth.getToken());

                MeBean meBean = new Gson().fromJson(new InputStreamReader(response.getBody().in()), MeBean.class);
                if (meBean.getMember() != null) {
                    MemberAuthStore.setMember(mContext, meBean.getMember());
                    MemberAuthStore.setApp(mContext, meBean.getApp());
                    statusResult = AsyncTasks.Result.OK;
                    return meBean;
                } else {
                    statusResult = AsyncTasks.Result.FAILED;
                }
            } else {
                statusResult = AsyncTasks.Result.FAILED;
            }

        } catch (ApiException apiEx) {
            apiEx.printStackTrace();
            if (apiEx.getId() == -4) {
                statusResult = AsyncTasks.Result.NOT_AVAILABLE;
            } else if (apiEx.getId() == ApiConfiguration.BANNED_DEVICE_CODE) {
                statusResult = AsyncTasks.Result.BANNED;
            } else if (apiEx.getId() == ApiConfiguration.EMAIL_NOT_VALID_CODE) {
                statusResult = AsyncTasks.Result.OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof UnknownHostException) {
                statusResult = AsyncTasks.Result.NETWORK_ERROR;
            } else {
                statusResult = AsyncTasks.Result.FAILED;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(MeBean meBean) {
        super.onPostExecute(meBean);
        if (statusResult == AsyncTasks.Result.OK) {
            if (mCallback != null) {
                mCallback.onSignupSucceed(meBean.getMember().getEmail());
            }
        } else {
            if (mCallback != null) {
                mCallback.onSignupFailed(statusResult);
            }
        }

        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

