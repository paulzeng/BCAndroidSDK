package com.beintoo.activities;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.ChangePasswordBean;

public class SettingsChangePassword extends BeNotificationActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_forgot_password_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		final EditText oldPassword = (EditText) findViewById(R.id.old_password);
		final EditText newPassword = (EditText) findViewById(R.id.new_password);
		final EditText newPasswordRepeat = (EditText) findViewById(R.id.new_password_repeat);

		findViewById(R.id.change_password_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (newPassword.getText().length() >= MemberSignupActivity.MIN_PASSWORD_LENGTH
								&& newPasswordRepeat.getText().length() >= MemberSignupActivity.MIN_PASSWORD_LENGTH) {
							if (newPassword
									.getText()
									.toString()
									.equals(newPasswordRepeat.getText()
											.toString())) { // new password
															// valid
								final ProgressDialog progressDialog = ProgressDialog
										.show(SettingsChangePassword.this,
												"Change password",
												"Please wait...", true, false);

								ChangePasswordBean changePasswordBean = new ChangePasswordBean();
								changePasswordBean.setOldPassword(oldPassword
										.getText().toString());
								changePasswordBean.setPassword(newPassword
										.getText().toString());

								RestAdapter restAdapter = BeRestAdapter
										.getMemberTokenRestAdapter();
								AuthResource.IAuthResource service = restAdapter
										.create(AuthResource.IAuthResource.class);

								service.changePassword(
										MemberAuthStore.getAuth(
												SettingsChangePassword.this)
												.getToken(),
										changePasswordBean,
										new Callback<AuthBean>() {
											@Override
											public void success(
													AuthBean authBean,
													Response response) {
												if (authBean.getToken() != null) {
													MemberAuthStore
															.setAuth(
																	SettingsChangePassword.this,
																	authBean);
													Toast.makeText(
															SettingsChangePassword.this,
															getResources()
																	.getString(
																			R.string.forgot_password_sent),
															Toast.LENGTH_SHORT)
															.show();
													finish();
												}
												progressDialog.dismiss();
												InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
												imm.toggleSoftInput(
														InputMethodManager.HIDE_IMPLICIT_ONLY,
														0);
											}

											@Override
											public void failure(
													RetrofitError error) {
												if (error.isNetworkError()) {
													Toast.makeText(
															SettingsChangePassword.this,
															getString(R.string.connection_error),
															Toast.LENGTH_LONG)
															.show();
												} else {
													Toast.makeText(
															SettingsChangePassword.this,
															"The old password is wrong",
															Toast.LENGTH_SHORT)
															.show();
												}
												progressDialog.dismiss();
												InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
												imm.toggleSoftInput(
														InputMethodManager.HIDE_IMPLICIT_ONLY,
														0);
											}
										});
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(
										InputMethodManager.HIDE_IMPLICIT_ONLY,
										0);
							} else {
								Toast.makeText(
										SettingsChangePassword.this,
										"The password you have inserted is incorrect",
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(
									SettingsChangePassword.this,
									"Password too short, minimum length is "
											+ MemberSignupActivity.MIN_PASSWORD_LENGTH,
									Toast.LENGTH_LONG).show();
						}

					}
				});

		((CheckBox) findViewById(R.id.checkBox))
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundButton,
							boolean b) {
						if (b) {
							oldPassword
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							newPassword
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							newPasswordRepeat
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						} else {
							oldPassword
									.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
							newPassword
									.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
							newPasswordRepeat
									.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
						}
					}
				});
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
