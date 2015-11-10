package com.beintoo.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.beintoo.adapters.TellAFriendAdapter;
import com.beintoo.beclubapp.R;
import com.beintoo.social.GPlusManager;
import com.beintoo.utils.DebugUtility;

@SuppressLint("NewApi")
public class TellAFriendActivity extends BeNotificationActivity implements
		AdapterView.OnItemClickListener {

	private Bundle mSavedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mSavedInstanceState = savedInstanceState;

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		setTitle(R.string.tell_a_friend);
		setContentView(R.layout.tell_a_friend_activity);

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		// Facebook
		Map<String, String> facebook = new HashMap<String, String>();
		facebook.put("name", getString(R.string.tell_a_friend_facebook_title));
		facebook.put("description",
				getString(R.string.tell_a_friend_facebook_desc));
		facebook.put("icon", "share_fb");
		// Twitter
		Map<String, String> twitter = new HashMap<String, String>();
		twitter.put("name", getString(R.string.tell_a_friend_twitter_title));
		twitter.put("description",
				getString(R.string.tell_a_friend_twitter_desc));
		twitter.put("icon", "share_twitter");
		// SMS
		Map<String, String> sms = new HashMap<String, String>();
		sms.put("name", getString(R.string.tell_a_friend_sms_title));
		sms.put("description", getString(R.string.tell_a_friend_sms_desc));
		sms.put("icon", "share_message");
		// Email
		Map<String, String> email = new HashMap<String, String>();
		email.put("name", getString(R.string.tell_a_friend_email_title));
		email.put("description", getString(R.string.tell_a_friend_email_desc));
		email.put("icon", "share_mail");

		// Email
		Map<String, String> gplus = new HashMap<String, String>();
		gplus.put("name", getString(R.string.tell_a_friend_gplus_title));
		gplus.put("description", getString(R.string.tell_a_friend_gplus_desc));
		gplus.put("icon", "share_google");

		data.add(facebook);
		data.add(twitter);
		data.add(sms);
		data.add(email);
		data.add(gplus);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(new TellAFriendAdapter(this,
				R.layout.brand_mission_row, data));
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if (i == 0) {

		} else if (i == 1) {
			// share("twitter");

		} else if (i == 2) {
			try {
				Intent smsIntent = new Intent();
				smsIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"I've just registered to the BeClub, a really exciting new way to earn great rewards and discounts on my smartphone. Check it out! www.beclub.com");

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // At
																			// least
																			// KitKat
					String defaultSmsPackageName = Telephony.Sms
							.getDefaultSmsPackage(this); // Need to change the
															// build to API 19
					smsIntent.setAction(Intent.ACTION_SEND);
					smsIntent.setType("text/plain");

					if (defaultSmsPackageName != null) {// Can be null in case
														// that there is no
														// default, then the
														// user would be able to
														// choose any app that
														// support this intent.
						smsIntent.setPackage(defaultSmsPackageName);
					}
				} else {
					smsIntent.setAction(Intent.ACTION_VIEW);
					smsIntent.setType("vnd.android-dir/mms-sms");
				}
				startActivity(smsIntent);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (i == 3) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/html");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Heard about the BeClub?");
			intent.putExtra(
					Intent.EXTRA_TEXT,
					"I've just registered to the BeClub, a really exciting new way to earn great rewards and discounts on my smartphone. Check it out! www.beclub.com");
			startActivity(Intent.createChooser(intent, "Send Email"));

		} else if (i == 4) {
			GPlusManager
					.shareStatus(
							this,
							"Heard about the BeClub?",
							"I've just registered to the BeClub, a really exciting new way to earn great rewards and discounts on my smartphone. Check it out! www.beclub.com");

		}
	}

	private void share(String nameApp) {
		try {
			DebugUtility.showLog("SHARE " + nameApp);
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.putExtra(
					Intent.EXTRA_TEXT,
					"I just registered to the BeClub, a really exciting way to earn great rewards. Check it out! Www.BeClub.com");
			share.setType("text/plain");
			List<ResolveInfo> resInfo = getPackageManager()
					.queryIntentActivities(share, 0);
			boolean resolved = false;
			for (ResolveInfo resolveInfo : resInfo) {
				if (resolveInfo.activityInfo.packageName.contains(nameApp)) {
					share.setClassName(resolveInfo.activityInfo.packageName,
							resolveInfo.activityInfo.name);
					resolved = true;
					break;
				}
			}
			if (resolved) {
				startActivity(share);
			} else {
				Toast.makeText(this, nameApp + " app isn't found",
						Toast.LENGTH_LONG).show();
			}
		}

		catch (Exception e) {
			Log.v("VM",
					"Exception while sending image on" + nameApp + " "
							+ e.getMessage());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == 64206) { // FACEBOOK LOGIN
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
		// } else
		if (requestCode == 64207 && resultCode == RESULT_OK) { // Facebook
			// FacebookManager.getIstance().facebookUiHelperActivityResultHandler(
			// requestCode, resultCode, data);
		}
	}
}
