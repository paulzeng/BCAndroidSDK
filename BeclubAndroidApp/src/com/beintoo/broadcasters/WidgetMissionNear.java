package com.beintoo.broadcasters;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MissionNearMeWrapper;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WidgetMissionNear extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.internal_notification);
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

            missionService.getMissionNearMe(MemberAuthStore.getAuth(context).getToken(), LocationUtils.getLocationParams(context), new Callback<MissionNearMeWrapper>() {
                @Override
                public void success(MissionNearMeWrapper missionNearMe, Response response) {
                    try {
                        views.setTextViewText(R.id.tv_title, context.getString(R.string.gimbal_notification_title));
                        views.setTextViewText(R.id.tv_message, context.getString(R.string.gimbal_notification_body, missionNearMe.getMissions().getBrandname()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        views.setTextViewText(R.id.tv_title, "BeClub");
                        views.setTextViewText(R.id.tv_message, "There aren't missions near you at the moment!");
                    }

                    appWidgetManager.updateAppWidget(appWidgetIds[0], views);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
