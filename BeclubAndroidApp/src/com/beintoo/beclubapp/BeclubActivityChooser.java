package com.beintoo.beclubapp;

import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RestAdapter;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.beintoo.activities.MemberEntryActivity;
import com.beintoo.activities.VersionTooOldActivity;
import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AppWrapper;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.MeBean;
import com.beintoo.wrappers.MemberBean;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

public class BeclubActivityChooser extends FragmentActivity {
	public static Activity memberEntryActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			MobileAppTracker.init(getApplicationContext(), "6118",
					"220879c2082e4692af59101967ca9856");
			MobileAppTracker.getInstance().setReferralSources(this);
			if (DebugUtility.isDebugEnable) {
				MobileAppTracker.getInstance().setAllowDuplicates(true);
				MobileAppTracker.getInstance().setDebugMode(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Check if the user is logged in
		 */
		try {
			final AuthBean authBean = MemberAuthStore
					.getAuth(getApplicationContext());
			final MemberBean memberBean = MemberAuthStore
					.getMember(getApplicationContext());
			if (authBean != null && authBean.getToken() != null)
				DebugUtility.showLog("TOKEN IS " + authBean.getToken());
			DebugUtility.showLog(new Gson().toJson(memberBean));

			if (authBean != null && memberBean != null) { // the user is logged in
				Intent intent = null;
				Uri uri = getIntent().getData();
				if (uri != null) {
					DebugUtility.showLog("@@@@ URI: " + uri.toString());
					DebugUtility.showLog("@@@@ Auth:" + uri.getAuthority());
					DebugUtility.showLog("@@@@ Path: " + uri.getPath());
					DebugUtility.showLog("@@@@ Params: "
							+ uri.getQueryParameter("brandID"));
					DebugUtility.showLog("@@@@ Params: "
							+ uri.getQueryParameter("missionType"));
					intent = parseURIToIntent(uri);
				} else {
					intent = new Intent(getApplicationContext(),
							MainActivity.class);
				}
				startActivity(intent);
			} else { // user is not logged in - signup
				Intent intent = new Intent(this, MemberEntryActivity.class);
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String appVersion = ApiConfiguration
							.getAppVersion(getApplicationContext());
					DebugUtility.showLog("####: App version: " + appVersion);

					Intent intent = new Intent(getApplicationContext(),
							VersionTooOldActivity.class);

					RestAdapter restAdapter = BeRestAdapter
							.getAuthKeyRestAdapter();
					AuthResource.IAuthResource service = restAdapter
							.create(AuthResource.IAuthResource.class);

					Response response = service.me();

					MeBean meBean = new Gson().fromJson(new InputStreamReader(
							response.getBody().in()), MeBean.class);
					AppWrapper app = meBean.getApp();

					if (app != null) {
						PreferenceManager
								.getDefaultSharedPreferences(
										BeclubActivityChooser.this)
								.edit()
								.putInt("maxCardsPerUser",
										app.getMaxCardsPerUser())
								.putString("spreedlyBasePath",
										app.getSpreedlyBasePath()).commit();

						if ((app.getCustomdata().getOptional() == null || (!app
								.getCustomdata().getOptional()
								.contains(appVersion)))
								&& (app.getCustomdata().getAllowed() == null || (!app
										.getCustomdata().getAllowed()
										.contains(appVersion)))) {
							startActivity(intent);
						} else if (app.getCustomdata().getOptional() != null
								&& app.getCustomdata().getOptional()
										.contains(appVersion)) {
							intent.putExtra("show_skip", true);
							startActivity(intent);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		/**
		 * tracking referrals
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AdvertisingIdClient.Info adInfo = AdvertisingIdClient
							.getAdvertisingIdInfo(getApplicationContext());
					MobileAppTracker.getInstance().setGoogleAdvertisingId(
							adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
				} catch (IOException e) {
					// Unrecoverable error connecting to Google Play services
					// (e.g.,
					// the old version of the service doesn't support getting
					// AdvertisingId).
				} catch (GooglePlayServicesNotAvailableException e) {
					// Google Play services is not available entirely.
				} catch (GooglePlayServicesRepairableException e) {
					// Encountered a recoverable error connecting to Google Play
					// services.
				}
			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobileAppTracker.getInstance().measureSession();
	}

	private Intent parseURIToIntent(Uri uri) {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.putExtra("eventtype", "URL_SCHEME_LAUNCH");
		intent.putExtra("scheme", uri.getScheme());
		intent.putExtra("authority", uri.getAuthority());
		intent.putExtra("path", uri.getPath());
		intent.putExtra("brandID", uri.getQueryParameter("brandID"));
		intent.putExtra("rewardID", uri.getQueryParameter("rewardID"));
		intent.putExtra("missionType", uri.getQueryParameter("missionType"));
		intent.putExtra("missionID", uri.getQueryParameter("missionID"));
		return intent;
	}
}