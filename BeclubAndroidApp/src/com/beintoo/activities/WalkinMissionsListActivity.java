package com.beintoo.activities;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.adapters.MissionTypeAdapter;
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

public class WalkinMissionsListActivity extends BeNotificationActivity
		implements AdapterView.OnItemClickListener {

	private ListView mListView;
	private String mMissionType;
	private int mLastClickedRow = -1;
	private final static int REQUEST_FOR_WALKIN = 10000;
	private ProgressBar mProgress;
	private String brandID;
	private String brandName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brand_mission_detail_list_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);
		mProgress = (ProgressBar) findViewById(R.id.progressBar);

		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.getString("brand_id") != null
				&& extras.getString("mission_type") != null) {
			mMissionType = extras.getString("mission_type");
			String[] params = { extras.getString("brand_id"), mMissionType };
			setIcon(params[1]);

			ArrayList<String> subTypes = new ArrayList<String>();
			subTypes.add(mMissionType);
			if (mMissionType
					.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN
							.toString())) {
				subTypes.add(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME
						.toString());
			}

			if (extras.getString("brand_name") != null) {
				brandName = extras.getString("brand_name");
				setTitle(extras.getString("brand_name"));
			}
			brandID = extras.getString("brand_id");

			RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
			IMissionSponsorResource missionService = restAdapter
					.create(IMissionSponsorResource.class);

			missionService.getBrandMissions(MemberAuthStore.getAuth(this)
					.getToken(), LocationUtils.getLocationParams(this), null,
					null, subTypes, brandID, null,
					new Callback<PaginatedList<MissionWrapper>>() {
						@Override
						public void success(
								PaginatedList<MissionWrapper> missions,
								Response response) {
							if (missions == null || missions.getList() == null) {
								finish();
								return;
							}
							mListView.setAdapter(new MissionTypeAdapter(
									WalkinMissionsListActivity.this,
									R.layout.brand_mission_row, missions
											.getList()));
							mProgress.setVisibility(View.GONE);
						}

						@Override
						public void failure(RetrofitError error) {
							if (error.isNetworkError()) {
								Toast.makeText(WalkinMissionsListActivity.this,
										getString(R.string.connection_error),
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(WalkinMissionsListActivity.this,
										getString(R.string.something_wrong),
										Toast.LENGTH_LONG).show();
							}
						}
					});
		}
	}

	private void setIcon(String missionType) {
		if (missionType.equals(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN
				.toString()))
			((ImageView) findViewById(R.id.mission_type_icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.code_icon));
		else if (missionType
				.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN
						.toString())
				|| missionType
						.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME
								.toString())) {
			((ImageView) findViewById(R.id.mission_type_icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.walk_mission_2));
			((TextView) findViewById(R.id.mission_type_title))
					.setText(Html
							.fromHtml(getString(R.string.earn_bedollars_walkin_main_title)));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		/**
		 * If the mission has a reward we check if the user has verified the
		 * email
		 */
		boolean verified = UserVerificationHelper.isUserVerified(this,
				getSupportFragmentManager(),
				((MissionTypeAdapter) mListView.getAdapter()).getItem(i));
		if (!verified) {
			return;
		}

		mLastClickedRow = i;
		try {
			if (mMissionType
					.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN
							.toString())
					|| mMissionType
							.equals(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME
									.toString())) {
				Intent intent = new Intent(this, WalkinMissionActivity.class);
				intent.putExtra("brand_id",
						getIntent().getExtras().getString("brand_id"));
				intent.putExtra("brand_name", getIntent().getExtras()
						.getString("brand_name"));
				intent.putExtra("mission_id",
						((MissionTypeAdapter) mListView.getAdapter())
								.getItem(i).getMission().getId());
				intent.putExtra("place_id", ((MissionTypeAdapter) mListView
						.getAdapter()).getItem(i).getPlacesid());
				startActivityForResult(intent, REQUEST_FOR_WALKIN);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_FOR_WALKIN && resultCode == RESULT_OK) {
			((MissionTypeAdapter) mListView.getAdapter()).getItem(
					mLastClickedRow).setEnabled(false);
			((MissionTypeAdapter) mListView.getAdapter())
					.notifyDataSetChanged();
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
	public void onStart() {
		super.onStart();

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

	public static Intent getWalkinMissionListActivity(Context context,
			String brandID, String missionType) {
		Intent intent = new Intent(context, WalkinMissionsListActivity.class);
		intent.putExtra("brand_id", brandID);
		intent.putExtra("mission_type", missionType);
		intent.putExtra("brand_name", "BeClub");
		return intent;
	}
}