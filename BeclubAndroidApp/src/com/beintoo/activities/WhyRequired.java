package com.beintoo.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.beintoo.beclubapp.R;

public class WhyRequired extends BeNotificationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		setTitle(getString(R.string.postsignup_why_verification));

		float scale = getResources().getDisplayMetrics().density;
		TextView content = new TextView(this);
		content.setText(getString(R.string.postsignup_why_verification_text));
		content.setPadding((int) (10 * scale + 0.5f),
				(int) (20 * scale + 0.5f), (int) (10 * scale + 0.5f),
				(int) (20 * scale + 0.5f));

		setContentView(content);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}
}
