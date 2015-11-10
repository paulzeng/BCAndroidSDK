package com.beintoo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.AlarmsReceiver;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.MemberSettingsBean;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingsPushNotificationsActivity extends BeNotificationActivity {

    private CheckBox cbLocationBased;
    private CheckBox cbBeclubUpdates;
    private CheckBox cbUserHighlights;
    private CheckBox cbBedollarsEarn;
    private RelativeLayout rlUpdateSettings;

    private MemberSettingsBean mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_push_notifications_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        cbLocationBased = (CheckBox) findViewById(R.id.cb_location_based);
        cbBeclubUpdates = (CheckBox) findViewById(R.id.cb_beclub_updates);
        cbUserHighlights = (CheckBox) findViewById(R.id.cb_user_highlights);
        cbBedollarsEarn = (CheckBox) findViewById(R.id.cb_bedollars_earn);
        rlUpdateSettings = (RelativeLayout) findViewById(R.id.update_settings);

        MemberBean member = MemberAuthStore.getMember(this);
        if (member != null) {
            List<MemberSettingsBean> settingsList = member.getMemberssettings();
            if (settingsList != null && settingsList.size() > 0) {
                mSettings = settingsList.get(0);
                cbLocationBased.setChecked(mSettings.getLocationbasedmessages());
                cbBeclubUpdates.setChecked(mSettings.getBeclubnews());
                cbUserHighlights.setChecked(mSettings.getUserhighlights());
                cbBedollarsEarn.setChecked(mSettings.getPurchasemissionsalert());
            } else {
                resetToDefault();
            }
        } else {
            resetToDefault();
        }

        rlUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMemberSettingsBean();

                RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                IMemberResource memberService = restAdapter.create(IMemberResource.class);

                final ProgressDialog dialog = ProgressDialog.show(SettingsPushNotificationsActivity.this, "Update", "Please wait...", true, false);
                memberService.updateMemberSettings(MemberAuthStore.getAuth(SettingsPushNotificationsActivity.this).getToken(), MemberAuthStore.getMember(SettingsPushNotificationsActivity.this).getId(), mSettings, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        // After push notification update is completed in backend I update local settings
                        MemberBean member = MemberAuthStore.getMember(SettingsPushNotificationsActivity.this);
                        ArrayList<MemberSettingsBean> settingsBeans = new ArrayList<MemberSettingsBean>();
                        settingsBeans.add(mSettings);
                        member.setMemberssettings(settingsBeans);
                        MemberAuthStore.setMember(SettingsPushNotificationsActivity.this, member);

                        if (mSettings.getLocationbasedmessages()) { // Wake up Gimbal service if settings is enable.
                            Intent intent = new Intent(SettingsPushNotificationsActivity.this, AlarmsReceiver.class);
                            intent.putExtra("notificationID", NotificationID.WAKE_UP_GIMBAL_SDK.getId());
                            sendBroadcast(intent);
                        }
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        if (error.isNetworkError()) {
                            Toast.makeText(SettingsPushNotificationsActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SettingsPushNotificationsActivity.this, getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void resetToDefault() {
        mSettings = new MemberSettingsBean();
        cbLocationBased.setChecked(true);
        cbBeclubUpdates.setChecked(true);
        cbUserHighlights.setChecked(true);
        cbBedollarsEarn.setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMemberSettingsBean() {
        if (mSettings == null) {
            resetToDefault();
        }
        mSettings.setBeclubnews(cbBeclubUpdates.isChecked());
        mSettings.setPurchasemissionsalert(cbBedollarsEarn.isChecked());
        mSettings.setLocationbasedmessages(cbLocationBased.isChecked());
        mSettings.setUserhighlights(cbUserHighlights.isChecked());
    }
}
