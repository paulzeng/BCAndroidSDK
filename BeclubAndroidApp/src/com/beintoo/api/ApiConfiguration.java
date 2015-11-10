package com.beintoo.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.beintoo.utils.DebugUtility;

public class ApiConfiguration {

    public enum Environment{
        LOCAL,
        SANDBOX,
        DEVELOPMENT,
        PRODUCTION
    }

    public static String localUrl = "http://localhost:8080/";
    public static String productionUrl = "https://api-v2.beintoo.com/";
    public static String sandboxUrl = "https://api-v2-sand.beintoo.com/";
    public static String devUrl = "http://54.221.232.178:8080/";
    public static String authKey = "x6yffTsXW5PVzrvfikq2bLBG83ORJVq1sf90CDPO";
    public static String apiBaseUrl = ApiConfiguration.productionUrl;
    public static Environment workingEnvironment = Environment.PRODUCTION;
    public static String appVersion = "1.1.0a5";

    public static void setEnvironment(Environment environment){
        if(environment == Environment.PRODUCTION){
            apiBaseUrl = productionUrl;
            DebugUtility.isDebugEnable = false;
        }else if(environment == Environment.SANDBOX){
            apiBaseUrl = sandboxUrl;
            DebugUtility.isDebugEnable = true;
        }else if(environment == Environment.DEVELOPMENT){
            apiBaseUrl = devUrl;
            DebugUtility.isDebugEnable = true;
        } else if(environment == Environment.LOCAL) {
            apiBaseUrl = localUrl;
            DebugUtility.isDebugEnable = true;
        }

        workingEnvironment = environment;
    }

    /**
     * Set app version including libraries indicator for API
     * @param context
     */
    public static void setAppVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String versione = pInfo.versionName;
            String[] versionSplit = versione.split("\\.");

            String lastNumber;
            String middleNumber;

            if(versionSplit[2].length() == 1) {
                lastNumber = "0" + versionSplit[2];
            } else {
                lastNumber = versionSplit[2];
            }

            if(versionSplit[1].length() == 1) {
                middleNumber = "0" + versionSplit[1];
            } else {
                middleNumber = versionSplit[1];
            }

            String finalVersion = versionSplit[0] + middleNumber + lastNumber;

            appVersion = finalVersion + "a5";
        } catch (Exception e) {
            e.printStackTrace();
            appVersion = pInfo.versionName + "a5";
        }

        DebugUtility.showLog("APP VERSION: " + appVersion);
    }

    /**
     * Return app version without libraries indicator es. a1
     */
    public static String getAppVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public final static int BANNED_DEVICE_CODE = -34; // status code 400
    public final static int EMAIL_NOT_VALID_CODE = -35; // status code 400
}