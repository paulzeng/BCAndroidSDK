package com.beintoo.fragments;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.adapters.OnlineProductAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.EmailNotVerifiedDialog;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.OnlineProductBean;
import com.beintoo.wrappers.PaginatedList;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;

/**
 * Created by Giulio Bider on 03/09/14. Copyright (c) 2014 Beintoo. All rights
 * reserved.
 */
public class BrandOnlineFragment extends Fragment {

	private View mView;
	private View mHeader;

	private EarnBedollarsWrapper mMission;
	private MissionWrapper mAssignedMission;

	private StaggeredGridView mGridView;
	private OnlineProductAdapter mAdapter;

	private ProgressBar mProgressBar;

	private String mLastKey;
	private boolean mHasRequestedMore;

	private Bundle mIntentBundle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.brand_online_fragment, container,
				false);

		mGridView = (StaggeredGridView) mView.findViewById(R.id.grid_view);

		mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

		mHeader = inflater.inflate(R.layout.online_product_header, container,
				false);
		mGridView.addHeaderView(mHeader, null, false);

		Button btnGetDeal = (Button) mHeader.findViewById(R.id.btn_get_deal);
		btnGetDeal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (MemberAuthStore.getMember(getActivity()).isEmailverified()) {

					openGetDealDialog();
				} else {
					new EmailNotVerifiedDialog(getActivity()).show(
							getActivity().getSupportFragmentManager(),
							"email-not-verified");
				}
			}
		});

		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {

			}

			@Override
			public void onScroll(final AbsListView view,
					final int firstVisibleItem, final int visibleItemCount,
					final int totalItemCount) {
				if (!mHasRequestedMore) {
					int lastInScreen = firstVisibleItem + visibleItemCount;
					if (lastInScreen >= totalItemCount) {
						mHasRequestedMore = true;
						onLoadMoreItems();
					}
				}
			}
		});

		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mMission = new Gson().fromJson(
					savedInstanceState.getString("mission"),
					EarnBedollarsWrapper.class);
		}

		ArrayList<String> types = new ArrayList<String>();
		types.add("ONLINE");

		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		final IMissionSponsorResource missionService = restAdapter
				.create(IMissionSponsorResource.class);

		missionService.getBrandMissions(MemberAuthStore.getAuth(getActivity())
				.getToken(), LocationUtils.getLocationParams(getActivity()),
				null, null, types, mMission.getBrands().getId(), null,
				new Callback<PaginatedList<MissionWrapper>>() {
					@Override
					public void success(PaginatedList<MissionWrapper> missions,
							Response response) {
						if (missions == null || missions.getList() == null) {
							getActivity().finish();
							return;
						}

						if (missions.getList().get(0) != null) {
							final MissionWrapper mission = missions.getList()
									.get(0);

							missionService.getMission(
									MemberAuthStore.getAuth(getActivity())
											.getToken(), LocationUtils
											.getLocationParams(getActivity()),
									mission.getMission().getId(), mLastKey, 10,
									new Callback<MissionWrapper>() {
										@Override
										public void success(
												MissionWrapper assignedMission,
												Response response) {
											if (getActivity() != null
													&& assignedMission != null
													&& assignedMission
															.getMission() != null
													&& assignedMission
															.getItems() != null
													&& assignedMission
															.getItems()
															.getList() != null) {
												mAssignedMission = assignedMission;

												TextView tvDescription = (TextView) mHeader
														.findViewById(R.id.tv_mission_description);
												tvDescription
														.setText(mAssignedMission
																.getMission()
																.getDescription());

												mIntentBundle = new Bundle();
												mIntentBundle.putString(
														"brand_id", mMission
																.getBrands()
																.getId());
												mIntentBundle.putString(
														"brand_name", mMission
																.getBrands()
																.getName());
												mIntentBundle.putString(
														"mission_id",
														mAssignedMission
																.getMission()
																.getId());

												mAdapter = new OnlineProductAdapter(
														getActivity(),
														R.layout.brand_mission_row,
														assignedMission
																.getItems()
																.getList(),
														mIntentBundle);
												mGridView.setAdapter(mAdapter);
												mLastKey = assignedMission
														.getItems()
														.getLastKey();
												mProgressBar
														.setVisibility(View.GONE);
											}
										}

										@Override
										public void failure(RetrofitError error) {
											mView.findViewById(
													R.id.mission_empty)
													.setVisibility(View.VISIBLE);
											mProgressBar
													.setVisibility(View.GONE);
										}
									});
						} else {
							mView.findViewById(R.id.mission_empty)
									.setVisibility(View.VISIBLE);
							mProgressBar.setVisibility(View.GONE);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						if (error.isNetworkError()) {
							Toast.makeText(getActivity(),
									getString(R.string.connection_error),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getActivity(),
									getString(R.string.something_wrong),
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mission", mMission.toString());
	}

	private void onLoadMoreItems() {
		if (mAssignedMission != null && mLastKey != null) {
			RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
			IMissionSponsorResource missionService = restAdapter
					.create(IMissionSponsorResource.class);

			missionService.getMission(MemberAuthStore.getAuth(getActivity())
					.getToken(),
					LocationUtils.getLocationParams(getActivity()),
					mAssignedMission.getMission().getId(), mLastKey, 10,
					new Callback<MissionWrapper>() {
						@Override
						public void success(MissionWrapper assignedMission,
								Response response) {
							if (assignedMission != null
									&& assignedMission.getMission() != null
									&& assignedMission.getItems() != null
									&& assignedMission.getItems().getList() != null) {
								mAssignedMission = assignedMission;
								if (mAdapter != null) {
									mAdapter.addAll(assignedMission.getItems()
											.getList());
									mAdapter.notifyDataSetChanged();
									mHasRequestedMore = false;
								} else {
									mIntentBundle = new Bundle();
									mIntentBundle.putString("brand_id",
											mMission.getBrands().getId());
									mIntentBundle.putString("brand_name",
											mMission.getBrands().getName());
									mIntentBundle.putString("mission_id",
											mAssignedMission.getMission()
													.getId());

									mAdapter = new OnlineProductAdapter(
											getActivity(),
											R.layout.brand_mission_row,
											assignedMission.getItems()
													.getList(), mIntentBundle);
									mGridView.setAdapter(mAdapter);
									mAdapter.notifyDataSetChanged();
									mHasRequestedMore = false;
								}
								mLastKey = assignedMission.getItems()
										.getLastKey();
							}
						}

						@Override
						public void failure(RetrofitError error) {

						}
					});
		} else {
			mHasRequestedMore = false;
		}
	}

	public void openGetDealDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		final AlertDialog dialog = builder.create();

		dialog.setCancelable(true);

		View view = View.inflate(getActivity(), R.layout.online_product_dialog,
				null);
		view.findViewById(R.id.rl_online_close).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

		view.findViewById(R.id.rl_online_send_mail).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						RestAdapter restAdapter = BeRestAdapter
								.getMemberTokenRestAdapter();
						IMissionSponsorResource missionService = restAdapter
								.create(IMissionSponsorResource.class);

						final ProgressDialog dialog = ProgressDialog.show(
								getActivity(), "Sending", "Please wait...",
								true, false);
						OnlineProductBean productBean = new OnlineProductBean();
						productBean
								.setId(mAssignedMission.getMission().getId());

						missionService.sendOnlineMissionEmail(MemberAuthStore
								.getAuth(getActivity()).getToken(),
								mAssignedMission.getMission().getId(),
								productBean, new Callback<Object>() {
									@Override
									public void success(Object o,
											Response response) {
										dialog.dismiss();
										Toast.makeText(
												getActivity(),
												getActivity()
														.getString(
																R.string.online_check_your_inbox),
												Toast.LENGTH_LONG).show();
									}

									@Override
									public void failure(RetrofitError error) {
										dialog.dismiss();
										if (error.isNetworkError()) {
											Toast.makeText(
													getActivity(),
													getString(R.string.connection_error),
													Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(
													getActivity(),
													getString(R.string.something_wrong),
													Toast.LENGTH_LONG).show();
										}
									}
								});
						dialog.dismiss();
					}
				});

		view.findViewById(R.id.rl_online_open_browser).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String url = mAssignedMission.getMission()
								.getActioncheck().getUrl();

						if (!url.startsWith("http://")
								&& !url.startsWith("https://"))
							url = "http://" + url;

						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
						dialog.dismiss();
					}
				});
		dialog.setView(view);
		dialog.show();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

	}

	public static BrandOnlineFragment newInstance(EarnBedollarsWrapper mission) {
		BrandOnlineFragment fragment = new BrandOnlineFragment();
		fragment.mMission = mission;
		return fragment;
	}

}
