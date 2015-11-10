package com.beintoo.fragments;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.beintoo.activities.BrandMissionActivity;
import com.beintoo.adapters.EarnBedollarsAdapterV2;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.FraudManager;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperGridview;

@SuppressLint("ResourceAsColor") public class EarnBedollarsTabFragmentV2 extends Fragment {

	private SuperGridview mGridView;
	private EarnBedollarsAdapterV2 mAdapter;
	private List<String> mSupportedTypes;

	public static String mLastBrandKey = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSupportedTypes = EarnBedollarsWrapper
				.getSubTypesAvailable(getActivity());

		EarnBedollarsTabFragmentV2.mLastBrandKey = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.earn_bedollars_tab_fragment_v2,
				container, false);
		mGridView = (SuperGridview) view.findViewById(R.id.list);

		mGridView.setRefreshingColor(android.R.color.holo_green_light,
				android.R.color.holo_green_dark,
				android.R.color.holo_green_light,
				android.R.color.holo_green_dark);

		mLastBrandKey = null;

		getBrands(mLastBrandKey, 50, mSupportedTypes, null,
				new Callback<PaginatedList<EarnBedollarsWrapper>>() {
					@Override
					public void success(
							PaginatedList<EarnBedollarsWrapper> brands,
							Response response) {
						if (getActivity() != null) {
							if (brands != null && brands.getList() != null) {
								mLastBrandKey = brands.getLastKey();
								mAdapter = new EarnBedollarsAdapterV2(
										getActivity(),
										R.layout.bestore_item_row, brands
												.getList());
								mGridView.setAdapter(mAdapter);
							}
						}
					}

					@Override
					public void failure(RetrofitError error) {
						handleError(error);
					}
				});

		mGridView
				.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mLastBrandKey = null;
						getBrands(
								mLastBrandKey,
								50,
								mSupportedTypes,
								null,
								new Callback<PaginatedList<EarnBedollarsWrapper>>() {
									@Override
									public void success(
											PaginatedList<EarnBedollarsWrapper> brands,
											Response response) {
										if (getActivity() != null) {
											if (brands != null
													&& brands.getList() != null) {
												mLastBrandKey = brands
														.getLastKey();
												mAdapter = new EarnBedollarsAdapterV2(
														getActivity(),
														R.layout.bestore_item_row,
														brands.getList());
												mGridView.setAdapter(mAdapter);
											}
										}
									}

									@Override
									public void failure(RetrofitError error) {
										handleError(error);
									}
								});
					}
				});

		mGridView.setupMoreListener(new OnMoreListener() {
			@Override
			public void onMoreAsked(int numberOfItems, int numberBeforeMore,
					int currentItemPos) {
				if (mLastBrandKey != null) {
					getBrands(
							mLastBrandKey,
							50,
							mSupportedTypes,
							null,
							new Callback<PaginatedList<EarnBedollarsWrapper>>() {
								@Override
								public void success(
										PaginatedList<EarnBedollarsWrapper> brands,
										Response response) {
									if (brands != null) {
										mLastBrandKey = brands.getLastKey();
										mAdapter.addAll(brands.getList());
									}
									mGridView.hideMoreProgress();
								}

								@Override
								public void failure(RetrofitError error) {
									handleError(error);
								}
							});
				} else {
					mGridView.hideMoreProgress();
				}
			}
		}, 1);

		mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), true, true));

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				EarnBedollarsWrapper item = (EarnBedollarsWrapper) adapterView
						.getItemAtPosition(i);
				Intent intent = new Intent(getActivity(),
						BrandMissionActivity.class);
				intent.putExtra("missions", new Gson().toJson(item));
				startActivityForResult(intent,
						FraudManager.BANNED_ACTIVITY_RESULT);

			}
		});

		return view;
	}

	private void handleError(RetrofitError error) {
		if (error.isNetworkError()) {
			Toast.makeText(getActivity(), getString(R.string.connection_error),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), getString(R.string.something_wrong),
					Toast.LENGTH_LONG).show();
		}
	}

	private void getBrands(String lastKey, Integer rows, List<String> subTypes,
			String brandID,
			Callback<PaginatedList<EarnBedollarsWrapper>> callback) {
		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		IMissionSponsorResource missionResource = restAdapter
				.create(IMissionSponsorResource.class);

		missionResource.getBrands(MemberAuthStore.getAuth(getActivity())
				.getToken(), LocationUtils.getLocationParams(getActivity()),
				true, lastKey, rows, subTypes, brandID, callback);
	}
}
