package com.beintoo.utils.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationTimeStore {

    public static void setWhenTriggerNotification(int notificationID, Context context, long millis) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putLong(String.valueOf(notificationID), millis).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getWhenTriggerNotification(Context context, int notificationID) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getLong(String.valueOf(notificationID), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setWeeksSent(Context context, int notificationID, boolean sent) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putBoolean(String.valueOf(notificationID) + "-sent", sent).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWeeksSent(Context context, int notificationID) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getBoolean(String.valueOf(notificationID) + "-sent", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
