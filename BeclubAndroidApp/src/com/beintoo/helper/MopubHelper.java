package com.beintoo.helper;

import android.app.Activity;

import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

/**
 * Created by Giulio Bider on 19/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class MopubHelper implements MoPubInterstitial.InterstitialAdListener {

    private static MopubHelper mInstance;

    private MoPubInterstitial mInterstitial;
    private Activity mActivity;

    public static MopubHelper getInstance() {
        if(mInstance == null) {
            mInstance = new MopubHelper();
        }
        return mInstance;
    }

    public void onCreate(Activity activity) {
        try {
            this.mActivity = activity;
            mInterstitial = new MoPubInterstitial(activity, "decd878e1cfa4d5bba7fb1620778acfd");
            mInterstitial.setInterstitialAdListener(this);
            mInterstitial.setKeywords("memberID:" + MemberAuthStore.getMember(activity).getId());
            mInterstitial.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        if (interstitial.isReady()) {
            try {
                interstitial.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {

    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {

    }
}
