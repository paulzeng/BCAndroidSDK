package com.beintoo.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MemberLogoutTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MemberBean;

public class SettingsActivity extends BeNotificationActivity implements
		View.OnClickListener {

	public final static int REQUEST_LOGOUT = 100;
	public final static int REQUEST_DELETE_ACCOUNT = 101;
	public final static int REQUEST_UPDATE_SETTINGS = 110;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_main_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		if (getIntent().getExtras() != null
				&& getIntent().getBooleanExtra("userprofile", false)) {
			startActivityForResult(new Intent(this,
					SettingsEditProfileActivity.class), REQUEST_DELETE_ACCOUNT);
		}

		((TextView) findViewById(R.id.action_button_text))
				.setText(getString(R.string.logout));
		findViewById(R.id.action_button_content).setOnClickListener(this);
		findViewById(R.id.tv_account_info).setOnClickListener(this);
		findViewById(R.id.tv_change_password).setOnClickListener(this);
		findViewById(R.id.tv_push_notification).setOnClickListener(this);

		MemberBean me = MemberAuthStore.getMember(this);
		if (me.getHasfbprovider() != null && me.getHasfbprovider()) {
			findViewById(R.id.tv_change_password).setVisibility(View.GONE);
			findViewById(R.id.ll_divider).setVisibility(View.GONE);
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_account_info:
			startActivityForResult(new Intent(this,
					SettingsEditProfileActivity.class), REQUEST_DELETE_ACCOUNT);
			break;
		case R.id.tv_change_password:
			startActivity(new Intent(this, SettingsChangePassword.class));
			break;
		case R.id.tv_push_notification:
			startActivityForResult(new Intent(this,
					SettingsPushNotificationsActivity.class),
					REQUEST_UPDATE_SETTINGS);
			break;
		case R.id.action_button_content:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final AlertDialog dialog = builder.create();

			View dialogView = View.inflate(this, R.layout.logout_dialog, null);

			Button btnYes = (Button) dialogView.findViewById(R.id.btnYes);
			btnYes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					new MemberLogoutTask(SettingsActivity.this,
							new MemberLogoutTask.OnLogoutListener() {
								@Override
								public void onLogoutSucceed() {
									dialog.dismiss();
									MemberAuthStore
											.logoutMember(SettingsActivity.this);
									setResult(Activity.RESULT_OK);
									startActivity(new Intent(
											SettingsActivity.this,
											MemberEntryActivity.class));
									finish();
								}

								@Override
								public void onLogoutFailed(
										AsyncTasks.Result errorResult) {
									dialog.dismiss();
									switch (errorResult) {
									case NETWORK_ERROR:
										Toast.makeText(
												SettingsActivity.this,
												getString(R.string.connection_error),
												Toast.LENGTH_LONG).show();
										break;
									default:
										Toast.makeText(
												SettingsActivity.this,
												"Unable to logout. Check your internet connection and retry.",
												Toast.LENGTH_LONG).show();
									}
								}
							}).execute();
				}
			});

			dialog.setView(dialogView);
			dialog.show();

			Button btnNo = (Button) dialogView.findViewById(R.id.btnNo);
			btnNo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_DELETE_ACCOUNT && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
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

}