package com.beintoo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IPushNotificationResource;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.DynamicDictionaryBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by Giulio Bider on 25/08/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class NotificationCopyTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    public NotificationCopyTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IPushNotificationResource pnr = restAdapter.create(IPushNotificationResource.class);

            String version = DataStore.getNotificationCopyVersion(mContext);
            if (version == null || "".equals(version)) {
                version = null;
            }

            Response response = pnr.getNotificationABCopy(MemberAuthStore.getAuth(mContext).getToken(), version);

            if (response.getStatus() == 204) {
                //I had the latest version
                DynamicDictionaryBean copies = new Gson().fromJson(DataStore.getNotificationCopy(mContext), DynamicDictionaryBean.class);
                copies.setCreationDate(System.currentTimeMillis());
                DataStore.setNotificationCopy(mContext, copies);
            } else if (response.getStatus() == 200) {
                DynamicDictionaryBean copies = new Gson().fromJson(new InputStreamReader(response.getBody().in()), DynamicDictionaryBean.class);
                copies.setCreationDate(System.currentTimeMillis());
                DataStore.setNotificationCopyVersion(mContext, copies.getVersion());
                DataStore.setNotificationCopy(mContext, copies);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
