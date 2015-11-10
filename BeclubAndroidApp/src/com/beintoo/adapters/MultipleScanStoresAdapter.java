package com.beintoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.UnitLocale;
import com.beintoo.wrappers.PlacesContainerWrapper;

import java.util.List;

public class MultipleScanStoresAdapter extends ArrayAdapter<PlacesContainerWrapper> {

    public MultipleScanStoresAdapter(Context context, int textViewResourceId, List<PlacesContainerWrapper> objects) {
        super(context, textViewResourceId, objects);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.multiple_scan_stores_row, null);


        final TextView storeName = (TextView) convertView.findViewById(R.id.store_name);
        final TextView address = (TextView) convertView.findViewById(R.id.store_address);
        final TextView distance = (TextView) convertView.findViewById(R.id.store_distance);

        PlacesContainerWrapper p = getItem(position);

        try {
            storeName.setText(p.getPlaces().getName());
            address.setText(p.getPlaces().getAddress());
            if(UnitLocale.getDefault() == UnitLocale.Imperial)
                distance.setText(UnitLocale.metersToMiles(p.getDistance()).toString() + " miles");
            else{
                Float meters = p.getDistance();
                if(meters < 1000.0){
                    distance.setText(String.format("%.2f", p.getDistance()) + " mt");
                }else{

                    distance.setText(String.format("%.2f", UnitLocale.metersToKilometers(meters)) + " km");
                }
            }

            if(!p.isEnabled()){
                convertView.findViewById(R.id.done).setVisibility(View.VISIBLE);
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
    public PlacesContainerWrapper getItem(int position) {
        return super.getItem(position);
    }

}
