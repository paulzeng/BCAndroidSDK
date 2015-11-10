package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;

import java.net.UnknownHostException;

import retrofit.RestAdapter;

public class MissionCompleteTask extends AsyncTask<String, Void, AssignedMissionWrapper> {

    private ProgressDialog mProgressDialog;
    private Context mContext;

    private AsyncTasks.Result mResult;
    private Location mLocation;

    public interface MissionCompleteCallback {
        public void onCompleted(AssignedMissionWrapper results);

        public void onFailed(AsyncTasks.Result error);

        public void onPopupClosed();
    }

    final private MissionCompleteCallback mCallback;

    public MissionCompleteTask(Context context, Location location, MissionCompleteCallback callback) {
        mContext = context;
        mCallback = callback;
        mLocation = location;
        mProgressDialog = ProgressDialog.show(context, "Complete", "Please wait...", true, false);
    }

    @Override
    protected AssignedMissionWrapper doInBackground(String... start) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

            AssignedMissionWrapper reward = missionService.completeMission(MemberAuthStore.getAuth(mContext).getToken(), start[0], start[1], GeoAndDeviceWrapper.build(mContext, mLocation));

            new MeTask((android.app.Activity) mContext).execute();
            mResult = AsyncTasks.Result.OK;

            return reward;
        } catch (ApiException apiEx) {
            apiEx.printStackTrace();
            if (apiEx.getId() == -4) {
                mResult = AsyncTasks.Result.NOT_AVAILABLE;
            } else if (apiEx.getId() == ApiConfiguration.BANNED_DEVICE_CODE) {
                mResult = AsyncTasks.Result.BANNED;
            } else if (apiEx.getId() == ApiConfiguration.EMAIL_NOT_VALID_CODE) {
                mResult = AsyncTasks.Result.OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof UnknownHostException) {
                mResult = AsyncTasks.Result.NETWORK_ERROR;
            } else {
                mResult = AsyncTasks.Result.FAILED;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(AssignedMissionWrapper results) {
        super.onPostExecute(results);

        DebugUtility.showLog("MISSION RESULT " + mResult);

        if (results == null) {
            if (mCallback != null)
                mCallback.onFailed(mResult);
        } else {
            if (mCallback != null)
                mCallback.onCompleted(results);
        }

        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}