package com.beintoo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IPushNotificationResource;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.EventNotificationWrapper;

import retrofit.RestAdapter;

public class NotificationClickedTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private EventNotificationWrapper mNotificationWrapepr;
    private String mAction;

    public NotificationClickedTask(Context context, EventNotificationWrapper notificationWrapper, String action) {
        this.mContext = context;
        this.mNotificationWrapepr = notificationWrapper;
        this.mAction = action;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IPushNotificationResource pushService = restAdapter.create(IPushNotificationResource.class);
            pushService.trackNotificationClicked(MemberAuthStore.getAuth(mContext).getToken(), mAction, mNotificationWrapepr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
