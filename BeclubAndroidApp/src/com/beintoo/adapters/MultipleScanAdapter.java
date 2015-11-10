package com.beintoo.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.MissionWrapper;

import java.util.List;

public class MultipleScanAdapter extends ArrayAdapter<MissionWrapper> {

    private Context mContext;

    public MultipleScanAdapter(Context context, int textViewResourceId, List<MissionWrapper> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.multiple_scan_row, null);

        MissionWrapper m = getItem(position);

        final TextView title = (TextView) convertView.findViewById(R.id.multiple_scan_title);
        final TextView scanned = (TextView) convertView.findViewById(R.id.multiple_scan_scanned);
        final ImageView spunta = (ImageView) convertView.findViewById(R.id.multiple_scan_spunta);

        try{
            if(position == 0){
                title.setText(mContext.getString(R.string.earn_bedollars_multiplescanning_scan1));
            }else{
                title.setText(mContext.getString(R.string.earn_bedollars_multiplescanning_scanother));
            }

            if(m.isChecked()){
                spunta.setImageResource(R.drawable.spunta);
                spunta.setVisibility(View.VISIBLE);
                scanned.setVisibility(View.VISIBLE);
            }

            if(!m.isEnabled()){
                title.setTextColor(Color.parseColor("#aaaaaa"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }

    @Override
    public MissionWrapper getItem(int position) {
        return super.getItem(position);
    }
}
