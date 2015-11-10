package com.beintoo.asynctasks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.beintoo.activities.MemberEntryActivity;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RootChecker;
import com.beintoo.utils.TokenExpiredException;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.MeAndAuthWrapper;
import com.beintoo.wrappers.MeBean;
import com.mobileapptracker.MobileAppTracker;

import retrofit.RestAdapter;
import retrofit.client.Response;

public class MeTask extends AsyncTask<Void, Void, MeAndAuthWrapper> {

    public interface MeCallback{
        public void onComplete(MeAndAuthWrapper meAndAuth);
        public void onError(AsyncTasks.Result result);
    }

    private Activity mActivity;
    private AsyncTasks.Result statusResult;
    private MeCallback mCallback;

    public MeTask(Activity activty){
        mActivity = activty;
    }

    public MeTask(Activity activty, MeCallback callback){
        mActivity = activty;
        mCallback = callback;
    }

    @Override
    protected MeAndAuthWrapper doInBackground(Void... data) {
        try {
            DebugUtility.showLog("UPDATING USER");
            AuthResource ar = new AuthResource();
            AuthBean auth = MemberAuthStore.getAuth(mActivity);
            MeBean meBean = ar.me(auth.getToken());
            DebugUtility.showLog("MEMBER "+meBean.getMember() );

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
            boolean justDoIt = preferences.getBoolean("onappupdate", false);
            if(!justDoIt) {

                GeoAndDeviceWrapper gaw = new GeoAndDeviceWrapper();
                gaw.setAndroidid(DeviceId.getMACAddress(mActivity));

                try {
                    Location location = BeLocationManager.getInstance(mActivity).getLastKnowLocation(mActivity);
                    if (location != null) {
                        gaw.setLatitude(location.getLatitude());
                        gaw.setLongitude(location.getLongitude());
                        gaw.setAltitude(location.getAltitude());
                        gaw.setHaccuracy((double) location.getAccuracy());
                    }
                    gaw.setImei(DeviceId.getImei(mActivity));
                    gaw.setMacaddress(DeviceId.getMACAddress(mActivity));
                    gaw.setMatid(MobileAppTracker.getInstance().getMatId());
                    gaw.setGoogleadvid(MemberAuthStore.advertiseID);
                    gaw.setGoogleadvidenabled(MemberAuthStore.advertiseDisable);
                    gaw.setDeviceunlocked(RootChecker.isRooted());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                AuthResource.IAuthResource service = restAdapter.create(AuthResource.IAuthResource.class);

                Response response = service.onAppUpdate(auth.getToken(), gaw);
                if(response.getStatus() == 204) {
                    preferences.edit().putBoolean("onappupdate", true).apply();
                }
            }

            if(meBean.getMember() != null){
                MemberAuthStore.setMember(mActivity, meBean.getMember());
                statusResult = AsyncTasks.Result.OK;

                return new MeAndAuthWrapper(meBean, auth);
            }else{
                DebugUtility.showLog("FAILED");
                statusResult = AsyncTasks.Result.FAILED;
            }
        }catch (TokenExpiredException expire){
            expire.printStackTrace();
            statusResult = AsyncTasks.Result.EXPIRED_TOKEN;
            DebugUtility.showLog("TOKEN EXPIRED");
        }catch (Exception e){
            e.printStackTrace();
            statusResult = AsyncTasks.Result.FAILED;
            DebugUtility.showLog("EXCEPTION");
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(MeAndAuthWrapper meAndAuth) {
        super.onPostExecute(meAndAuth);
        DebugUtility.showLog("RESULT "+statusResult);
        if(statusResult == AsyncTasks.Result.EXPIRED_TOKEN){
            DebugUtility.showLog("TOKEN IS EXPIRED");
            mActivity.finish();
            Intent intent = new Intent(mActivity, MemberEntryActivity.class);
            mActivity.startActivity(intent);
            MemberAuthStore.logoutMember(mActivity);
        }

        if(mCallback != null){
            if(statusResult == AsyncTasks.Result.OK)
                mCallback.onComplete(meAndAuth);
            else
                mCallback.onError(statusResult);
        }
    }
}
