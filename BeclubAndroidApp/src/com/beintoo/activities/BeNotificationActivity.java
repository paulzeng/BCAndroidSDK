package com.beintoo.activities;

import android.support.v4.app.FragmentActivity;

import com.beintoo.utils.BeUtils;
import com.beintoo.utils.notification.BeNotificationManager;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BeNotificationActivity extends FragmentActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BeNotificationManager.getInstance(this).onResume(this);
        BeUtils.isMockLocationEnabled(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Crouton.clearCroutonsForActivity(this);
        BeNotificationManager.getInstance(this).onStop();
    }

    @Override
    protected void onDestroy() {
        Crouton.clearCroutonsForActivity(this);
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
}
