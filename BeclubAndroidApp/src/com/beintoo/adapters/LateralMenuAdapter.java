package com.beintoo.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.fragments.BestoreFragmentV2;
import com.beintoo.fragments.EarnBedollarsTabFragmentV2;
import com.beintoo.fragments.ProfileFragmentV3;
import com.beintoo.utils.ui.CircleImageView;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MemberBean;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LateralMenuAdapter extends BaseAdapter {

    private Context mContext;
    private Fragment mFragment;

    public LateralMenuAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(i == 0){
            try {
                MemberBean me = MemberAuthStore.getMember(mContext);

                view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_profile_row, viewGroup, false);

                if(me.getImagesmall() != null)
                    ImageLoader.getInstance().displayImage(me.getImagesmall(), ((CircleImageView) view.findViewById(R.id.user_img)));
                else
                    ((CircleImageView) view.findViewById(R.id.user_img)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile));

                ((TextView)view.findViewById(R.id.user_name_lastname)).setText(me.getName() + " "+ me.getSurname());
                ((TextView)view.findViewById(R.id.user_bedollars)).setText(""+me.getBedollars().intValue());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(i == 2){
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            TextView tvBestore = (TextView)view.findViewById(R.id.sectionName);
            if(mFragment != null && mFragment instanceof BestoreFragmentV2) {
                ((ImageView)view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bestore));
                tvBestore.setText(mContext.getResources().getString(R.string.lateral_menu_bestore));
                tvBestore.setTextColor(mContext.getResources().getColor(R.color.b_green));
            } else {
                ((ImageView)view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.bestore_grey));
                tvBestore.setText(mContext.getResources().getString(R.string.lateral_menu_bestore));
                tvBestore.setTextColor(mContext.getResources().getColor(R.color.b_menu_gray));
            }
        }else if(i == 1){
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            TextView tvEarn = (TextView)view.findViewById(R.id.sectionName);
            if(mFragment != null && mFragment instanceof EarnBedollarsTabFragmentV2) {
                ((ImageView)view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.earn));
                tvEarn.setText(mContext.getResources().getString(R.string.lateral_menu_earn_bedollars));
                tvEarn.setTextColor(mContext.getResources().getColor(R.color.b_green));
            } else {
                ((ImageView)view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.earn_grey));
                tvEarn.setText(mContext.getResources().getString(R.string.lateral_menu_earn_bedollars));
                tvEarn.setTextColor(mContext.getResources().getColor(R.color.b_menu_gray));
            }
        } else if(i == 3) {
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            TextView tvWallet = (TextView)view.findViewById(R.id.sectionName);
            if(mFragment != null && mFragment instanceof ProfileFragmentV3) {
                ((ImageView) view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.wallet));
                tvWallet.setText(mContext.getResources().getString(R.string.lateral_menu_wallet));
                tvWallet.setTextColor(mContext.getResources().getColor(R.color.b_green));
            } else {
                ((ImageView) view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.wallet_grey));
                tvWallet.setText(mContext.getResources().getString(R.string.lateral_menu_wallet));
                tvWallet.setTextColor(mContext.getResources().getColor(R.color.b_menu_gray));
            }
        } else if(i == 5){
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            ((TextView)view.findViewById(R.id.sectionName)).setText(mContext.getResources().getString(R.string.action_settings));
            ((ImageView) view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.settings_grey));
        } else if(i == 6){
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            ((TextView)view.findViewById(R.id.sectionName)).setText(mContext.getResources().getString(R.string.action_help));
            ((ImageView) view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.help_grey));
        } else if(i == 4){
            view = LayoutInflater.from(mContext).inflate(R.layout.lateral_menu_row, viewGroup, false);
            ((TextView)view.findViewById(R.id.sectionName)).setText(mContext.getResources().getString(R.string.tell_a_friend));
            ((ImageView) view.findViewById(R.id.sectionIcon)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.share_grey));
        }

        return view;
    }

    public void setCurrentFragment(Fragment mCurrentFragment) {
        this.mFragment = mCurrentFragment;
    }
}