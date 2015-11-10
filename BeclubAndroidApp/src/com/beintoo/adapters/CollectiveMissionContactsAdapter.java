package com.beintoo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.DataStore;
import com.beintoo.wrappers.ShareContactWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CollectiveMissionContactsAdapter extends ArrayAdapter<ShareContactWrapper> implements Filterable {

    public interface ItemCheckedCallback{
        public void onItemChecked(int pos);
        public void onItemUnChecked(int pos);
    }

    private Context mContext;
    private List<ShareContactWrapper> mObjects;
    private boolean mMultipleSelection;
    private ItemCheckedCallback mCallback;
    private List<String> mAlreadyInvited;
    private String mMissionId;

    public CollectiveMissionContactsAdapter(Context context, int resource, List<ShareContactWrapper> objects, String missionId, boolean multipleSelection, ItemCheckedCallback callback) {
        super(context, resource, objects);
        mContext = context;
        mObjects = objects;
        mMultipleSelection = multipleSelection;
        mCallback = callback;
        mMissionId = missionId;
        mAlreadyInvited = DataStore.getStringList(context, "collective_invited_friends_" + missionId);
    }

    @Override
    public ShareContactWrapper getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void notifyDataSetChanged() {
        mAlreadyInvited = DataStore.getStringList(mContext, "collective_invited_friends_" + mMissionId);
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.collective_contact_row, parent, false);

        final ShareContactWrapper contact = getItem(position);

        ((TextView)convertView.findViewById(R.id.collective_name)).setText(contact.getName());

        if(contact.getImageUrl() != null)
            ImageLoader.getInstance().displayImage(contact.getImageUrl(), ((ImageView)convertView.findViewById(R.id.collective_image)));
        else
            ((ImageView)convertView.findViewById(R.id.collective_image)).setImageResource(R.drawable.user);


        if(mAlreadyInvited.contains(contact.getId())){
            convertView.findViewById(R.id.already_invited).setVisibility(View.VISIBLE);
        }


        if(!mMultipleSelection){
            convertView.findViewById(R.id.collective_checkBox).setVisibility(View.GONE);
        }else{
            final CheckBox cb = ((CheckBox) convertView.findViewById(R.id.collective_checkBox));
            cb.setChecked(contact.isSelected());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cb.setChecked(!cb.isChecked());
                }
            });

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    contact.setSelected(b);

                    if(mCallback != null){
                        if(b){
                            mCallback.onItemChecked(position);
                        }else{
                            mCallback.onItemUnChecked(position);
                        }
                    }

                }
            });
        }


        return convertView;
    }
}
