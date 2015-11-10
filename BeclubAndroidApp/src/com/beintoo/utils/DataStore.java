package com.beintoo.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.beintoo.wrappers.DynamicDictionaryBean;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    private final static String TWITTER_KEY = "twitter_auth";

    public static void setTwitterToken(Context context, List<String> tokenAndSecret){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPrefs.edit().putString(TWITTER_KEY, new Gson().toJson(tokenAndSecret)).commit();

            DebugUtility.showLog("SAVED TWITTER TOKEN");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<String> getTwitterToken(Context context){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String jsonList = sharedPrefs.getString(TWITTER_KEY, null);

            if(jsonList != null){
                List<String> tokenAndSecret = new Gson().fromJson(jsonList, new TypeToken<List<String>>(){}.getType());

                return tokenAndSecret;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }

    public static List<String> getStringList(Context context, String key){
        try {
            DebugUtility.showLog("GETTING "+key);

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String jsonList = sharedPrefs.getString(key, null);

            if(jsonList != null){
                List<String> listData = new Gson().fromJson(jsonList, new TypeToken<List<String>>(){}.getType());

                return listData;
            }else{
                return new ArrayList<String>();
            }
        }catch (Exception e){
            e.printStackTrace();

            return new ArrayList<String>();
        }
    }

    public static void saveStringList(Context context, String key, List<String> data){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPrefs.edit().putString(key, new Gson().toJson(data)).commit();

            DebugUtility.showLog("SAVED "+key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(Context context, String key){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean value = sharedPrefs.getBoolean(key, false);

            DebugUtility.showLog("SHARED PREFS GOT BOOLEAN key: "+key + " value: "+value);

            return value;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    public static void saveBoolean(Context context, String key, boolean value){
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPrefs.edit().putBoolean(key, value).commit();

            DebugUtility.showLog("SHARED PREFS SAVED BOOLEAN key: "+key + " value: "+value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getNotificationCopyVersion(Context context) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("notification_copy_version", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setNotificationCopyVersion(Context context, String version) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putString("notification_copy_version", version).commit();

            DebugUtility.showLog("SHARED PREFS SAVED NOTIFICATION COPY VERSION:" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNotificationCopy(Context context) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            return preferences.getString("notification_copy", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setNotificationCopy(Context context, DynamicDictionaryBean copies) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putString("notification_copy", copies.toString()).commit();

            DebugUtility.showLog("SHARED PREFS SAVED NOTIFICATION COPY:" + copies.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
