package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IPushNotificationResource;
import com.beintoo.beclubapp.GCMUtils;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RootChecker;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.mobileapptracker.MobileAppTracker;

import java.net.UnknownHostException;

import retrofit.RestAdapter;
import retrofit.client.Response;

public class MemberLogoutTask extends AsyncTask<Void, Integer, Boolean> {

    public interface OnLogoutListener {
        public void onLogoutSucceed();

        public void onLogoutFailed(AsyncTasks.Result errorResult);
    }

    private Context mContext;
    private AsyncTasks.Result statusResult;
    private OnLogoutListener mListener;
    private ProgressDialog mProgressDialog;

    public MemberLogoutTask(Context context, OnLogoutListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mProgressDialog = ProgressDialog.show(context, "Logout", "Please wait...", true, false);
    }

    @Override
    protected Boolean doInBackground(Void... v) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            AuthResource.IAuthResource service = restAdapter.create(AuthResource.IAuthResource.class);
            IPushNotificationResource memberService = restAdapter.create(IPushNotificationResource.class);

            GeoAndDeviceWrapper gaw = new GeoAndDeviceWrapper();
            gaw.setAndroidid(DeviceId.getMACAddress(mContext));
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

            memberService.pushNotifications(MemberAuthStore.getAuth(mContext).getToken(), MemberAuthStore.getMember(mContext).getId(), "REMOVE", gaw);
            GCMUtils.getInstance(mContext).removeRegistrationId(mContext);

            Response response = service.logoutMember(MemberAuthStore.getAuth(mContext).getToken());
            if (response.getStatus() == 200) {
                statusResult = AsyncTasks.Result.OK;
                return true;
            } else {
                statusResult = AsyncTasks.Result.FAILED;
            }

        } catch (ApiException apiEx) {
            apiEx.printStackTrace();
            if (apiEx.getId() == -4) {
                statusResult = AsyncTasks.Result.NOT_AVAILABLE;
            } else if (apiEx.getId() == ApiConfiguration.BANNED_DEVICE_CODE) {
                statusResult = AsyncTasks.Result.BANNED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof UnknownHostException) {
                statusResult = AsyncTasks.Result.NETWORK_ERROR;
            } else {
                statusResult = AsyncTasks.Result.FAILED;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean res) {
        super.onPostExecute(res);

        if (statusResult == AsyncTasks.Result.OK) {
            if (mListener != null) {
                mListener.onLogoutSucceed();
            }
        } else {
            if (mListener != null) {
                mListener.onLogoutFailed(statusResult);
            }
        }

        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
