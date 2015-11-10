package com.beintoo.activities;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.beintoo.asynctasks.BrandPlacesTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.BeMarker;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PlacesContainerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MapStoresActivity extends BeNotificationActivity {

	private GoogleMap mMap;
	private String mBrandId, mBrandName;
	private HashMap<Marker, BeMarker> mPlaces;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_stores_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mPlaces = new HashMap<Marker, BeMarker>();

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("brand") != null) {
			mBrandId = extras.getString("brand");
		}

		if (extras != null && extras.getString("brandName") != null) {
			mBrandName = extras.getString("brandName");
			setTitle(extras.getString("brandName"));
		}

		if (mMap != null) {
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			Location location = BeLocationManager.getInstance(this)
					.getLastKnowLocation(this);
			if (location != null) {
				LatLng userLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				try {
					mPlaces.put(mMap.addMarker(new MarkerOptions()
							.position(userLocation)
							.title("You are here")
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker))
							.visible(true)), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,
						12));
			}

			final ProgressDialog dialog = ProgressDialog.show(this, "Loading",
					"Please wait...", true, false);

			new BrandPlacesTask(this, mBrandId, null, null,
					new BrandPlacesTask.OnBrandPlacesListener() {
						@Override
						public void onBrandPlacesLoaded(
								PaginatedList<PlacesContainerWrapper> places) {
							for (PlacesContainerWrapper p : places.getList()) {
								if (p.getPlaces().getLatitude() != null
										&& p.getPlaces().getLongitude() != null) {
									LatLng userLocation = new LatLng(p
											.getPlaces().getLatitude(), p
											.getPlaces().getLongitude());
									final MarkerOptions placeMarker = new MarkerOptions()
											.position(userLocation).title(
													p.getPlaces().getAddress());
									if (p.getPlaces().getImagemarker() != null) {
										try {
											placeMarker
													.icon(BitmapDescriptorFactory
															.fromBitmap(p
																	.getPlaces()
																	.getImageMarkerBitmap()));
											BeMarker beMarker = new BeMarker(
													mMap.addMarker(placeMarker),
													p);
											mPlaces.put(beMarker.getMarker(),
													beMarker);
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
										try {
											placeMarker
													.icon(BitmapDescriptorFactory
															.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
										} catch (Exception e) {
											e.printStackTrace();
										}
										BeMarker beMarker = new BeMarker(mMap
												.addMarker(placeMarker), p);
										mPlaces.put(beMarker.getMarker(),
												beMarker);
									}
								}
							}

							try {
								dialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onError() {
							Toast.makeText(MapStoresActivity.this,
									getString(R.string.something_wrong),
									Toast.LENGTH_SHORT).show();
							try {
								dialog.dismiss();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).execute();
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
	protected void onStop() {
		super.onStop();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}
}