package com.beintoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.beclubapp.R;

import java.util.List;
import java.util.Map;


public class TellAFriendAdapter extends ArrayAdapter<Map<String,String>> {

    private List<Map<String,String>> mObjects;
    private Context mContext;

    public TellAFriendAdapter(Context context, int textViewResourceId, List<Map<String,String>> objects) {
        super(context, textViewResourceId, objects);
        mObjects = objects;
        mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.tell_a_friend_row, parent, false);

        TextView name = ((TextView) convertView.findViewById(R.id.brand_mission_name));
        TextView description = ((TextView) convertView.findViewById(R.id.brand_mission_desc));
        ImageView image = ((ImageView) convertView.findViewById(R.id.brand_mission_icon));

        Map<String,String> item = mObjects.get(position);

        name.setText(item.get("name"));
        description.setText(item.get("description"));
        int resID = mContext.getResources().getIdentifier(item.get("icon"), "drawable", mContext.getPackageName());
        image.setImageResource(resID);

        return convertView;
    }
}
