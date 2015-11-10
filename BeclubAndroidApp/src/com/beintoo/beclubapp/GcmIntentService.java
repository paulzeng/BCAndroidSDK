package com.beintoo.beclubapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.EventNotificationWrapper;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());*/
                // Post notification of received message.
                try {
                    sendNotification(extras);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle msg) {
        if(msg == null) {
            return;
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        DebugUtility.showLog("PUSH PAYLOAD IS: "+msg.toString());

        String eventType = msg.getString("y");
        String entityId = msg.getString("e");

        String firstChar = eventType.substring(0, 1);

        String secondChar = "";
        try {
            secondChar = eventType.substring(1, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventNotificationWrapper notificationWrapper = new EventNotificationWrapper();
        notificationWrapper.setPushcode(msg.getString("c"));
        notificationWrapper.setEntityid(msg.getString("e"));
        notificationWrapper.setEventtype(msg.getString("y"));
        notificationWrapper.setType("push");


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("eventtype", "URL_SCHEME_LAUNCH");
        intent.putExtra("notificationWrapper", new Gson().toJson(notificationWrapper));

        if(firstChar.equals("0")) {
            generateAndSendNotification(intent, msg);
            return;
        }

        if(secondChar.equals("a")) {
            intent.putExtra("path", "/bestore");
        } else if(secondChar.equals("b")) {
            intent.putExtra("path", "/earnbedollars");
        } else if(secondChar.equals("c")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
        } else if(secondChar.equals("d")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
            intent.putExtra("missionType", EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN.toString());
        } else if(secondChar.equals("e")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
            intent.putExtra("missionType", EarnBedollarsWrapper.MissionActionTypeEnum.SCAN.toString());
        } else if(secondChar.equals("f")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
            intent.putExtra("missionType", EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN.toString());
        } else if(secondChar.equals("g")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
            intent.putExtra("missionType", EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE.toString());
        } else if(secondChar.equals("h")) {
            intent.putExtra("path", "/earnbedollars/missions");
            intent.putExtra("missionID", entityId);
        } else if(secondChar.equals("i")) {
            intent.putExtra("path", "/userprofile");
        } else if(secondChar.equals("j")) {
            intent.putExtra("path", "/userprofile/settings");
        } else if(secondChar.equals("n")) {
            intent.putExtra("path", "/userprofile/balance");
        } else if(secondChar.equals("k")) {
            intent.putExtra("path", "/help");
        } else if(secondChar.equals("l")) {
            intent.putExtra("path", "/tellafriend");
        } else if(secondChar.equals("m")) {
            intent.putExtra("path", "/whatsnew");
        } else if(secondChar.equals("o")) {
            intent.putExtra("path", "/bestore");
            intent.putExtra("rewardID", entityId);
        } else if(secondChar.equals("p")) {
            intent.putExtra("path", "/earnbedollars/map");
        } else if(secondChar.equals("r")) {
            //ONLINE Mission is handled different because it hasn't its dedicated screen.
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
        } else if(secondChar.equals("q")) {
            intent.putExtra("path", "/earnbedollars/brands");
            intent.putExtra("brandID", entityId);
            intent.putExtra("missionType", EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE.toString());
        }

        generateAndSendNotification(intent, msg);
    }

    private void generateAndSendNotification(Intent intent, Bundle bundle) {
        PendingIntent contentIntent = PendingIntent.getActivity(GcmIntentService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT); // FLAG_CANCEL_CURRENT is needed due to a bug on KitKat. Other way is added exported = true inside manifest.xml

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(GcmIntentService.this)
                        .setSmallIcon(R.drawable.be_ic_launcher)
                        .setContentTitle(bundle.getString("t"))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bundle.getString("m")))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentText(bundle.getString("m"))
                        .setLights(0xFF00FF00, 1000, 2000)
                        .setVibrate(new long[]{500, 0, 500});


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NotificationID.PUSH_NOTIFICATION.getId(), mBuilder.build());
    }
}