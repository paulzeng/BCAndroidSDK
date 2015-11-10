package com.beintoo.activities;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beintoo.adapters.ScanMissionAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MeTask;
import com.beintoo.asynctasks.MissionCompleteTask;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.WidgetBedollars;
import com.beintoo.dialogs.DeviceBannedDialog;
import com.beintoo.dialogs.MissionUnlockedDialog;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.FraudManager;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.UserVerificationHelper;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.zbar.scanner.CodeScanning;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

public class ScanMissionActivity extends BeNotificationActivity implements
		AdapterView.OnItemClickListener {

	private ListView mListView;
	private Double mMissionAmount;
	private MissionWrapper mSelectedMission;
	private View mSelectedRow;
	private Activity mActivity;
	private String brandID, brandName;

	private final String mScreenName = "EARN-BEDOLLARS-SCAN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_mission_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		setTitle(getString(R.string.earn_bedollars_scanning_title));

		mActivity = this;

		mListView = (ListView) findViewById(R.id.listView);

		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.getString("brand_id") != null
				&& extras.getString("mission_type") != null) {
			ArrayList<String> type = new ArrayList<String>();
			type.add(EarnBedollarsWrapper.MissionActionTypeEnum.SCAN.toString());

			RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
			IMissionSponsorResource missionService = restAdapter
					.create(IMissionSponsorResource.class);

			missionService.getBrandMissions(MemberAuthStore.getAuth(this)
					.getToken(), LocationUtils.getLocationParams(this), null,
					null, type, brandID, null,
					new Callback<PaginatedList<MissionWrapper>>() {
						@Override
						public void success(
								PaginatedList<MissionWrapper> missions,
								Response response) {
							if (missions == null || missions.getList() == null) {
								finish();
								return;
							}
							ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar);
							if (missions.getList().size() > 0) {
								ScanMissionAdapter sma;
								if (mListView.getAdapter() == null) {
									sma = new ScanMissionAdapter(mActivity,
											R.layout.brand_mission_row,
											missions.getList());
									mListView.setAdapter(sma);
								}
							}
							loading.setVisibility(View.GONE);
						}

						@Override
						public void failure(RetrofitError error) {
							if (error.isNetworkError()) {
								Toast.makeText(ScanMissionActivity.this,
										getString(R.string.connection_error),
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(ScanMissionActivity.this,
										getString(R.string.something_wrong),
										Toast.LENGTH_LONG).show();
							}
						}
					});
			mListView.setOnItemClickListener(this);

			if (extras.getString("brand_name") != null) {
				brandName = extras.getString("brand_name");
				setTitle(extras.getString("brand_name"));
			}
			brandID = extras.getString("brand_id");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		mSelectedMission = (MissionWrapper) mListView.getAdapter().getItem(i);
		/**
		 * If the mission has a reward we check if the user has verified the
		 * email
		 */
		boolean verified = UserVerificationHelper.isUserVerified(this,
				getSupportFragmentManager(), mSelectedMission);
		if (!verified) {
			return;
		}

		if (mSelectedMission != null) {
			mSelectedRow = view;
			startScanningAndCheckCode();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10000) {
			CodeScanning.getInstance().scanResultHandler(resultCode, data);
		} else if (resultCode == FraudManager.BANNED_ACTIVITY_RESULT) {
			setResult(FraudManager.BANNED_ACTIVITY_RESULT);
			finish();
		}
	}

	private void startScanningAndCheckCode() {
		CodeScanning.getInstance().startScanning(this,
				new CodeScanning.CodeScanningCallback() {
					@Override
					public void onCodeScanned(String codeNumber,
							Integer codeType) {
						DebugUtility.showLog("GOT CODE " + codeNumber
								+ " SELECTED ROW " + mSelectedRow);
						if (codeNumber.equals(mSelectedMission.getMission()
								.getActioncheck().getScannumber())) {
							mMissionAmount = mSelectedMission.getMission()
									.getBedollars();
							String[] params = {
									mSelectedMission.getMission().getId(),
									mSelectedMission.getPlacesid() };
							// new CompleteMissionTask().execute(params);
							new MissionCompleteTask(
									ScanMissionActivity.this,
									null,
									new MissionCompleteTask.MissionCompleteCallback() {
										@Override
										public void onCompleted(
												AssignedMissionWrapper results) {
											if (mSelectedRow != null) {
												mSelectedMission
														.setEnabled(false);
												mSelectedMission
														.setMinutestoredo(10);
												((ScanMissionAdapter) mListView
														.getAdapter())
														.notifyDataSetChanged();
											}
											try {

												WidgetBedollars
														.updateWidgetBalance(ScanMissionActivity.this);
											} catch (Exception e) {
												e.printStackTrace();
											}

											try {
												new MissionUnlockedDialog(
														ScanMissionActivity.this,
														getString(R.string.earn_bedollars_reward_mission_completed),
														results, this)
														.show(getSupportFragmentManager(),
																"MissionCompleted");
											} catch (Exception e) {
												e.printStackTrace();
												MATEventItem item = new MATEventItem(
														"bedollars",
														1,
														results.getBedollars(),
														0,
														MemberAuthStore
																.getMember(
																		ScanMissionActivity.this)
																.getId(),
														results.getMissionsid(),
														null, null, null);
												MobileAppTracker
														.getInstance()
														.measureAction(
																"mission-completed",
																item);

												// try {
												// AppEventsLogger logger =
												// AppEventsLogger.newLogger(ScanMissionActivity.this);
												// Bundle args = new Bundle();
												// args.putString("memberID",
												// MemberAuthStore.getMember(ScanMissionActivity.this).getId());
												// args.putString("missionID",
												// results.getMissionsid());
												// args.putDouble("bedollars",
												// results.getBedollars());
												// logger.logEvent("mission-completed",
												// args);
												// } catch (Exception ex) {
												// ex.printStackTrace();
												// }
											}
										}

										@Override
										public void onFailed(
												AsyncTasks.Result error) {
											switch (error) {
											case BANNED:
												new DeviceBannedDialog(
														ScanMissionActivity.this,
														true)
														.show(getSupportFragmentManager(),
																"banned-device");
												break;
											case NETWORK_ERROR:
												Toast.makeText(
														ScanMissionActivity.this,
														getString(R.string.connection_error),
														Toast.LENGTH_LONG)
														.show();
												break;
											default:
												Toast.makeText(
														ScanMissionActivity.this,
														getString(R.string.something_wrong),
														Toast.LENGTH_LONG)
														.show();
											}
										}

										@Override
										public void onPopupClosed() {

										}
									}).execute(params);

						} else

						{
							showErrorDialog();

						}
					}

					@Override
					public void onCodeFailed() {

					}
				});
	}

	private void showErrorDialog() {
		new AlertDialog.Builder(ScanMissionActivity.this)
				.setTitle(
						ScanMissionActivity.this.getResources().getString(
								R.string.earn_bedollars_scan_failed_title))
				.setMessage(
						ScanMissionActivity.this.getResources().getString(
								R.string.earn_bedollars_scan_failed_message))
				.setNegativeButton(
						R.string.earn_bedollars_scan_failed_negative,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						})
				.setPositiveButton(
						R.string.earn_bedollars_scan_failed_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mSelectedMission != null)
									startScanningAndCheckCode();
							}
						}).create().show();

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		finish();
		return super.onMenuItemSelected(featureId, item);
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

	public static Intent getScanMissionActivityIntent(Context context,
			String brandID, String missionType) {
		Intent intent = new Intent(context, ScanMissionActivity.class);
		intent.putExtra("brand_id", brandID);
		intent.putExtra("mission_type", missionType);
		intent.putExtra("brand_name", "BeClub");
		return intent;
	}
}
