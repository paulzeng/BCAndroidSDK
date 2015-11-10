package com.beintoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.EarnBedollarsWrapper;

import java.util.List;

public class BrandMissionAdapter extends ArrayAdapter<EarnBedollarsWrapper.MissionActionTypeEnum> {

    private List<EarnBedollarsWrapper.MissionActionTypeEnum> mEnabledMissions;
    private Context mContext;

    public BrandMissionAdapter(Context context, int resource, List<EarnBedollarsWrapper.MissionActionTypeEnum> availableMissions, List<EarnBedollarsWrapper.MissionActionTypeEnum> enabledMissions) {
        super(context, resource, availableMissions);
        this.mContext = context;
        this.mEnabledMissions = enabledMissions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.brand_mission_row, parent, false);
        TextView missionName = ((TextView) convertView.findViewById(R.id.brand_mission_name));
        TextView missionDesc = ((TextView) convertView.findViewById(R.id.brand_mission_desc));
        ImageView missionIcon = ((ImageView) convertView.findViewById(R.id.brand_mission_icon));

        EarnBedollarsWrapper.MissionActionTypeEnum type = getItem(position);

        if(type == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN || type == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME){
            missionName.setText(mContext.getString(R.string.earn_bedollars_walkin_anytime_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_walkin_desc));
            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN) || mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.walk_mission_2));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.walk_mission_1_grey));
                disableRow(missionName, missionDesc);
            }

        }else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.SCAN){
            missionName.setText(mContext.getString(R.string.earn_bedollars_scanning_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_scanning_desc));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scan_mission_1));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scan_mission_1_grey));
                disableRow(missionName, missionDesc);
            }

        }else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN){
            missionName.setText(mContext.getString(R.string.earn_bedollars_multiplescanning_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_multiplescanning_desc));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scan_mission_2));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.scan_mission_2_grey));
                disableRow(missionName, missionDesc);
            }
        }else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE){
            missionName.setText(mContext.getString(R.string.earn_bedollars_online_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_online_subtitle));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.web_icon));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.web_icon_gray_big));
                disableRow(missionName, missionDesc);
            }

        }else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE){
            missionName.setText(mContext.getString(R.string.collective_mission_title));
            missionDesc.setText(mContext.getString(R.string.collective_mission_subtitle));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.COLLECTIVE)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.collective));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.collective_grey));
                disableRow(missionName, missionDesc);
            }
        } else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING) {
            missionName.setText(mContext.getString(R.string.earn_bedollars_cardspring_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_cardspring_desc));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card_green));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card_grey));
                disableRow(missionName, missionDesc);
            }
        } else if(type == EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE) {
            missionName.setText(mContext.getString(R.string.earn_bedollars_takeapicture_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_takeapicture_desc));

            if(mEnabledMissions.contains(EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE)){
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_picture_camera_green));
            }else{
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_picture_camera_grey));
                disableRow(missionName, missionDesc);
            }
        }

        return convertView;
    }

    private void disableRow(TextView missionName, TextView missionDesc){
        missionName.setTextColor(mContext.getResources().getColor(R.color.b_sections_header_text));
        missionDesc.setTextColor(mContext.getResources().getColor(R.color.b_sections_header_text));
    }
}
