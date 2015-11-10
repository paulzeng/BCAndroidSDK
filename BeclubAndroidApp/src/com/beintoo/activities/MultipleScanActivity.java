package com.beintoo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.adapters.MultipleScanAdapter;
import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MeTask;
import com.beintoo.asynctasks.MissionCompleteTask;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.WidgetBedollars;
import com.beintoo.dialogs.DeviceBannedDialog;
import com.beintoo.dialogs.MissionUnlockedDialog;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.FraudManager;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.MissionDataWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlaceWrapper;
import com.beintoo.zbar.scanner.CodeScanning;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

public class MultipleScanActivity extends BeNotificationActivity implements
		AdapterView.OnItemClickListener {

	private final double DEFAULT_UNLOCK_DISTANCE = 60;
	private double UNLOCK_DISTANCE = DEFAULT_UNLOCK_DISTANCE;

	private ListView mListView;
	private String mBrandId, mBrandName;
	private int mClickedRow;
	private List<String> mScannedCodes;
	private String mPlaceId;
	private PlaceWrapper mPlace = null;

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Location mUserLocation;
	private Location mPlaceLocation;
	private boolean mIsFirstLocationCheck = true;

	private PaginatedList<MissionWrapper> mMissionsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiple_scan_stores_activity);
		setTitle(getString(R.string.earn_bedollars_multiplescanning_windowtitle));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		findViewById(R.id.title).setVisibility(View.GONE);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("brand_id") != null
				&& extras.getString("brand_name") != null
				&& extras.getString("place_id") != null) {
			mBrandId = extras.getString("brand_id");
			mBrandName = extras.getString("brand_name");
			mPlaceId = extras.getString("place_id");
			if (extras.getString("place") != null)
				mPlace = new Gson().fromJson(extras.getString("place"),
						PlaceWrapper.class);

			mMissionsList = new Gson().fromJson(extras.getString("missions"),
					new TypeToken<PaginatedList<MissionWrapper>>() {
					}.getType());

			mPlaceLocation = new Location("network");
			mPlaceLocation.setLatitude(extras.getFloat("place_lat"));
			mPlaceLocation.setLongitude(extras.getFloat("place_lng"));
		}

		mScannedCodes = new ArrayList<String>();
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);
		mListView.setVisibility(View.VISIBLE);

		initLocationListener();

		// new
		// MissionCompleteTask(this).execute("all_participant_stores-zara-multiplescan-brisbane",
		// "zarabrisbane1");
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		mClickedRow = i - 1; // the fucking header count
		startScanningAndCheck();
	}

	private void startScanningAndCheck() {
		CodeScanning.getInstance().startScanning(this,
				new CodeScanning.CodeScanningCallback() {
					@Override
					public void onCodeScanned(String codeNumber,
							Integer codeType) {
						if (codeNumber != null
								&& !mScannedCodes.contains(codeNumber)) {
							if (mPlaceLocation.distanceTo(mUserLocation) > UNLOCK_DISTANCE) {
								Toast.makeText(
										MultipleScanActivity.this,
										"You must be in the store to complete this mission",
										Toast.LENGTH_LONG).show();
								return;
							}

							// OK
							MultipleScanAdapter adapter = ((MultipleScanAdapter) ((HeaderViewListAdapter) mListView
									.getAdapter()).getWrappedAdapter());
							adapter.getItem(mClickedRow).setChecked(true);
							adapter.getItem(mClickedRow).setEnabled(false);
							adapter.notifyDataSetChanged();

							mScannedCodes.add(codeNumber);

							if (mScannedCodes.size() == adapter.getCount()) { // fucking
																				// header
																				// in
																				// count
								new MissionCompleteTask(
										MultipleScanActivity.this,
										null,
										new MissionCompleteTask.MissionCompleteCallback() {
											@Override
											public void onCompleted(
													AssignedMissionWrapper results) {
												try {

													WidgetBedollars
															.updateWidgetBalance(MultipleScanActivity.this);
												} catch (Exception e) {
													e.printStackTrace();
												}

												try {
													new MissionUnlockedDialog(
															MultipleScanActivity.this,
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
																			MultipleScanActivity.this)
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
													// AppEventsLogger.newLogger(MultipleScanActivity.this);
													// Bundle args = new
													// Bundle();
													// args.putString("memberID",
													// MemberAuthStore.getMember(MultipleScanActivity.this).getId());
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
															MultipleScanActivity.this,
															true)
															.show(getSupportFragmentManager(),
																	"banned-device");
													break;
												case NETWORK_ERROR:
													Toast.makeText(
															MultipleScanActivity.this,
															getString(R.string.connection_error),
															Toast.LENGTH_LONG)
															.show();
													break;
												default:
													Toast.makeText(
															MultipleScanActivity.this,
															getString(R.string.something_wrong),
															Toast.LENGTH_LONG)
															.show();
												}
											}

											@Override
											public void onPopupClosed() {
												setResult(RESULT_OK);
												finish();
											}
										}).execute(adapter.getItem(mClickedRow)
										.getMission().getMissionsid(), adapter
										.getItem(mClickedRow).getMission()
										.getPlacesid());
							}
						} else {

							AlertDialog.Builder builder = new AlertDialog.Builder(
									MultipleScanActivity.this);
							builder.setMessage(
									"You have already scanned this code, please choose another one")
									.setPositiveButton(
											MultipleScanActivity.this
													.getString(R.string.ok),
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialogInterface,
														int i) {
												}
											});
							builder.create().show();
						}
					}

					@Override
					public void onCodeFailed() {

					}
				});
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

	private class MultipleScanMissionTask extends
			AsyncTask<Void, Integer, List<MissionWrapper>> {

		private AsyncTasks.Result statusResult;

		@Override
		protected List<MissionWrapper> doInBackground(Void... start) {

			try {

				if (mMissionsList != null && mMissionsList.getList() != null
						&& mMissionsList.getList().size() > 0) {

					DebugUtility.showLog("---> "
							+ mMissionsList.getList().size());

					List<MissionWrapper> multipleScans = new ArrayList<MissionWrapper>();
					for (int i = 0; i < mMissionsList.getList().get(0)
							.getMission().getActioncheck().getMultiplenumber(); i++) {
						MissionWrapper m = new MissionWrapper();
						m.setEnabled(true);
						m.setChecked(false);
						m.setMinutestoredo(mMissionsList.getList().get(0)
								.getMinutestoredo());

						if (mMissionsList.getList().get(0).getMinutestoredo() > 0
								|| mMissionsList.getList().get(0)
										.getMinutestoredo() == -1)
							m.setEnabled(false);
						multipleScans.add(m);
						MissionDataWrapper md = new MissionDataWrapper();
						md.setMissionsid(mMissionsList.getList().get(0)
								.getMission().getId());
						md.setPlacesid(mPlaceId);
						m.setMission(md);

						if (mPlace != null && mPlace.getRadius() != null) {
							UNLOCK_DISTANCE = mPlace.getRadius();
						}

						/*
						 * if the user is to far lock the cell
						 */
						if (mPlaceLocation.distanceTo(mUserLocation) > UNLOCK_DISTANCE)
							m.setEnabled(false);
					}

					new MeTask(MultipleScanActivity.this).execute();

					return multipleScans;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return new ArrayList<MissionWrapper>();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<MissionWrapper> results) {
			super.onPostExecute(results);
			if (results.size() == 0)
				return;

			View header = getLayoutInflater().inflate(
					R.layout.multiple_scan_header, null, false);
			Integer minutesToRedo = results.get(0).getMinutestoredo();
			if (minutesToRedo > 0) {
				int hours = minutesToRedo / 60;
				int minutes = minutesToRedo % 60;
				String timeString = hours + "h" + minutes + "m";
				String title = getString(
						R.string.earn_bedollars_multiplescanning_already_done,
						timeString);
				((TextView) header.findViewById(R.id.multiple_scan_header_text))
						.setText(Html.fromHtml(title));
			} else if (minutesToRedo == -1) {
				((TextView) header.findViewById(R.id.multiple_scan_header_text))
						.setText(Html
								.fromHtml(getString(R.string.earn_bedollars_multiplescanning_already_done_forever)));
			} else if (mPlaceLocation.distanceTo(mUserLocation) > UNLOCK_DISTANCE) {
				((TextView) header.findViewById(R.id.multiple_scan_header_text))
						.setText(Html
								.fromHtml(getString(R.string.earn_bedollars_multiplescanning_nolocation)));
				/*
				 * for(MissionWrapper m : results) m.setEnabled(false);
				 */
			} else {
				((TextView) header.findViewById(R.id.multiple_scan_header_text))
						.setText(Html.fromHtml(getString(
								R.string.earn_bedollars_multiplescanning_top,
								mBrandName)));
			}
			// ((TextView)header.findViewById(R.id.multiple_scan_header_text)).setText(Html.fromHtml(getString(R.string.earn_bedollars_multiplescanning_nolocation)));
			mListView.addHeaderView(header, null, false);
			findViewById(R.id.progressBar).setVisibility(View.GONE);

			mListView.setAdapter(new MultipleScanAdapter(
					MultipleScanActivity.this, R.layout.multiple_scan_row,
					results));
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}

	private void initLocationListener() {
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				DebugUtility.showLog("LOCATION CHANGED " + location);
				if (mIsFirstLocationCheck) {
					new MultipleScanMissionTask().execute();
					mIsFirstLocationCheck = false;
				} else {

				}
				/*
				 * location = new Location("network");
				 * location.setLatitude(45.4569631);
				 * location.setLongitude(9.1939589);
				 */
				mUserLocation = location;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
				DebugUtility.showLog("PROVIDER DISABLED");
			}
		};

		// if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		// {
		// DebugUtility.showLog("PROVIDER IS EANBLED");
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, mLocationListener);
		// }
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DebugUtility.showLog("onDestroy");
		mLocationManager.removeUpdates(mLocationListener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}
}
