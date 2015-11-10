package com.beintoo.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.BrandPlacesTask;
import com.beintoo.asynctasks.MissionCompleteTask;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.WidgetBedollars;
import com.beintoo.dialogs.DeviceBannedDialog;
import com.beintoo.dialogs.MissionUnlockedDialog;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.DeviceId;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlacesContainerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WalkinMissionActivity extends BeNotificationActivity {

	private final int DEFAULT_UNLOCK_DISTANCE = 60;

	private int UNLOCK_DISTANCE = DEFAULT_UNLOCK_DISTANCE;

	private GoogleMap mMap;

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private Marker userMarker;
	private List<PlacesContainerWrapper> mAvailablePlaces;
	private boolean mIsActionButtonEnabled = false;
	private boolean hasMovedMap = false;
	private LatLngBounds.Builder mBoundsBuilder = new LatLngBounds.Builder();
	private ProgressDialog mLocationDialog;
	private String mNearestPlace = null;

	private String brandID, brandName;
	private Location mCurrentBestLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkin_mission_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		final Bundle extras = getIntent().getExtras();
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (mMap != null)
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		if (extras.getString("brand_id") != null) {
			brandID = extras.getString("brand_id");
		}
		if (extras.getString("brand_name") != null) {
			brandName = extras.getString("brand_name");
			setTitle(extras.getString("brand_name"));
		}

		changeActionButtonState(false);
		((TextView) findViewById(R.id.action_button_text))
				.setText(getString(R.string.earn_bedollars_action_button_checkin));

		mLocationDialog = ProgressDialog.show(this, null,
				"Searching for your location...");
		mLocationDialog.setCancelable(true);

		findViewById(R.id.action_button_content).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mIsActionButtonEnabled
								&& !BeUtils
										.isMockLocationEnabled(WalkinMissionActivity.this)) {
							new MissionCompleteTask(
									WalkinMissionActivity.this,
									mCurrentBestLocation,
									new MissionCompleteTask.MissionCompleteCallback() {
										@Override
										public void onCompleted(
												AssignedMissionWrapper results) {
											try {

												WidgetBedollars
														.updateWidgetBalance(WalkinMissionActivity.this);

											} catch (Exception e) {
												e.printStackTrace();
											}

											try {
												new MissionUnlockedDialog(
														WalkinMissionActivity.this,
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
																		WalkinMissionActivity.this)
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
												// AppEventsLogger.newLogger(WalkinMissionActivity.this);
												// Bundle args = new Bundle();
												// args.putString("memberID",
												// MemberAuthStore.getMember(WalkinMissionActivity.this).getId());
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
														WalkinMissionActivity.this,
														true)
														.show(getSupportFragmentManager(),
																"banned-device");
												break;
											case NETWORK_ERROR:
												Toast.makeText(
														WalkinMissionActivity.this,
														getString(R.string.connection_error),
														Toast.LENGTH_LONG)
														.show();
												break;
											default:
												Toast.makeText(
														WalkinMissionActivity.this,
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
									}).execute(new String[] {
									extras.getString("mission_id"),
									mNearestPlace });

							mLocationManager.removeUpdates(mLocationListener);
							changeActionButtonState(false);
							mIsActionButtonEnabled = false;

						}
					}
				});

		ArrayList<String> typesList = new ArrayList<String>();
		typesList.add(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN
				.toString());
		typesList.add(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME
				.toString());

		if (mMap != null) {
			new BrandPlacesTask(this, null, typesList,
					extras.getString("mission_id"),
					new BrandPlacesTask.OnBrandPlacesListener() {
						@Override
						public void onBrandPlacesLoaded(
								PaginatedList<PlacesContainerWrapper> places) {
							if (places != null && places.getList() != null
									&& places.getList().size() > 0) {
								mAvailablePlaces = places.getList();

								if (mMap != null) {
									for (PlacesContainerWrapper place : places
											.getList()) {
										try {
											LatLng placeLocation = new LatLng(
													place.getPlaces()
															.getLatitude(),
													place.getPlaces()
															.getLongitude());
											final MarkerOptions placeMarker = new MarkerOptions()
													.position(placeLocation)
													.title(place.getPlaces()
															.getName());

											if (place.getPlaces()
													.getImagemarker() != null) {
												try {
													placeMarker
															.icon(BitmapDescriptorFactory
																	.fromBitmap(place
																			.getPlaces()
																			.getImageMarkerBitmap()));
													mMap.addMarker(placeMarker);
												} catch (Exception e) {
													e.printStackTrace();
												}

											} else {
												try {
													placeMarker
															.icon(BitmapDescriptorFactory
																	.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
													mMap.addMarker(placeMarker);
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

										} catch (Exception e) {
											e.printStackTrace();
										}

										/**
										 * INIT LOCATION TRACKING, MARKERS AND
										 * USER LOCATION
										 */
										initLocationListener();

										/**
										 * SETTING THE CAMERA CHANGE LISTENER TO
										 * REMOVE THE CENTERING BEFORE THE USERs
										 * TOUCH
										 */
										new Handler().postDelayed(
												new Runnable() {
													@Override
													public void run() {
														mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
															@Override
															public void onCameraChange(
																	CameraPosition cameraPosition) {
																if (!hasMovedMap) {
																	hasMovedMap = true;
																}
															}
														});
													}
												}, 3000);
									}
								}
							}
						}

						@Override
						public void onError() {
							Toast.makeText(WalkinMissionActivity.this,
									getString(R.string.something_wrong),
									Toast.LENGTH_SHORT).show();
						}
					}).execute();
		}
		LinearLayout llRequestSupport = (LinearLayout) findViewById(R.id.ll_request_support);
		llRequestSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String location = "Not Available";
				try {
					location = mLocationManager.getLastKnownLocation("network")
							.getLatitude()
							+ "/"
							+ mLocationManager.getLastKnownLocation("network")
									.getLongitude();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("message/rfc822");
					intent.putExtra(Intent.EXTRA_EMAIL,
							new String[] { "support@beclub.com" });
					intent.putExtra(Intent.EXTRA_SUBJECT,
							"I'm having location trouble");
					intent.putExtra(
							Intent.EXTRA_TEXT,
							"\n\n\nThe information below helps us fix this problem. Please don't delete it!\n\n"
									+ extras.getString("mission_id")
									+ "\n\n"
									+ DeviceId
											.getAndroidId(WalkinMissionActivity.this)
									+ "\n\n"
									+ MemberAuthStore.getMember(
											WalkinMissionActivity.this).getId()
									+ "\n\n" + location);
					startActivity(Intent.createChooser(intent, "Send Email"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initLocationListener() {
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new LocationListener() {
			public void onLocationChanged(Location location) {

				if (mMap == null) {
					return;
				}

				if (isBetterLocation(location, mCurrentBestLocation)) {
					mCurrentBestLocation = location;
					// DebugUtility.showLog("####: Best location: " +
					// location.toString());
				} else {
					// DebugUtility.showLog("####: Bad location: " +
					// location.toString());
					return;
				}

				if (userMarker != null) {
					userMarker.remove();
				}
				// DebugUtility.showLog("GOT NEW LOCATION " +
				// location.toString() + " DATE: " + new
				// Date(location.getTime()));

				// location =
				// BeLocationManager.getInstance(WalkinMissionActivity.this).getLastKnowLocation();
				// // faking the location

				LatLng userLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				MarkerOptions userMarkerOptions = new MarkerOptions()
						.position(userLocation)
						.title("You are here")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker));
				userMarker = mMap.addMarker(userMarkerOptions);
				mBoundsBuilder.include(userMarker.getPosition());

				boolean isInStore = false;
				for (PlacesContainerWrapper pcw : mAvailablePlaces) {
					Location l = new Location("network");
					l.setLatitude(pcw.getPlaces().getLatitude());
					l.setLongitude(pcw.getPlaces().getLongitude());

					mBoundsBuilder.include(new LatLng(l.getLatitude(), l
							.getLongitude()));

					Double dist = pcw.getPlaces().getRadius() != null ? pcw
							.getPlaces().getRadius() : UNLOCK_DISTANCE;

					if (l.distanceTo(location) < dist.floatValue()) {
						isInStore = true;
						userIsNextToTheStore(pcw.getPlaces().getId());
						break;
					}
				}

				if (!isInStore) {
					userIsNotInStore();
				}

				if (!hasMovedMap) {
					DebugUtility.showLog("---> "
							+ mBoundsBuilder.build().describeContents());
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
							mBoundsBuilder.build(), 100));
				}

				if (mLocationDialog.isShowing()) {
					mLocationDialog.dismiss();
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		}
	}

	private void userIsNotInStore() {
		changeActionButtonState(false);
		mIsActionButtonEnabled = false;
	}

	private void userIsNextToTheStore(String placeId) {
		changeActionButtonState(true);
		mIsActionButtonEnabled = true;
		mNearestPlace = placeId;
	}

	private void changeActionButtonState(boolean enabled) {
		try {
			if (!enabled) {
				((RelativeLayout) findViewById(R.id.action_button_content))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.action_button_content)),
								this.getResources().getDrawable(
										R.drawable.disabled_button_selector));
				((TextView) findViewById(R.id.action_button_text))
						.setTextColor(Color.WHITE);
				((TextView) findViewById(R.id.action_button_text))
						.setText(getString(R.string.earn_bedollars_action_button_checkin_fail));
			} else {
				((RelativeLayout) findViewById(R.id.action_button_content))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.action_button_content)),
								this.getResources().getDrawable(
										R.drawable.green_button));
				((TextView) findViewById(R.id.action_button_text))
						.setTextColor(Color.WHITE);
				((TextView) findViewById(R.id.action_button_text))
						.setText(getString(R.string.earn_bedollars_action_button_checkin));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mLocationManager.removeUpdates(mLocationListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		long TWO_MINUTES = 2 * 60 * 1000;

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	protected void onStop() {
		super.onStop();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
