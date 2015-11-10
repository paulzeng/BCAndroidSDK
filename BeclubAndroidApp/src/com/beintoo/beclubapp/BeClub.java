package com.beintoo.beclubapp;

import android.support.multidex.MultiDexApplication;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.utils.BeLocationManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class BeClub extends MultiDexApplication {

    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        BeLocationManager.getInstance(getApplicationContext()).updateLocation();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        ApiConfiguration.setEnvironment(ApiConfiguration.Environment.DEVELOPMENT);
        ApiConfiguration.setAppVersion(this);

        mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.analytics);
        mTracker.enableAdvertisingIdCollection(true); 
    }

    public Tracker getTracker() {
        if(mTracker == null) {
            mTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.analytics);
        }
        return mTracker;
    }
}