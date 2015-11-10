package com.beintoo.activities;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.adapters.MultipleScanStoresAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.asynctasks.MeTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.FraudManager;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.UserVerificationHelper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlacesContainerWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MultipleScanStoresActivity extends BeNotificationActivity
		implements AdapterView.OnItemClickListener {

	public final static int REQUEST_FOR_MULTIPLESCAN = 10001;

	private float mScale;
	String mBrandId = null;
	String mBrandName = null;
	private int mLastSelectedRow = -1;
	private ListView mListView;
	private PaginatedList<MissionWrapper> mMissionsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiple_scan_stores_activity);
		setTitle(getString(R.string.earn_bedollars_multiplescanning_windowtitle));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		((ImageView) findViewById(R.id.mission_type_icon))
				.setImageDrawable(getResources().getDrawable(
						R.drawable.scan_mission_2));
		((TextView) findViewById(R.id.mission_type_title))
				.setText(getString(R.string.earn_bedollars_multiplescanning_stores_title));

		mScale = getResources().getDisplayMetrics().density;

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("brand_id") != null) {
			mBrandId = extras.getString("brand_id");

			if (extras.getString("brand_name") != null) {
				mBrandName = extras.getString("brand_name");
				setTitle(extras.getString("brand_name"));
			}
		}

		ArrayList<String> typesList = new ArrayList<String>();
		typesList.add(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN
				.toString());

		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		final IMissionSponsorResource missionService = restAdapter
				.create(IMissionSponsorResource.class);

		missionService.getBrandPlaces(MemberAuthStore.getAuth(this).getToken(),
				LocationUtils.getLocationParams(this), null, 15, typesList,
				mBrandId, null,
				new Callback<PaginatedList<PlacesContainerWrapper>>() {
					@Override
					public void success(
							PaginatedList<PlacesContainerWrapper> placesContainer,
							Response response) {
						if (placesContainer != null) {
							List<PlacesContainerWrapper> places = placesContainer
									.getList();
							TextView header = new TextView(
									MultipleScanStoresActivity.this);
							header.setPadding((int) (10 * mScale + 0.5f),
									(int) (30 * mScale + 0.5f),
									(int) (10 * mScale + 0.5f),
									(int) (10 * mScale + 0.5f));
							header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
							header.setTextColor(getResources().getColor(
									R.color.b_bestore_text));
							header.setText(MultipleScanStoresActivity.this
									.getResources()
									.getString(
											R.string.earn_bedollars_multiplescanning_stores));
							mListView.addHeaderView(header, null, false);
							mListView.setAdapter(new MultipleScanStoresAdapter(
									MultipleScanStoresActivity.this,
									R.layout.multiple_scan_stores_row, places));

							if (places != null && places.size() > 0
									&& places.get(0) != null
									&& places.get(0).getBrands() != null
									&& places.get(0).getBrands().size() > 0) {
								mBrandName = places.get(0).getBrands().get(0)
										.getBrands().getName();
							}

							if (places != null && places.size() > 0) {

								ArrayList<String> subTypes = new ArrayList<String>();
								subTypes.add(EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN
										.toString());
								missionService
										.getBrandMissions(
												MemberAuthStore
														.getAuth(
																MultipleScanStoresActivity.this)
														.getToken(),
												LocationUtils
														.getLocationParams(MultipleScanStoresActivity.this),
												null,
												1,
												subTypes,
												mBrandId,
												places.get(0).getPlaces()
														.getId(),
												new Callback<PaginatedList<MissionWrapper>>() {
													@Override
													public void success(
															final PaginatedList<MissionWrapper> missionWrapper,
															Response response) {
														if (missionWrapper == null
																|| missionWrapper
																		.getList() == null) {
															finish();
														}
														mMissionsList = missionWrapper;
														// SHOW the reward if
														// present
														if (missionWrapper
																.getList()
																.size() > 0
																&& missionWrapper
																		.getList()
																		.get(0)
																		.getReward() != null) {
															findViewById(
																	R.id.mission_reward_container)
																	.setVisibility(
																			View.VISIBLE);
															ImageLoader
																	.getInstance()
																	.displayImage(
																			missionWrapper
																					.getList()
																					.get(0)
																					.getReward()
																					.getImagesmall(),
																			(ImageView) findViewById(R.id.view_reward_details));
															((TextView) findViewById(R.id.mission_reward_amount))
																	.setText("+"
																			+ missionWrapper
																					.getList()
																					.get(0)
																					.getMission()
																					.getBedollars()
																					.intValue());
															findViewById(
																	R.id.view_reward_details)
																	.setOnClickListener(
																			new View.OnClickListener() {
																				@Override
																				public void onClick(
																						View view) {
																					Intent intent = new Intent(
																							MultipleScanStoresActivity.this,
																							RewardActivity.class);
																					intent.putExtra(
																							"reward",
																							missionWrapper
																									.getList()
																									.get(0)
																									.getReward()
																									.toString());
																					intent.putExtra(
																							"is_detail_from_mission",
																							true);
																					MultipleScanStoresActivity.this
																							.startActivity(intent);
																				}
																			});

															((TextView) findViewById(R.id.mission_type_title))
																	.setText(getString(R.string.earn_bedollars_multiplescanning_stores_title_w_reward));
														} else {
															findViewById(
																	R.id.mission_bedollars_reward_container)
																	.setVisibility(
																			View.VISIBLE);
															((TextView) findViewById(R.id.mission_reward_only_bed_amount))
																	.setText("+"
																			+ missionWrapper
																					.getList()
																					.get(0)
																					.getMission()
																					.getBedollars()
																					.intValue());
														}

														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														mListView
																.setVisibility(View.VISIBLE);
													}

													@Override
													public void failure(
															RetrofitError error) {
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														if (error
																.isNetworkError()) {
															Toast.makeText(
																	MultipleScanStoresActivity.this,
																	getString(R.string.connection_error),
																	Toast.LENGTH_LONG)
																	.show();
														} else {
															Toast.makeText(
																	MultipleScanStoresActivity.this,
																	getString(R.string.something_wrong),
																	Toast.LENGTH_LONG)
																	.show();
														}
													}
												});
							} else {
								finish();
							}
						}
					}

					@Override
					public void failure(RetrofitError error) {

					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		mLastSelectedRow = i - 1;
		/**
		 * If the mission has a reward we check if the user has verified the
		 * email
		 */
		boolean verified = UserVerificationHelper.isUserVerified(this,
				getSupportFragmentManager(), mMissionsList.getList().get(0));
		if (!verified) {
			return;
		}

		try {
			MultipleScanStoresAdapter adapter = ((MultipleScanStoresAdapter) ((HeaderViewListAdapter) mListView
					.getAdapter()).getWrappedAdapter());

			Intent intent = new Intent(this, MultipleScanActivity.class);
			intent.putExtra("brand_id", mBrandId);
			intent.putExtra("brand_name", mBrandName);
			intent.putExtra("place",
					new Gson().toJson(adapter.getItem(i - 1).getPlaces()));
			intent.putExtra("place_id", adapter.getItem(i - 1).getPlaces()
					.getId());
			intent.putExtra("place_lat", adapter.getItem(i - 1).getPlaces()
					.getLatitude().floatValue());
			intent.putExtra("place_lng", adapter.getItem(i - 1).getPlaces()
					.getLongitude().floatValue());
			intent.putExtra("missions", new Gson().toJson(mMissionsList));

			startActivityForResult(intent, REQUEST_FOR_MULTIPLESCAN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_FOR_MULTIPLESCAN && resultCode == RESULT_OK) {
			MultipleScanStoresAdapter adapter = ((MultipleScanStoresAdapter) ((HeaderViewListAdapter) mListView
					.getAdapter()).getWrappedAdapter());
			adapter.getItem(mLastSelectedRow).setEnabled(false);
			adapter.notifyDataSetChanged();
		} else if (resultCode == FraudManager.BANNED_ACTIVITY_RESULT) {
			setResult(FraudManager.BANNED_ACTIVITY_RESULT);
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//
	}

	@Override
	protected void onResume() {
		super.onResume();

		/**
		 * If the user email is not verified everytime the activity starts we
		 * check if the user has verified the email
		 */
		if (!MemberAuthStore.isUserVerified(this)) {
			new MeTask(this).execute();
		}
	}

	public static Intent getMultipleScanStoresActivityIntent(Context context,
			String brandId) {
		Intent intent = new Intent(context, MultipleScanStoresActivity.class);
		intent.putExtra("brand_id", brandId);
		intent.putExtra("brand_name", "BeClub");
		return intent;
	}
}
