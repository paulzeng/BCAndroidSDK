package com.beintoo.utils;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.beintoo.utils.notification.NotificationID;
import com.beintoo.utils.notification.NotificationTimeStore;
import com.beintoo.wrappers.AppWrapper;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.MemberBean;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.gson.Gson;

public class MemberAuthStore {

	public static String advertiseID;
	public static boolean advertiseDisable;

	public static String MEMBER_KEY = "member";
	public static String AUTH_KEY = "auth";
	public static String APP_KEY = "app";

	public static void collectAdvertiseSettings(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AdvertisingIdClient.Info info = AdvertisingIdClient
							.getAdvertisingIdInfo(context);
					MemberAuthStore.advertiseID = info.getId();
					MemberAuthStore.advertiseDisable = info
							.isLimitAdTrackingEnabled();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				} catch (GooglePlayServicesRepairableException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void setMember(Context context, MemberBean member) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			sharedPrefs.edit().putString(MEMBER_KEY, new Gson().toJson(member))
					.commit();

			DebugUtility.showLog("SAVED MEMBER");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MemberBean getMember(Context context) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String member = sharedPrefs.getString(MEMBER_KEY, null);

			if (member != null) {
				return new Gson().fromJson(member, MemberBean.class);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static void updateMemberBedollarsBalance(Context context,
			Double bedollars) {
		MemberBean member = getMember(context);
		if (member != null && bedollars != null) {
			member.setBedollars(bedollars);

			setMember(context, member);

			DebugUtility.showLog("UPDATED USER BALANCE TO " + bedollars);
		}
	}

	public static boolean isUserVerified(Context context) {
		if (getMember(context) != null)
			return getMember(context).isEmailverified();
		else
			return false;
	}

	public static void setAuth(Context context, AuthBean auth) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			sharedPrefs.edit().putString(AUTH_KEY, new Gson().toJson(auth))
					.commit();

			DebugUtility.showLog("SAVED AUTH");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static AuthBean getAuth(Context context) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String member = sharedPrefs.getString(AUTH_KEY, null);

			if (member != null) {
				return new Gson().fromJson(member, AuthBean.class);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static void logoutMember(Context context) {
		MemberAuthStore.setMember(context, null);
		MemberAuthStore.setAuth(context, null);
		MemberAuthStore.setApp(context, null);
		DataStore.setTwitterToken(context, null);
		// FacebookManager.facebookLogout(context);
		NotificationTimeStore.setWhenTriggerNotification(
				NotificationID.WAKE_UP_GIMBAL_SDK.getId(), context, 0);
	}

	public static void setApp(Context context, AppWrapper app) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			sharedPrefs.edit().putString(APP_KEY, new Gson().toJson(app))
					.commit();

			DebugUtility.showLog("SAVED APP");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static AppWrapper getApp(Context context) {
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String app = sharedPrefs.getString(APP_KEY, null);

			if (app != null) {
				return new Gson().fromJson(app, AppWrapper.class);
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static boolean hasLocationBaseNotificationEnable(Context context) {
		try {
			return getMember(context).getMemberssettings().get(0)
					.getLocationbasedmessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
