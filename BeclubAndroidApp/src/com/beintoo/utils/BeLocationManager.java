package com.beintoo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beintoo.beclubapp.R;

import java.util.Date;

public class BeLocationManager {

    private static BeLocationManager instance;

    private LocationManager mLocationManager;

    private Handler mHandler;

    private LocationListener mLocationListener;

    private AlertDialog.Builder dialog;

    private boolean IS_DIALOG_OPENED;

    private BeLocationManager(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mHandler = new Handler();
    }

    public static BeLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new BeLocationManager(context);
        }

        return instance;
    }

    public Location getLastKnowLocation(final Context context) {
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                DebugUtility.showLog("LAST KNOWN LOCATION: " + lastLocation.toString() + " DATE: " + new Date(lastLocation.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.updateLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //office: 45.4569631, 9.1939589
            //manhattan 41.442726,12.392578

            return lastLocation;
        } else {
            if (isNetworkOnline(context)) {
                if (dialog == null) {
                    dialog = new AlertDialog.Builder(context);
                    dialog.setCancelable(false);
                    dialog.setMessage(context.getString(R.string.beclub_require_localization));
                    dialog.setPositiveButton("Take me to location settings", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                            IS_DIALOG_OPENED = false;
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            IS_DIALOG_OPENED = false;
                        }
                    });
                }

                if (!IS_DIALOG_OPENED) {
                    IS_DIALOG_OPENED = true;
                    if(dialog != null) {
                        dialog.show();
                    }
                }
            }
            return null;
        }
    }

    public void updateLocation() {
        //mLocationManager.removeUpdates(mLocationListener);
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mLocationManager.removeUpdates(this);

                DebugUtility.showLog("GOT NEW LOCATION " + location.toString() + " DATE: " + new Date(location.getTime()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

                else
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);*/
            }
        });
    }

    public boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }
}