package com.beintoo.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.ILogResource;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.api.IRewardResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RedirectingScreenUtils;
import com.beintoo.utils.RootChecker;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.LogAppBean;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.RewardWrapper;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmptyLoadingActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_activity);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (args.getString("rewardID") != null) {
                String rewardID = args.getString("rewardID");

                RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                IRewardResource rewardService = restAdapter.create(IRewardResource.class);

                Location location = BeLocationManager.getInstance(this).getLastKnowLocation(this);
                Double latitude = null;
                Double longitude = null;
                Double alt = null;
                Float ha = null;
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    alt = location.getAltitude();
                    ha = location.getAccuracy();
                }
                rewardService.getReward(MemberAuthStore.getAuth(this).getToken(), rewardID, latitude, longitude, alt, ha, new Callback<RewardWrapper>() {
                    @Override
                    public void success(RewardWrapper rewardWrapper, Response response) {
                        try {
                            String rewardJson = new Gson().toJson(rewardWrapper);
                            Intent intent = new Intent(EmptyLoadingActivity.this, RewardActivity.class);
                            intent.putExtra("reward", rewardJson);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        finish();
                    }
                });
            } else if (args.getString("brandID") != null) {
                String brandID = args.getString("brandID");
                if (args.getString("missionType") != null && !args.getString("missionType").equals(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE.toString())) {
                    try {
                        String missionType = args.getString("missionType");
                        Intent intent = RedirectingScreenUtils.redirectingFromParams(this, missionType, brandID);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                } else {
                    RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                    IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

                    missionService.getBrands(MemberAuthStore.getAuth(this).getToken(), LocationUtils.getLocationParams(this), true, null, 10, EarnBedollarsWrapper.getSubTypesAvailable(this), brandID, new Callback<PaginatedList<EarnBedollarsWrapper>>() {
                        @Override
                        public void success(PaginatedList<EarnBedollarsWrapper> earnBedollarsWrapperPaginatedList, Response response) {
                            try {
                                if (earnBedollarsWrapperPaginatedList != null && earnBedollarsWrapperPaginatedList.getList() != null && earnBedollarsWrapperPaginatedList.getList().size() > 0) {
                                    EarnBedollarsWrapper wrapper = earnBedollarsWrapperPaginatedList.getList().get(0);
                                    String wrapperJson = new Gson().toJson(wrapper);
                                    Intent intent = new Intent(EmptyLoadingActivity.this, BrandMissionActivity.class);
                                    intent.putExtra("missions", wrapperJson);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            finish();
                        }
                    });
                }
            } else if (args.getString("missionID") != null) {
                final String missionID = args.getString("missionID");
                try {

                    RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                    IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

                    missionService.getMission(MemberAuthStore.getAuth(this).getToken(), LocationUtils.getLocationParams(this), missionID, null, null, new Callback<MissionWrapper>() {
                        @Override
                        public void success(MissionWrapper mission, Response response) {
                            try {
                                if (mission != null && mission.getMission() != null) {
                                    if (mission.getMission().getActioncheck().getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE) { // Se la mission è di tipo COLLECTIVE chiamo la MissionDetails.
                                        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                                        IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

                                        missionService.getMissionDetails(MemberAuthStore.getAuth(EmptyLoadingActivity.this).getToken(), LocationUtils.getLocationParams(EmptyLoadingActivity.this), missionID, new Callback<AssignedMissionWrapper>() {
                                            @Override
                                            public void success(AssignedMissionWrapper assignedMission, Response response) {
                                                try {
                                                    String missionType = assignedMission.getMissionswrapper().getMission().getActioncheck().getSubtype().toString();
                                                    Intent intent = RedirectingScreenUtils.redirectingFromParams(EmptyLoadingActivity.this, missionType, assignedMission.getMissionswrapper().getMission().getBrandsid());
                                                    startActivity(intent);
                                                    finish();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                finish();
                                            }
                                        });
                                    } else {
                                        try {
                                            String missionType = mission.getMission().getActioncheck().getSubtype().toString();
                                            Intent intent = RedirectingScreenUtils.redirectingFromParams(EmptyLoadingActivity.this, missionType, mission.getMission().getBrandsid());
                                            startActivity(intent);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            finish();
                                        }
                                    }
                                } else {
                                    RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                                    ILogResource service = restAdapter.create(ILogResource.class);
                                    service.sendLogReport(MemberAuthStore.getAuth(EmptyLoadingActivity.this).getToken(), buildLogAppBean(), new Callback<Object>() {
                                        @Override
                                        public void success(Object o, Response response) {
                                            finish();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            finish();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                                ILogResource service = restAdapter.create(ILogResource.class);
                                service.sendLogReport(MemberAuthStore.getAuth(EmptyLoadingActivity.this).getToken(), buildLogAppBean(), new Callback<Object>() {
                                    @Override
                                    public void success(Object o, Response response) {
                                        finish();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        finish();
                                    }
                                });
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }
    }

    private LogAppBean buildLogAppBean() {
        LogAppBean logAppBean = new LogAppBean();
        logAppBean.setMessage("Potential member borderline");
        logAppBean.setStatusCode(null);
        logAppBean.setStackTrace(null);
        logAppBean.setEventType("");
        logAppBean.setMemberId(MemberAuthStore.getMember(EmptyLoadingActivity.this).getId());

        GeoAndDeviceWrapper gaw = new GeoAndDeviceWrapper();
        gaw.setAndroidid(DeviceId.getMACAddress(EmptyLoadingActivity.this));

        try {
            Location location = BeLocationManager.getInstance(EmptyLoadingActivity.this).getLastKnowLocation(EmptyLoadingActivity.this);
            if (location != null) {
                gaw.setLatitude(location.getLatitude());
                gaw.setLongitude(location.getLongitude());
                gaw.setAltitude(location.getAltitude());
                gaw.setHaccuracy((double) location.getAccuracy());
            }
            gaw.setImei(DeviceId.getImei(EmptyLoadingActivity.this));
            gaw.setMacaddress(DeviceId.getMACAddress(EmptyLoadingActivity.this));
            gaw.setMatid(MobileAppTracker.getInstance().getMatId());
            gaw.setGoogleadvid(MemberAuthStore.advertiseID);
            gaw.setGoogleadvidenabled(MemberAuthStore.advertiseDisable);
            gaw.setDeviceunlocked(RootChecker.isRooted());
        } catch (Exception e) {
            e.printStackTrace();
        }

        logAppBean.setGeoAndDeviceWrapper(gaw);
        return logAppBean;
    }
}
