package com.beintoo.activities;

import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MemberLoginTask;
import com.beintoo.beclubapp.BeclubActivityChooser;
import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.DeviceBannedDialog;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DeviceId;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.ResponseWrapper;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

public class MemberLoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_login_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		final EditText etEmail = (EditText) findViewById(R.id.email);
		final EditText etPassword = (EditText) findViewById(R.id.password);
		// 点击进行登陆验证
		findViewById(R.id.get_started).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String email = etEmail.getText().toString();
						String password = etPassword.getText().toString();

						if (email.length() > 0
								&& MemberSignupActivity.isValidEmail(email)) {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									etEmail.getWindowToken(), 0);

							new MemberLoginTask(MemberLoginActivity.this,
									buildGeoAndDeviceWrapper(email, password),
									new MemberLoginTask.OnLoginListener() {
										@Override
										public void onLoginSucceed() {
											startActivity(new Intent(
													MemberLoginActivity.this,
													MainActivity.class));
											finish();
											if (BeclubActivityChooser.memberEntryActivity != null) {
												BeclubActivityChooser.memberEntryActivity
														.finish();
											}
										}

										@Override
										public void onLoginFailed(
												AsyncTasks.Result statusResult) {
											switch (statusResult) {
											case BANNED:
												new DeviceBannedDialog(
														MemberLoginActivity.this,
														false)
														.show(getSupportFragmentManager(),
																"banned-device");
												break;
											case NETWORK_ERROR:
												Toast.makeText(
														MemberLoginActivity.this,
														getString(R.string.connection_error),
														Toast.LENGTH_LONG)
														.show();
												break;
											default:
												Toast.makeText(
														MemberLoginActivity.this,
														"Incorrect password or email",
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}).execute();
						} else {
							Toast.makeText(MemberLoginActivity.this,
									"Please insert a valid email",
									Toast.LENGTH_LONG).show();
						}
					}
				});

		findViewById(R.id.forgot_password).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								MemberLoginActivity.this);

						View dialogView = View.inflate(
								MemberLoginActivity.this,
								R.layout.forgot_password_dialog, null);
						builder.setView(dialogView);
						final AlertDialog dialog = builder.create();

						final EditText email = (EditText) dialogView
								.findViewById(R.id.etEmail);

						dialogView.findViewById(R.id.btnYes)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										if (email.getText().length() > 0
												&& MemberSignupActivity
														.isValidEmail(email
																.getText())) {
											final ProgressDialog progressDialog = ProgressDialog
													.show(MemberLoginActivity.this,
															"Forgot password",
															"Please wait...",
															true, false);
											RestAdapter restAdapter = BeRestAdapter
													.getAuthKeyRestAdapter();
											AuthResource.IAuthResource service = restAdapter
													.create(AuthResource.IAuthResource.class);
											service.forgotPassword(email
													.getText().toString(),
													new Callback<Response>() {
														@Override
														public void success(
																Response response,
																Response response2) {

															Toast.makeText(
																	MemberLoginActivity.this,
																	getResources()
																			.getString(
																					R.string.forgot_password_sent),
																	Toast.LENGTH_SHORT)
																	.show();
															try {
																dialog.dismiss();
																progressDialog
																		.dismiss();
															} catch (Exception e) {
																e.printStackTrace();
															}
														}

														@Override
														public void failure(
																RetrofitError error) {

															if (error
																	.isNetworkError()) {
																Toast.makeText(
																		MemberLoginActivity.this,
																		getString(R.string.connection_error),
																		Toast.LENGTH_LONG)
																		.show();
															} else {
																ResponseWrapper responseWrapper = null;
																try {
																	responseWrapper = new Gson()
																			.fromJson(
																					new InputStreamReader(
																							error.getResponse()
																									.getBody()
																									.in()),
																					ResponseWrapper.class);
																} catch (IOException e) {
																	e.printStackTrace();
																}

																if (responseWrapper != null
																		&& responseWrapper
																				.getCode() == ApiConfiguration.BANNED_DEVICE_CODE) {
																	new DeviceBannedDialog(
																			MemberLoginActivity.this,
																			false)
																			.show(getSupportFragmentManager(),
																					"banned-device");
																} else {
																	Toast.makeText(
																			MemberLoginActivity.this,
																			"Unable to recovery password. Please contact us",
																			Toast.LENGTH_LONG)
																			.show();
																}
															}
															try {
																dialog.dismiss();
																progressDialog
																		.dismiss();
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													});
										} else {
											Toast.makeText(
													MemberLoginActivity.this,
													"Please insert a valid email",
													Toast.LENGTH_LONG).show();
										}
									}
								});

						dialogView.findViewById(R.id.btnNo).setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View view) {

										dialog.dismiss();
									}
								});

						dialog.show();
					}
				});

		Bundle b = getIntent().getExtras();
		if (b != null && b.getString("email") != null) {
			etEmail.setText(b.getString("email"));
			etPassword.requestFocus();
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	private GeoAndDeviceWrapper buildGeoAndDeviceWrapper(String email,
			String password) {
		GeoAndDeviceWrapper wrapper = new GeoAndDeviceWrapper();
		wrapper.setEmail(email);
		wrapper.setPassword(password);

		wrapper.setAndroidid(DeviceId.getAndroidId(this));
		wrapper.setImei(DeviceId.getImei(this));
		wrapper.setMacaddress(DeviceId.getMACAddress(this));
		wrapper.setLocale(DeviceId.getLocale().getDisplayLanguage());

		Location location = BeLocationManager.getInstance(this)
				.getLastKnowLocation(this);
		Double latitude = null, longitude = null;
		Double altitude = null;
		Double haccuracy = null;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();
			haccuracy = (double) location.getAccuracy();
		}

		if (latitude != null && longitude != null) {
			wrapper.setLatitude(latitude);
			wrapper.setLongitude(longitude);
			wrapper.setAltitude(altitude);
			wrapper.setHaccuracy(haccuracy);
		}

		wrapper.setMatid(MobileAppTracker.getInstance().getMatId());

		return wrapper;
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
