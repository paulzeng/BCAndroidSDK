package com.beintoo.asynctasks;


import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;

import java.util.Arrays;

import retrofit.RestAdapter;

public class CollectiveMissionDetailsTask extends AsyncTask<Void, Void, AssignedMissionWrapper>{

    public interface CollectiveMissionDetailCallback{
        public void onComplete(AssignedMissionWrapper assignedMission);
        public void onError();
    }

    private Context mContext;
    private String mBrandId;
    private CollectiveMissionDetailCallback mCallback;

    public CollectiveMissionDetailsTask(Context contex, String brandId, CollectiveMissionDetailCallback callback) {
        mContext = contex;
        mBrandId = brandId;
        mCallback = callback;
    }

    @Override
    protected AssignedMissionWrapper doInBackground(Void... voids) {

        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);
        try {
            PaginatedList<MissionWrapper> missions = missionService.getBrandMissions(MemberAuthStore.getAuth(mContext).getToken(), LocationUtils.getLocationParams(mContext), null, null, Arrays.asList(new String[]{EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE.toString()}), mBrandId, null);
            if(missions != null && missions.getList() != null && missions.getList().size() > 0){
                String missionId = missions.getList().get(0).getMission().getId();

                return missionService.getMissionDetails(MemberAuthStore.getAuth(mContext).getToken(), LocationUtils.getLocationParams(mContext), missionId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(AssignedMissionWrapper ms) {
        super.onPostExecute(ms);
        if(mCallback != null) {
            if (ms != null) {
                mCallback.onComplete(ms);
            }else {
                mCallback.onError();
            }
        }
    }


}
