package com.beintoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.activities.RewardActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.TimingWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MissionTypeAdapter extends ArrayAdapter<MissionWrapper> {

    private Context mContext;
    private List<MissionWrapper> mMissionsList;
    private SimpleDateFormat dateFormatter;

    public MissionTypeAdapter(Context context, int textViewResourceId,
                               List<MissionWrapper> missionsList) {
        super(context, textViewResourceId, missionsList);
        mContext = context;

        mMissionsList = missionsList;
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public MissionWrapper getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return drawMissions(convertView, parent, position);
    }

    private View drawMissions(View convertView, ViewGroup parent, int position){
        convertView = LayoutInflater.from(mContext).inflate(R.layout.brand_mission_row, parent, false);
        convertView.findViewById(R.id.mission_bedollars_amount_container).setVisibility(View.VISIBLE);

        TextView missionName = ((TextView) convertView.findViewById(R.id.brand_mission_name));
        TextView missionDesc = ((TextView) convertView.findViewById(R.id.brand_mission_desc));
        TextView missionBedollarsAmount = ((TextView) convertView.findViewById(R.id.brand_mission_amount));
        ImageView missionIcon = ((ImageView) convertView.findViewById(R.id.brand_mission_icon));

        missionIcon.setVisibility(View.GONE);
        //TextView missionAmount = ((TextView) convertView.findViewById(R.id.brand_mission_amount));

        final MissionWrapper mission = getItem(position);

        if(mission.getMission().getActioncheck().getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN
        		|| mission.getMission().getActioncheck().getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME){
            try {
                TextView checkInTime = ((TextView) convertView.findViewById(R.id.line_3));
                TextView missionStatus = ((TextView) convertView.findViewById(R.id.line_4));
                TimingWrapper timing = mission.getMission().getActioncheck().getTiming();
                
                /*
                if(timing != null && timing.getDatetimingfrom() != null && timing.getDatetimingto() != null){
                    Date from = dateFormatter.parse(timing.getDatetimingfrom());
                    Date to = dateFormatter.parse(timing.getDatetimingto());

                    String dateString = new SimpleDateFormat("MM-dd-yyyy").format(from) + " between " + new SimpleDateFormat("HH:mm").format(from) + " - " + new SimpleDateFormat("HH:mm").format(to);
                    missionName.setText(mContext.getString(R.string.earn_bedollars_walkin_specialday_title));
                    missionDesc.setText(mContext.getString(R.string.earn_bedollars_walkin_specialday_desc));
                    missionDesc.setPadding(0,0,0,0);

                    checkInTime.setText(dateString);


                    // check if the time is ok for checkin
                    Date now = new Date();
                    if(!(now.after(from) && now.before(to))){
                        ((ImageView) convertView.findViewById(R.id.bed_icon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bedollar_grey));
                        missionBedollarsAmount.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        missionName.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        getItem(position).setEnabled(false);
                    }else if(now.after(to)){
                        ((ImageView) convertView.findViewById(R.id.bed_icon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bedollar_grey));
                        missionBedollarsAmount.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        missionName.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        getItem(position).setEnabled(false);
                    }

                }*/
                
                if(mission.getMission().getActioncheck().getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME){
                    Date from = dateFormatter.parse(timing.getDatetimingfrom());
                    Date to = dateFormatter.parse(timing.getDatetimingto());

                    String dateString = new SimpleDateFormat("MM-dd-yyyy").format(from) + " between " + new SimpleDateFormat("HH:mm").format(from) + " - " + new SimpleDateFormat("HH:mm").format(to);
                    missionName.setText(mContext.getString(R.string.earn_bedollars_walkin_specialday_title));
                    missionDesc.setText(mContext.getString(R.string.earn_bedollars_walkin_specialday_desc));
                    missionDesc.setPadding(0,0,0,0);

                    checkInTime.setText(dateString);


                    // check if the time is ok for checkin
                    Date now = new Date();
                    if(!(now.after(from) && now.before(to))){
                        ((ImageView) convertView.findViewById(R.id.bed_icon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bedollar_grey));
                        missionBedollarsAmount.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        missionName.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        getItem(position).setEnabled(false);
                    }else if(now.after(to)){
                        ((ImageView) convertView.findViewById(R.id.bed_icon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bedollar_grey));
                        missionBedollarsAmount.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        missionName.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                        getItem(position).setEnabled(false);
                    }

                    checkInTime.setVisibility(View.VISIBLE);
                }else{
                    missionName.setText(mContext.getString(R.string.earn_bedollars_walkin_anytime_title));
                    missionDesc.setText(mContext.getString(R.string.earn_bedollars_walkin_anytime_desc));
                    //missionDesc.setPadding(0,0,0,0);
                    checkInTime.setVisibility(View.GONE);
                }
                missionBedollarsAmount.setText(""+mission.getMission().getBedollars().intValue());
                missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.piedini_green));

                if(!mission.isEnabled() || mission.getMinutestoredo() > 0 || mission.getMinutestoredo() == -1){
                    missionStatus.setTextColor(mContext.getResources().getColor(R.color.b_green));
                    missionStatus.setTypeface(null, Typeface.BOLD);
                    missionStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    missionStatus.setText(mContext.getString(R.string.earn_bedollars_walkin_done));
                    missionStatus.setVisibility(View.VISIBLE);

                    ((ImageView) convertView.findViewById(R.id.bed_icon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bedollar_grey));
                    missionBedollarsAmount.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));
                    missionName.setTextColor(mContext.getResources().getColor(R.color.b_gray_texts));

                    getItem(position).setEnabled(false);
                }else{
                    checkInTime.setPadding(0,0,0, 8);
                }

                if(mission.getReward() != null){
                    convertView.findViewById(R.id.mission_reward_container).setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(mission.getReward().getImagesmall(), (ImageView)convertView.findViewById(R.id.view_reward_details));
                    convertView.findViewById(R.id.mission_bedollars_amount_container).setVisibility(View.GONE);
                    ((TextView) convertView.findViewById(R.id.mission_reward_amount)).setText("+"+mission.getMission().getBedollars().intValue());
                    convertView.findViewById(R.id.view_reward_details).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, RewardActivity.class);
                            intent.putExtra("reward", mission.getReward().toString());
                            intent.putExtra("is_detail_from_mission", true);
                            mContext.startActivity(intent);
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }else if(mission.getMission().getActioncheck().getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.SCAN){
            missionName.setText(mContext.getString(R.string.earn_bedollars_scanning_title));
            missionDesc.setText(mContext.getString(R.string.earn_bedollars_scanning_desc));
            missionBedollarsAmount.setText(""+mission.getMission().getBedollars().intValue());
            missionIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.code_icon));
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }
}
