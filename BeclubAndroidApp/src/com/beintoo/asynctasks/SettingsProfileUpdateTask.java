package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.client.Response;


public class SettingsProfileUpdateTask extends AsyncTask<MemberBean, Void, MeBean> {

    public interface OnProfileUpdateListener {
        public void onProfileUpdate();
        public void onProfileUpdateFailed();
    }

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private OnProfileUpdateListener mListener;

    public SettingsProfileUpdateTask(Context context, OnProfileUpdateListener listener) {
        mContext = context;
        this.mListener = listener;
        mProgressDialog = ProgressDialog.show(context, "Update", "Please wait...", true, false);
    }

    @Override
    protected MeBean doInBackground(MemberBean... memberBeans) {
        try {
            MemberBean member = memberBeans[0];

            if (member.getDobUnix() != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                member.setDob(outputFormat.format(new Date(member.getDobUnix())));
                member.setDobUnix(null);
            }

            // remove the default images otherwise it will update them
            if (member.getImagebig().equals(MemberBean.defaultImage))
                member.setImagebig(null);
            if (member.getImagesmall().equals(MemberBean.defaultImage))
                member.setImagesmall(null);

            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IMemberResource service = restAdapter.create(IMemberResource.class);

            MemberBean memberBean = service.updateMember(MemberAuthStore.getAuth(mContext).getToken(), MemberAuthStore.getMember(mContext).getId(), member);
            if (memberBean != null) {

                AuthResource.IAuthResource authService = restAdapter.create(AuthResource.IAuthResource.class);

                Response response = authService.me(MemberAuthStore.getAuth(mContext).getToken());

                MeBean meBean = new Gson().fromJson(new InputStreamReader(response.getBody().in()), MeBean.class);
                if (meBean.getMember() != null) {
                    MemberAuthStore.setMember(mContext, meBean.getMember());

                    return meBean;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(MeBean memberBean) {
        super.onPostExecute(memberBean);

        if(memberBean != null) {
            if(mListener != null) {
                mListener.onProfileUpdate();;
            }
        } else {
            if(mListener != null) {
                mListener.onProfileUpdateFailed();
            }
        }


        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
