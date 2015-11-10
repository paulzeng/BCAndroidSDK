package com.beintoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

public class PostSignupActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post_signup_activity);
		getActionBar().hide();

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("email") != null) {
			((TextView) findViewById(R.id.user_email)).setText(extras
					.getString("email"));
		}

		findViewById(R.id.get_started).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(PostSignupActivity.this,
								MainActivity.class));
						finish();
					}
				});

		findViewById(R.id.why_required).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(PostSignupActivity.this,
								WhyRequired.class));
					}
				});

		MATEventItem eventItem = new MATEventItem("member-signup",
				MemberAuthStore.getMember(this).getId(), null, null, null, null);
		MobileAppTracker.getInstance()
				.measureAction("member-signup", eventItem);

		// try {
		// AppEventsLogger logger = AppEventsLogger.newLogger(this);
		// Bundle args = new Bundle();
		// args.putString("memberID", MemberAuthStore.getMember(this).getId());
		// logger.logEvent("member-signup", args);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	/**
	 * 
	 * Disabling back button such that the user must tap "get started"
	 * 
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// preventing default implementation previous to
			// android.os.Build.VERSION_CODES.ECLAIR
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
