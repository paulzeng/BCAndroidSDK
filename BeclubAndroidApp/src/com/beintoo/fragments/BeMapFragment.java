package com.beintoo.fragments;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beintoo.activities.BrandMissionActivity;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.asynctasks.BrandPlacesTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.BeMarker;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlacesContainerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

public class BeMapFragment extends Fragment {

    private HashMap<Marker, BeMarker> mPlaces;
    private MapView mFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earn_map_fragment, container, false);
        mFragment = (MapView) view.findViewById(R.id.map);
        mFragment.onCreate(savedInstanceState);
        mFragment.onResume();

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlaces = new HashMap<Marker, BeMarker>();

        final GoogleMap mMap = mFragment.getMap();
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Location location = BeLocationManager.getInstance(getActivity()).getLastKnowLocation(getActivity());
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                try {
                    mPlaces.put(mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).visible(true)), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    BeMarker beMarker = mPlaces.get(marker);
                    if (beMarker != null) {
                        final PlacesContainerWrapper place = beMarker.getPlace();
                        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, getActivity().getString(R.string.loading_dialog));
                        dialog.show();

                        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
                        IMissionSponsorResource missionService = restAdapter.create(IMissionSponsorResource.class);

                        missionService.getBrands(MemberAuthStore.getAuth(getActivity()).getToken(), LocationUtils.getLocationParams(getActivity()), true, null, 50, EarnBedollarsWrapper.getSubTypesAvailable(getActivity()), place.getBrands().get(0).getBrands().getId(), new Callback<PaginatedList<EarnBedollarsWrapper>>() {
                            @Override
                            public void success(PaginatedList<EarnBedollarsWrapper> earnBedollarsWrapperPaginatedList, Response response) {
                                try {
                                    if (earnBedollarsWrapperPaginatedList != null && earnBedollarsWrapperPaginatedList.getList() != null && earnBedollarsWrapperPaginatedList.getList().size() > 0) {
                                        dialog.dismiss();
                                         EarnBedollarsWrapper wrapper = earnBedollarsWrapperPaginatedList.getList().get(0);
                                        String wrapperJson = new Gson().toJson(wrapper);
                                        Intent intent = new Intent(getActivity(), BrandMissionActivity.class);
                                        intent.putExtra("missions", wrapperJson);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    return true;
                }
            });

            new BrandPlacesTask(getActivity(), null, null, null, new BrandPlacesTask.OnBrandPlacesListener() {
                @Override
                public void onBrandPlacesLoaded(PaginatedList<PlacesContainerWrapper> places) {
                    for(PlacesContainerWrapper p : places.getList()) {
                        if (p.getPlaces().getLatitude() != null && p.getPlaces().getLongitude() != null) {
                            LatLng userLocation = new LatLng(p.getPlaces().getLatitude(), p.getPlaces().getLongitude());
                            final MarkerOptions placeMarker = new MarkerOptions().position(userLocation).title(p.getPlaces().getAddress());
                            if (p.getPlaces().getImagemarker() != null) {
                                try {
                                    placeMarker.icon(BitmapDescriptorFactory.fromBitmap(p.getPlaces().getImageMarkerBitmap()));
                                    BeMarker beMarker = new BeMarker(mMap.addMarker(placeMarker), p);
                                    mPlaces.put(beMarker.getMarker(), beMarker);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    placeMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                BeMarker beMarker = new BeMarker(mMap.addMarker(placeMarker), p);
                                mPlaces.put(beMarker.getMarker(), beMarker);
                            }
                        }
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(getActivity(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFragment != null) {
            mFragment.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onStop();
        if (mFragment != null) {
            mFragment.onDestroy();
        }
    }
}
