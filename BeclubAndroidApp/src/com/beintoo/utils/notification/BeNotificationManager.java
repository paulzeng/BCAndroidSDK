package com.beintoo.utils.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.beintoo.asynctasks.NotificationClickedTask;
import com.beintoo.asynctasks.NotificationCopyTask;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.AlarmsReceiver;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.RedirectingScreenUtils;
import com.beintoo.wrappers.DynamicDictionaryBean;
import com.beintoo.wrappers.DynamicTextBean;
import com.beintoo.wrappers.EventNotificationWrapper;
import com.beintoo.wrappers.MissionNearMeWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BeNotificationManager {

    private static BeNotificationManager instance;

    private Context mContext;

    private FragmentActivity mActivity;

    private BeNotificationManager(Context context) {
        this.mContext = context;
    }

    public static BeNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new BeNotificationManager(context);
        }
        return instance;
    }

    public void onResume(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void onStop() {
        this.mActivity = null;
    }

    public boolean isAppInForeground() {
        return this.mActivity != null;
    }

    public void scheduleNotification() {
        /**
         SCHEDULE DON'T FORGET BRAND MISSION NOTIFICATION 2 WEEKS LATER
         */
        long twoWeekMillis = 14 * 24 * 60 * 60 * 1000;
        long wakeUpAt = System.currentTimeMillis() + twoWeekMillis;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, AlarmsReceiver.class);
        intent.putExtra("notificationID", NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId());
        PendingIntent pi = PendingIntent.getBroadcast(mContext, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId(), intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpAt, pi);

        DebugUtility.showLog("####: Notification FORGET BRAND MISSION NOTIFICATION 14 DAYS Scheduled");

        NotificationTimeStore.setWhenTriggerNotification(NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId(), mContext, wakeUpAt);
        NotificationTimeStore.setWeeksSent(mContext, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId(), false);

        /**
         SCHEDULE DON'T FORGET BRAND MISSION NOTIFICATION 4 WEEKS LATER
         */

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MONTH, 1);

        long fourWeekMillis = cal.getTimeInMillis();

        Intent intent4 = new Intent(mContext, AlarmsReceiver.class);
        intent4.putExtra("notificationID", NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId());
        PendingIntent pi4 = PendingIntent.getBroadcast(mContext, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId(), intent4, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, fourWeekMillis, pi4);

        DebugUtility.showLog("####: Notification FORGET BRAND MISSION NOTIFICATION 28 DAYS Scheduled: " + fourWeekMillis);

        NotificationTimeStore.setWhenTriggerNotification(NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId(), mContext, fourWeekMillis);
        NotificationTimeStore.setWeeksSent(mContext, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId(), false);

        /**
         * UPDATE NOTIFICATION COPY ONE PER DAY
         */
        DynamicDictionaryBean copies = new Gson().fromJson(DataStore.getNotificationCopy(mContext), DynamicDictionaryBean.class);
        if(copies == null) {
            new NotificationCopyTask(mContext).execute();
        } else {
            long creationDate = copies.getCreationDate();
            long now = System.currentTimeMillis();
            long creationPlus24 = creationDate + (24 * 60 *60 * 1000);
            if(creationPlus24 - now < 0) {
                new NotificationCopyTask(mContext).execute();
            }
        }
    }

    public void scheduleGimbalSDKWakeUp(long mills) {
        long wakeUpAt = System.currentTimeMillis() + mills;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, AlarmsReceiver.class);
        intent.putExtra("notificationID", NotificationID.WAKE_UP_GIMBAL_SDK.getId());
        PendingIntent pi = PendingIntent.getBroadcast(mContext, NotificationID.WAKE_UP_GIMBAL_SDK.getId(), intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpAt, pi);

        NotificationTimeStore.setWhenTriggerNotification(NotificationID.WAKE_UP_GIMBAL_SDK.getId(), mContext, wakeUpAt);

        DebugUtility.showLog("####: Alarm WAKE_UP_GIMBAL_SDK Scheduled: " + wakeUpAt);
    }

    public void clearAllNotifications() {
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationID.MISSION_NEARBY.getId());
        manager.cancel(NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId());
        manager.cancel(NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId());
        manager.cancel(NotificationID.PUSH_NOTIFICATION.getId());
    }

    public void showGimbalInternalNotification(Intent intent) {
        View view = View.inflate(mContext, R.layout.internal_notification, null);
        final MissionNearMeWrapper missionNearMe = new Gson().fromJson(intent.getStringExtra("mission"), MissionNearMeWrapper.class);

        final EventNotificationWrapper notificationWrapper = new EventNotificationWrapper();

        try {
            DynamicDictionaryBean copies = new Gson().fromJson(DataStore.getNotificationCopy(mContext), DynamicDictionaryBean.class);
            DynamicTextBean text = copies.getTextFromPushCode("lgf");

            ((TextView) view.findViewById(R.id.tv_title)).setText(text.getTitle());
            ((TextView)view.findViewById(R.id.tv_message)).setText(text.getMessage().replace("{{brandname}}", missionNearMe.getMissions().getBrandname()));
            notificationWrapper.setPushcode("lgf" + (text.getDynamicCode() != null ? text.getDynamicCode() : ""));

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            ((TextView) view.findViewById(R.id.tv_title)).setText(mContext.getString(R.string.gimbal_notification_title));
            ((TextView)view.findViewById(R.id.tv_message)).setText(mContext.getString(R.string.gimbal_notification_body, missionNearMe.getMissions().getBrandname()));
            notificationWrapper.setPushcode("lgf");
        }

        notificationWrapper.setType("pushlocal");
        notificationWrapper.setEventtype("1h");
        notificationWrapper.setEntityid(missionNearMe.getMissions().getMission().getId());

        new NotificationClickedTask(mContext, notificationWrapper, "send").execute();

        final Crouton crouton = Crouton.make(mActivity, view).setConfiguration(new Configuration.Builder().setDuration(5000).build());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = missionNearMe.getMissions().getMission().getActioncheck().getSubtype().toString();
                Intent intent = RedirectingScreenUtils.redirectingFromParams(mContext, type, missionNearMe.getMissions().getMission().getBrandsid());

                if (intent != null) {
                    Crouton.hide(crouton);
                    Crouton.clearCroutonsForActivity(mActivity);
                    new NotificationClickedTask(mContext, notificationWrapper, "open").execute();
                    mActivity.startActivity(intent);

                }
            }
        });
        crouton.show();



    }
}
