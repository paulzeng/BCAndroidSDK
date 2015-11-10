package com.beintoo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.activities.RewardActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.ProductWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class ScanMissionAdapter extends ArrayAdapter<MissionWrapper> {

    private Context mContext;
    private List<MissionWrapper> mMissionsList;

    public ScanMissionAdapter(Context context, int textViewResourceId,
                              List<MissionWrapper> missionsList) {
        super(context, textViewResourceId, missionsList);
        mContext = context;

        mMissionsList = missionsList;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.scan_mission_row, parent, false);
        try {

            TextView productName = ((TextView) convertView.findViewById(R.id.scan_product_name));
            TextView scanAmount = ((TextView) convertView.findViewById(R.id.scan_amount));
            ImageView productImage = ((ImageView) convertView.findViewById(R.id.scan_product_image));


            final MissionWrapper mission = getItem(position);
            ProductWrapper product = mission.getMission().getActioncheck().getProduct();
            productName.setText(product.getName());

            if(mission.getMinutestoredo() != null && (mission.getMinutestoredo() > 0 || mission.getMinutestoredo() == -1)){
                convertView.findViewById(R.id.mission_price_container).setVisibility(View.GONE);
                convertView.findViewById(R.id.scanned_container).setVisibility(View.VISIBLE);
                mission.setEnabled(false);
            }else{
                scanAmount.setText("+"+mission.getMission().getBedollars().intValue());
            }

            if(mission.getReward() != null){
                convertView.findViewById(R.id.mission_reward_container).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.scan_text).setVisibility(View.GONE);
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

            ImageLoader.getInstance().displayImage(product.getImagebig(), productImage);

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }
}
