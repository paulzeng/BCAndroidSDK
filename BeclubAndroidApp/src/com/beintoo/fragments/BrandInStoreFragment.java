package com.beintoo.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.beintoo.activities.CardsMissionActivity;
import com.beintoo.activities.CollectiveMissionActivity;
import com.beintoo.activities.MapStoresActivity;
import com.beintoo.activities.MultipleScanStoresActivity;
import com.beintoo.activities.OnlineMissionActivity;
import com.beintoo.activities.ScanMissionActivity;
import com.beintoo.activities.TakeAPictureActivity;
import com.beintoo.activities.WalkinMissionsListActivity;
import com.beintoo.adapters.BrandMissionAdapter;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.FraudManager;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 03/09/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class BrandInStoreFragment extends Fragment {

    private EarnBedollarsWrapper mMission;
    private ListView mList;
    private View mView;

    public static BrandInStoreFragment newInstance(EarnBedollarsWrapper mission) {
        BrandInStoreFragment fragment = new BrandInStoreFragment();
        fragment.mMission = mission;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.brand_in_store_fragment, container, false);
        mList = (ListView) mView.findViewById(R.id.listView);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mMission = new Gson().fromJson(savedInstanceState.getString("mission"), EarnBedollarsWrapper.class);
        }

        // Remove null elements from enabled missions
        final List<EarnBedollarsWrapper.MissionActionTypeEnum> enabledMissions = mMission.getSubtype();
        enabledMissions.removeAll(Collections.singleton(null));
        enabledMissions.removeAll(Collections.singleton(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE));

        // Remove null elements from enabled missions and remove balance types (REWARDED, BEPOWERED, ecc) in order to obain a list with only mission types
        List<EarnBedollarsWrapper.MissionActionTypeEnum> availableMissions = new ArrayList<EarnBedollarsWrapper.MissionActionTypeEnum>();
        if(mMission.getBrands().getSponsoredactiontype() != null) {
            availableMissions = mMission.getBrands().getSponsoredactiontype();
            availableMissions.removeAll(Collections.singleton(null));
            availableMissions.removeAll(EarnBedollarsWrapper.getBalanceTypes());
            availableMissions.removeAll(Collections.singleton(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE));
        } else {
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE);
            availableMissions.add(EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE);
        }

        for(EarnBedollarsWrapper.MissionActionTypeEnum type : enabledMissions) {
            if(!availableMissions.contains(type)) {
                availableMissions.add(type);
            }
        }

        // Adding footer view with Participating Stores button
        if (!mMission.getBrands().isOnlyonline()) {
            View participatingView = getLayoutInflater(savedInstanceState).inflate(R.layout.all_participant_stores, mList, false);
            participatingView.findViewById(R.id.rl_all_participant_store).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapStoresActivity.class);
                    intent.putExtra("brand", mMission.getBrands().getId());
                    getActivity().startActivity(intent);

                    }
            });
            mList.addFooterView(participatingView, null, false);
        }

        mList.setAdapter(new BrandMissionAdapter(getActivity(), R.layout.brand_mission_row, availableMissions, enabledMissions));

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EarnBedollarsWrapper.MissionActionTypeEnum type = (EarnBedollarsWrapper.MissionActionTypeEnum) adapterView.getItemAtPosition(i);

                if(!enabledMissions.contains(type)) {
                    return;
                }

                Intent intent;
                if(type == EarnBedollarsWrapper.MissionActionTypeEnum.SCAN)
                    intent = new Intent(getActivity(), ScanMissionActivity.class);
                else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE)
                    intent = new Intent(getActivity(), OnlineMissionActivity.class);
                else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN)
                    intent = new Intent(getActivity(), MultipleScanStoresActivity.class);
                else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING)
                    intent = new Intent(getActivity(), CardsMissionActivity.class);
                else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE)
                    intent = new Intent(getActivity(), CollectiveMissionActivity.class);
                else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE)
                    intent = new Intent(getActivity(), TakeAPictureActivity.class);
                else
                    intent = new Intent(getActivity(), WalkinMissionsListActivity.class);

                intent.putExtra("brand_name", mMission.getBrands().getName());
                intent.putExtra("brand_id", mMission.getBrands().getId());
                intent.putExtra("mission_type", type.toString());

                getActivity().startActivityForResult(intent, FraudManager.BANNED_ACTIVITY_RESULT);

                  }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mission", mMission.toString());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }
}
