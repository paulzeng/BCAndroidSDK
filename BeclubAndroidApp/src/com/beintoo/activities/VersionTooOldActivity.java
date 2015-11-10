package com.beintoo.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.beintoo.beclubapp.R;

public class VersionTooOldActivity extends Activity {
	boolean isMandatory = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version_too_old_activity);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getBoolean("show_skip")) {
			findViewById(R.id.skip).setVisibility(View.VISIBLE);
			isMandatory = true;
		} else {
			findViewById(R.id.skip).setVisibility(View.GONE);
		}

		findViewById(R.id.version_too_old_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final String appPackageName = getPackageName(); // getPackageName()
																		// from
																		// Context
																		// or
																		// Activity
																		// object
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse("market://details?id="
											+ appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://play.google.com/store/apps/details?id="
											+ appPackageName)));
						}

					}
				});

		findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// don't write anything here to make back button not work
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

	}
}
