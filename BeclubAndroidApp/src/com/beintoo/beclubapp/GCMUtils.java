package com.beintoo.beclubapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IPushNotificationResource;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RootChecker;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobileapptracker.MobileAppTracker;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.client.Response;

public class GCMUtils {

    String SENDER_ID = "315247578878";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Context mContext;

    GoogleCloudMessaging gcm;
    String regid;


    private static GCMUtils instance = null;

    public static GCMUtils getInstance(Context context) {

        if (instance == null) {
            instance = new GCMUtils(context);
        }

        return instance;
    }

    public GCMUtils(Context context) {
        mContext = context;
    }

    public void initGCM() {

        if (checkPlayServices(mContext)) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
            regid = getRegistrationId(mContext);

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                DebugUtility.showLog("GCM TOKEN ---> " + regid);
            }
        } else {
            DebugUtility.showLog("GCM NOT AVAILABLE");
        }
    }


    private boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * removes the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     */
    public void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(mContext);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, null);
        editor.putInt(PROPERTY_APP_VERSION, -1);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            DebugUtility.showLog("Registration not found.");
            return "";
        } else {
            DebugUtility.showLog("GCM ID: " + registrationId);
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            DebugUtility.showLog("App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.

                    RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                    IPushNotificationResource memberResource = restAdapter.create(IPushNotificationResource.class);

                    GeoAndDeviceWrapper gaw = new GeoAndDeviceWrapper();
                    gaw.setAndroidid(DeviceId.getMACAddress(mContext));
                    if(regid != null)
                        gaw.setPushtoken(regid);

                    try {
                        Location location = BeLocationManager.getInstance(mContext).getLastKnowLocation(mContext);
                        if (location != null) {
                            gaw.setLatitude(location.getLatitude());
                            gaw.setLongitude(location.getLongitude());
                            gaw.setAltitude(location.getAltitude());
                            gaw.setHaccuracy((double) location.getAccuracy());
                        }
                        gaw.setImei(DeviceId.getImei(mContext));
                        gaw.setMacaddress(DeviceId.getMACAddress(mContext));
                        gaw.setMatid(MobileAppTracker.getInstance().getMatId());
                        gaw.setGoogleadvid(MemberAuthStore.advertiseID);
                        gaw.setGoogleadvidenabled(MemberAuthStore.advertiseDisable);
                        gaw.setDeviceunlocked(RootChecker.isRooted());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Response response = memberResource.pushNotifications(MemberAuthStore.getAuth(mContext).getToken(), MemberAuthStore.getMember(mContext).getId(), "ADD", gaw);

                    boolean registered = response.getStatus() == 200;
                    if (registered) {
                        storeRegistrationId(mContext, regid);
                    }
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DebugUtility.showLog("GCM ------------> " + msg);

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }
}
