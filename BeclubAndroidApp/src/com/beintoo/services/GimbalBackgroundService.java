package com.beintoo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.broadcasters.AlarmsReceiver;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.notification.BeNotificationManager;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.utils.notification.NotificationTimeStore;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.MissionNearMeWrapper;
import com.google.gson.Gson;
import com.qualcommlabs.usercontext.ContentListener;
import com.qualcommlabs.usercontext.ContextCoreConnector;
import com.qualcommlabs.usercontext.ContextCoreConnectorFactory;
import com.qualcommlabs.usercontext.ContextPlaceConnector;
import com.qualcommlabs.usercontext.ContextPlaceConnectorFactory;
import com.qualcommlabs.usercontext.PlaceEventListener;
import com.qualcommlabs.usercontext.protocol.ContentEvent;
import com.qualcommlabs.usercontext.protocol.PlaceEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GimbalBackgroundService extends Service {

    private ContextPlaceConnector mPlaceConnector;
    private ContextCoreConnector mCoreConnector;
    private PlaceEventListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        mCoreConnector = ContextCoreConnectorFactory.get(this);
        mCoreConnector.addContentListener(new ContentListener() {
            @Override
            public void contentEvent(ContentEvent contentEvent) {
                DebugUtility.showLog("Gimbal Content Title: " + contentEvent.getContent().get(0).getTitle() + " Description: " + contentEvent.getContent().get(0).getContentDescription());
            }
        });

        mPlaceConnector = ContextPlaceConnectorFactory.get(this);

        DebugUtility.showLog("Gimbal service started!");

        if(NotificationTimeStore.getWhenTriggerNotification(this, NotificationID.WAKE_UP_GIMBAL_SDK.getId()) > 0) { //If alarm was schedule to wake up this service in future, stop it. Fix for Android 4.4 that wake up every 20 mins. See below.
            stopSelf();
            return;
        }

        mPlaceConnector.monitorPlacesInBackground();
        mPlaceConnector.monitorPlacesWhenAllowed();

        listener = new PlaceEventListener() {
            @Override
            public void placeEvent(final PlaceEvent placeEvent) {
                try {
                    RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                    IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

                    if (placeEvent.getEventType().equals(PlaceEvent.PLACE_EVENT_TYPE_AT)) {
                        GeoAndDeviceWrapper gaw = GeoAndDeviceWrapper.build(GimbalBackgroundService.this, null);
                        gaw.setSubtypes(EarnBedollarsWrapper.getSubTypesAvailable(GimbalBackgroundService.this));
                        gaw.setFencename(placeEvent.getName());

                        missionService.geoFenceIn(MemberAuthStore.getAuth(GimbalBackgroundService.this).getToken(), gaw, new Callback<MissionNearMeWrapper>() {
                            @Override
                            public void success(MissionNearMeWrapper missionNearMe, Response response) {
                                String nextCall = missionNearMe.getNextcall();
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                                try {
                                    Date nextCallDate = dateFormatter.parse(nextCall);
                                    long nextCallMills = nextCallDate.getTime() - System.currentTimeMillis();
                                    BeNotificationManager.getInstance(GimbalBackgroundService.this).scheduleGimbalSDKWakeUp(nextCallMills);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mPlaceConnector.dontMonitorPlacesInBackground();
                                mPlaceConnector.dontMonitorPlacesWhenAllowed();

                                if (missionNearMe.getMissions() != null) {
                                    Intent intent = new Intent(GimbalBackgroundService.this, AlarmsReceiver.class);

                                    DebugUtility.showLog("####: Fence IN -> Send broadcast for notification!");

                                    intent.putExtra("notificationID", NotificationID.MISSION_NEARBY.getId());
                                    intent.putExtra("mission", new Gson().toJson(missionNearMe));
                                    GimbalBackgroundService.this.sendBroadcast(intent);
                                }

                                stopSelf();
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    } else {
                        DebugUtility.showLog("####: Fence OUT -> Call api fences/out!");

                        GeoAndDeviceWrapper gaw = GeoAndDeviceWrapper.build(GimbalBackgroundService.this, null);
                        gaw.setFencename(placeEvent.getName());

                        missionService.geoFenceOut(MemberAuthStore.getAuth(GimbalBackgroundService.this).getToken(), gaw, new Callback<MissionNearMeWrapper>() {
                            @Override
                            public void success(MissionNearMeWrapper missionNearMeWrapper, Response response) {
                                DebugUtility.showLog("####: Fence OUT -> OK!");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                DebugUtility.showLog("####: Fence OUT -> Error!");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DebugUtility.showLog("ERROR during send Gimbal notification.");
                }
            }
        };

        mPlaceConnector.addPlaceEventListener(listener);

        ensureServiceStaysRunning();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((intent != null) && (intent.getBooleanExtra("ALARM_RESTART_SERVICE_DIED", false))) {
            DebugUtility.showLog("onStartCommand after ALARM_RESTART_SERVICE_DIED");
            DebugUtility.showLog("Service already running - return immediately...");
            ensureServiceStaysRunning();
            return START_STICKY;
        }
        return Service.START_STICKY;
    }

    private void ensureServiceStaysRunning() {
        // KitKat appears to have (in some cases) forgotten how to honor START_STICKY
        // and if the service is killed, it doesn't restart.  On an emulator & AOSP device, it restarts...
        // on my CM device, it does not - WTF?  So, we'll make sure it gets back
        // up and running in a minimum of 20 minutes.  We reset our timer on a handler every
        // 2 minutes...but since the handler runs on uptime vs. the alarm which is on realtime,
        // it is entirely possible that the alarm doesn't get reset.  So - we make it a noop,
        // but this will still count against the app as a wakelock when it triggers.  Oh well,
        // it should never cause a device wakeup.  We're also at SDK 19 preferred, so the alarm
        // mgr set algorithm is better on memory consumption which is good.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // A restart intent - this never changes...
            final int restartAlarmInterval = 20 * 60 * 1000;
            final int resetAlarmTimer = 2 * 60 * 1000;
            final Intent restartIntent = new Intent(this, GimbalBackgroundService.class);
            restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
            final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Handler restartServiceHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Create a pending intent
                    PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
                    sendEmptyMessageDelayed(0, resetAlarmTimer);
                }
            };
            restartServiceHandler.sendEmptyMessageDelayed(0, 0);
        }
    }
}
