package com.beintoo.utils;

import android.content.Context;
import android.content.Intent;

import com.beintoo.activities.CollectiveMissionActivity;
import com.beintoo.activities.MultipleScanStoresActivity;
import com.beintoo.activities.ScanMissionActivity;
import com.beintoo.activities.TakeAPictureActivity;
import com.beintoo.activities.WalkinMissionsListActivity;
import com.beintoo.beclubapp.MainActivity;
import com.beintoo.wrappers.EarnBedollarsWrapper;

public class RedirectingScreenUtils {

    public static Intent redirectingFromParams(Context context, String missionType, String brandID) {
        Intent intent = new Intent(context, MainActivity.class);
        if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE.toString())) {
            intent = CollectiveMissionActivity.getCollectiveMissionActivityIntent(context, brandID);
        } else if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN.toString())) {
            intent = ScanMissionActivity.getScanMissionActivityIntent(context, brandID, missionType);
        } else if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN.toString()) || missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME.toString())) {
            intent = WalkinMissionsListActivity.getWalkinMissionListActivity(context, brandID, EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN.toString());
        } else if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN.toString())) {
            intent = MultipleScanStoresActivity.getMultipleScanStoresActivityIntent(context, brandID);
        } else if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE.toString())) {
            intent = TakeAPictureActivity.getTakeAPictureActivityIntent(context, brandID);
        }

        return intent;
    }
}
