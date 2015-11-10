package com.beintoo.broadcasters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.IntentCompat;
import android.widget.TextView;

import com.beintoo.asynctasks.NotificationClickedTask;
import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.services.GimbalBackgroundService;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.notification.BeNotificationManager;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.utils.notification.NotificationTimeStore;
import com.beintoo.wrappers.DynamicDictionaryBean;
import com.beintoo.wrappers.DynamicTextBean;
import com.beintoo.wrappers.EventNotificationWrapper;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.MissionNearMeWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            MemberBean member = MemberAuthStore.getMember(context);

            int notificationID = intent.getIntExtra("notificationID", 0);
            String message = "";
            String title = "BeClub";
            Intent contentIntent = new Intent(context, MainActivity.class);
            EventNotificationWrapper notificationWrapper;
            switch (notificationID) {
                case 899: // Wake up Gimbal SDK
                    NotificationTimeStore.setWhenTriggerNotification(NotificationID.WAKE_UP_GIMBAL_SDK.getId(), context, 0);
                    if (!member.getMemberssettings().get(0).getLocationbasedmessages()) {
                        return;
                    }
                    context.startService(new Intent(context, GimbalBackgroundService.class));
                    return;
                case 901: // NotificationID.NEW_FEATURES_UPDATE_APP
                    if (!member.getMemberssettings().get(0).getBeclubnews()) {
                        return;
                    }
                    message = "New features! Update the app";
                    contentIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()));
                    break;
                case 902: // NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS
                    notificationWrapper = new EventNotificationWrapper();

                    DynamicDictionaryBean copies = null;
                    DynamicTextBean text = null;
                    try {
                        copies = new Gson().fromJson(DataStore.getNotificationCopy(context), DynamicDictionaryBean.class);
                        text = copies.getTextFromPushCode("lw2");

                        title = text.getTitle();
                        message = text.getMessage();

                        notificationWrapper.setPushcode("lw2" + (text.getDynamicCode() != null ? text.getDynamicCode() : ""));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        notificationWrapper.setPushcode("lw2");
                    }

                    contentIntent = new Intent(context, MainActivity.class);

                    notificationWrapper.setType("pushlocal");
                    notificationWrapper.setEventtype("1b");

                    contentIntent.putExtra("notificationWrapper", new Gson().toJson(notificationWrapper));

                    new NotificationClickedTask(context, notificationWrapper, "send").execute();
                    NotificationTimeStore.setWeeksSent(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId(), true);
                    break;
                case 903: // NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS
                    notificationWrapper = new EventNotificationWrapper();

                    try {
                        copies = new Gson().fromJson(DataStore.getNotificationCopy(context), DynamicDictionaryBean.class);
                        text = copies.getTextFromPushCode("lw4");

                        title = text.getTitle();
                        message = text.getMessage();

                        notificationWrapper.setPushcode("lw4" + (text.getDynamicCode() != null ? text.getDynamicCode() : ""));
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        notificationWrapper.setPushcode("lw4");
                    }

                    contentIntent = new Intent(context, MainActivity.class);

                    notificationWrapper.setType("pushlocal");
                    notificationWrapper.setEventtype("1b");

                    contentIntent.putExtra("notificationWrapper", new Gson().toJson(notificationWrapper));

                    new NotificationClickedTask(context, notificationWrapper, "send").execute();
                    NotificationTimeStore.setWeeksSent(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId(), true);
                    break;
                case 907: // NotificationID.MISSION_NEARBY Gimbal
                    if (!member.getMemberssettings().get(0).getLocationbasedmessages()) {
                        return;
                    }
                    if (BeNotificationManager.getInstance(context).isAppInForeground()) {
                        BeNotificationManager.getInstance(context).showGimbalInternalNotification(intent);
                    } else {
                        handleGimbalNotification(context, intent);
                    }
                    return;
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.be_ic_launcher)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            .setContentText(message)
                            .setLights(0xFF00FF00, 1000, 2000)
                            .setVibrate(new long[]{500, 0, 500});

            PendingIntent pi = PendingIntent.getActivity(context, notificationID, contentIntent, 0);

            mBuilder.setContentIntent(pi);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationID, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGimbalNotification(Context context, Intent fromService) {
        MissionNearMeWrapper missionNearMe = new Gson().fromJson(fromService.getStringExtra("mission"), MissionNearMeWrapper.class);

        DebugUtility.showLog("####: Gimbal notification generation!");

        Intent activityToLaunch = new Intent(context, MainActivity.class);
        activityToLaunch.putExtra("mission", fromService.getStringExtra("mission"));
        activityToLaunch.putExtra("eventtype", "PUSH_NOTIFICATION");

        EventNotificationWrapper notificationWrapper = new EventNotificationWrapper();

        DynamicDictionaryBean copies = null;
        DynamicTextBean text = null;
        try {
            copies = new Gson().fromJson(DataStore.getNotificationCopy(context), DynamicDictionaryBean.class);
            text = copies.getTextFromPushCode("lgf");
            notificationWrapper.setPushcode("lgf" + (text.getDynamicCode() != null ? text.getDynamicCode() : ""));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            notificationWrapper.setPushcode("lgf");
        }

        notificationWrapper.setType("pushlocal");
        notificationWrapper.setEntityid(missionNearMe.getMissions().getMission().getId());
        notificationWrapper.setEventtype("1h");

        activityToLaunch.putExtra("notificationWrapper", new Gson().toJson(notificationWrapper));

        new NotificationClickedTask(context, notificationWrapper, "send").execute();

        activityToLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, activityToLaunch, PendingIntent.FLAG_CANCEL_CURRENT); // FLAG_CANCEL_CURRENT is needed due to a bug on KitKat. Other way is added exported = true inside manifest.xml

        String message;
        if(text != null) {
            message = text.getMessage().replace("{{brandname}}", missionNearMe.getMissions().getBrandname());
        } else {
            message = context.getString(R.string.gimbal_notification_body, missionNearMe.getMissions().getBrandname());
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.be_ic_launcher)
                        .setContentTitle(text != null ? text.getTitle() : context.getString(R.string.gimbal_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentText(message)
                        .setLights(0xFF00FF00, 1000, 2000)
                        .setVibrate(new long[]{500, 0, 500});

        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NotificationID.MISSION_NEARBY.getId(), mBuilder.build());
    }
}
