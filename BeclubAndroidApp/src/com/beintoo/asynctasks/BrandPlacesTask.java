package com.beintoo.asynctasks;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlacesContainerWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Giulio Bider on 29/10/14.
 * Copyright (c) 2014 Beintoo. All rights reserved.
 */
public class BrandPlacesTask extends AsyncTask<Void, Void, PaginatedList<PlacesContainerWrapper>> {

    public interface OnBrandPlacesListener {
        public void onBrandPlacesLoaded(PaginatedList<PlacesContainerWrapper> places);
        public void onError();
    }

    private Context mContext;
    private String mBrandID;
    private String mMissionID;
    private OnBrandPlacesListener mListener;
    private List<String> mTypes;

    public BrandPlacesTask(Context context, String brandID, List<String> types, String missionID, OnBrandPlacesListener listener) {
        this.mContext = context;
        this.mBrandID = brandID;
        this.mMissionID = missionID;
        this.mTypes = types;
        this.mListener = listener;
    }

    @Override
    protected PaginatedList<PlacesContainerWrapper> doInBackground(Void... voids) {

        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

        PaginatedList<PlacesContainerWrapper> places;
        try {
            places = missionService.getBrandPlaces(MemberAuthStore.getAuth(mContext).getToken(), LocationUtils.getLocationParams(mContext), null, 15, mTypes, mBrandID, mMissionID);

            if (places != null && places.getList() != null && places.getList().size() > 0) {

                // order by distance
                Location userLocation = BeLocationManager.getInstance(mContext).getLastKnowLocation(mContext);
                for (PlacesContainerWrapper place : places.getList()) {
                    if (place.getPlaces().getLatitude() != null && place.getPlaces().getLongitude() != null) {
                        Location currentPlace = new Location("network");
                        currentPlace.setLatitude(place.getPlaces().getLatitude());
                        currentPlace.setLongitude(place.getPlaces().getLongitude());
                        place.setDistance(currentPlace.distanceTo(userLocation));
                    }

                    place.getPlaces().setImageMarkerBitmap(ImageLoader.getInstance().loadImageSync(place.getPlaces().getImagemarker()));
                }
            }

            return places;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(PaginatedList<PlacesContainerWrapper> places) {
        super.onPostExecute(places);

        if (places != null && places.getList() != null && places.getList().size() > 0) {
            if(mListener != null) {
                mListener.onBrandPlacesLoaded(places);
            }
        } else {
            if(mListener != null) {
                mListener.onError();
            }
        }
    }
}
