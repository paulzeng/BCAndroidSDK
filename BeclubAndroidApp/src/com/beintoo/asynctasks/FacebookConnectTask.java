package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.UnknownHostException;

import retrofit.RestAdapter;
import retrofit.client.Response;

public class FacebookConnectTask extends AsyncTask<Void, Void, MemberBean> {

    public interface OnFacebookLoginListener {
        public void onFacebookLoginNewMember(String email);
        public void onFacebookLoginSucceed();
        public void onFacebookLoginMissingInfo(MemberBean result);
        public void onFacebookLoginFailed(AsyncTasks.Result statusResult);
    }

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private AsyncTasks.Result statusResult;
    private MemberBean mMemberSignupBean;
    private OnFacebookLoginListener mListener;

    public FacebookConnectTask(Context context, MemberBean bean, OnFacebookLoginListener listener) {
        this.mContext = context;
        this.mMemberSignupBean = bean;
        this.mListener = listener;
        this.mProgressDialog = ProgressDialog.show(context, "Login", "Please wait...", true, false);
    }

    protected MemberBean doInBackground(Void... params) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getAuthKeyRestAdapter();
            AuthResource.IAuthResource service = restAdapter.create(AuthResource.IAuthResource.class);

            MemberBean member = service.facebookLogin(mMemberSignupBean);

            if (member.getAuth() != null) {
                MemberAuthStore.setAuth(mContext, member.getAuth());

                restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                service = restAdapter.create(AuthResource.IAuthResource.class);
                Response response = service.me(member.getAuth().getToken());

                MeBean meBean = new Gson().fromJson(new InputStreamReader(response.getBody().in()), MeBean.class);
                if (meBean.getMember() != null) {
                    MemberAuthStore.setMember(mContext, meBean.getMember());
                    MemberAuthStore.setApp(mContext, meBean.getApp());
                    statusResult = AsyncTasks.Result.OK;

                    return member;
                } else { // if ok but token null we have missing params
                    statusResult = AsyncTasks.Result.NOT_AVAILABLE;
                    return member;
                }
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

    protected void onPostExecute(MemberBean result) {
        super.onPostExecute(result);
        try {
            if (statusResult == AsyncTasks.Result.OK && result != null) {
                if(mListener != null) {
                    mListener.onFacebookLoginSucceed();
                }
            } else if (statusResult == AsyncTasks.Result.NOT_AVAILABLE) { // missing params from fb
                if(mListener != null) {
                    mListener.onFacebookLoginMissingInfo(result);
                }
            } else {
                if(mListener != null) {
                    mListener.onFacebookLoginFailed(statusResult);
                }
            }

            if (statusResult == AsyncTasks.Result.OK && result != null && result.getAuth().getInfobean() != null && result.getAuth().getInfobean().isNewmember()) {

                if(mListener != null) {
                    mListener.onFacebookLoginNewMember(result.getEmail());
                }
            }

            mProgressDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
