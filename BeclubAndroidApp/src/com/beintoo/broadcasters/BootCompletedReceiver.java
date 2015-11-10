package com.beintoo.broadcasters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.beintoo.services.GimbalBackgroundService;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.utils.notification.NotificationTimeStore;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if(action.equals("android.intent.action.BOOT_COMPLETED")) {
                DebugUtility.showLog("BOOT COMPLETED! Starting Gimbal service...");

                // Starting Gimbal Service
                context.startService(new Intent(context, GimbalBackgroundService.class));

                // Starting Alarms

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent alarmReceiver = new Intent(context, AlarmsReceiver.class);
                alarmReceiver.putExtra("notificationID", NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId());
                PendingIntent pi = PendingIntent.getBroadcast(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId(), alarmReceiver, 0);

                long trigger = NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId());
                if(trigger - System.currentTimeMillis() > 0) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId()), pi);
                    DebugUtility.showLog("####: Notifica FORGET BRAND MISSION NOTIFICATION 14 DAYS Schedulata dopo il BOOT COMPLETED");
                } else if(trigger - System.currentTimeMillis()  < 0 && !NotificationTimeStore.isWeeksSent(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId())) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_14_DAYS.getId()), pi);
                    DebugUtility.showLog("####: Notifica FORGET BRAND MISSION NOTIFICATION 14 DAYS Schedulata dopo il BOOT COMPLETED Nel passato mai apparsa");
                }

                Intent alarmReceiver4 = new Intent(context, AlarmsReceiver.class);
                alarmReceiver4.putExtra("notificationID", NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId());
                PendingIntent pi4 = PendingIntent.getBroadcast(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId(), alarmReceiver4, 0);

                trigger = NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId());

                if(trigger - System.currentTimeMillis() > 0) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId()), pi4);
                    DebugUtility.showLog("####: Notifica FORGET BRAND MISSION NOTIFICATION 28 DAYS Schedulata dopo il BOOT COMPLETED");
                } else if(trigger - System.currentTimeMillis() < 0 && !NotificationTimeStore.isWeeksSent(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId())) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, NotificationTimeStore.getWhenTriggerNotification(context, NotificationID.DONT_FORGET_BRAND_MISSION_28_DAYS.getId()), pi4);
                    DebugUtility.showLog("####: Notifica FORGET BRAND MISSION NOTIFICATION 28 DAYS Schedulata dopo il BOOT COMPLETED Nel passato mai apparsa");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
