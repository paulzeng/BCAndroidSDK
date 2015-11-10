package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.FacebookConnectTask;
import com.beintoo.asynctasks.MemberSignupTask;
import com.beintoo.beclubapp.BeclubActivityChooser;
import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.DatePickerFragment;
import com.beintoo.dialogs.DeviceBannedDialog;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.DeviceId;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.MemberSignupBeClubBean;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

public class MemberSignupActivity extends FragmentActivity {

	private boolean isValid = true;
	public final static int MIN_PASSWORD_LENGTH = 8;
	private Date mLastSelectedBirthday = null;
	private boolean isCoppaCompliant = true;
	private final String mScreenName = "SIGNUP";
	private MemberBean mMember;
	private boolean isFromFacebook = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.member_signup_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		checkFruitNinjaDialog();

		final EditText email = (EditText) findViewById(R.id.email);
		final EditText password = (EditText) findViewById(R.id.password);
		final EditText firstname = (EditText) findViewById(R.id.firstname);
		final EditText lastname = (EditText) findViewById(R.id.lastname);
		final EditText birthday = (EditText) findViewById(R.id.birthday);

		final RadioButton male = (RadioButton) findViewById(R.id.male);
		final RadioButton female = (RadioButton) findViewById(R.id.female);

		final RelativeLayout registerNow = (RelativeLayout) findViewById(R.id.register_now);
		final TextView registerDisclaimer = (TextView) findViewById(R.id.register_disclaimer);
		registerDisclaimer.setText(Html
				.fromHtml(getString(R.string.register_disclaimer)));
		registerDisclaimer.setMovementMethod(LinkMovementMethod.getInstance());

		/**
		 * if we have the member data we are coming from the facebook signup
		 * where facebook hasn't provided also the informations we need
		 */
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getString("member") != null) {
			mMember = new Gson().fromJson(bundle.getString("member"),
					MemberBean.class);
			password.setVisibility(View.GONE);
			findViewById(R.id.facebook_login).setVisibility(View.GONE);
			findViewById(R.id.fb_informations_missing).setVisibility(
					View.VISIBLE);

			if (mMember.getEmail() != null) {
				email.setText(mMember.getEmail());
				email.setEnabled(false);
			}
			if (mMember.getName() != null) {
				firstname.setText(mMember.getName());
				firstname.setEnabled(false);
			}
			if (mMember.getSurname() != null) {
				lastname.setText(mMember.getSurname());
				lastname.setEnabled(false);
			}
			if (mMember.getDob() != null) {
				try {
					SimpleDateFormat inputFormat = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					mLastSelectedBirthday = inputFormat.parse(mMember.getDob());
					String date = DateFormat.getDateInstance(DateFormat.SHORT,
							Locale.getDefault()).format(mLastSelectedBirthday);

					birthday.setText(date);
					birthday.setEnabled(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mMember.getGender() != null) {
				if (mMember.getGender().equals("M")) {
					male.setChecked(true);
					female.setChecked(false);
				} else if (mMember.getGender().equals("F")) {
					female.setChecked(true);
					male.setChecked(false);
				}
			}

			isFromFacebook = true;
		} else {
			mMember = new MemberBean();
		}

		findViewById(R.id.facebook_login_right).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							List<String> permissions = new ArrayList<String>();
							permissions.add("email");
							permissions.add("user_birthday");
							permissions.add("user_location");
							// FacebookManager.facebookLogin(
							// MemberSignupActivity.this, permissions,
							// false);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

		final Map<String, String> params = new HashMap<String, String>();

		male.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (female.isChecked()) {
					female.setChecked(false);
				}

				findViewById(R.id.error_gender).setVisibility(View.GONE);

				params.put("gender", "M");
				mMember.setGender("M");
			}
		});

		female.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (male.isChecked()) {
					male.setChecked(false);
				}

				findViewById(R.id.error_gender).setVisibility(View.GONE);

				params.put("gender", "F");
				mMember.setGender("F");
			}
		});

		birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b) {
					final DateFormat df = DateFormat.getDateInstance(
							DateFormat.SHORT, Locale.getDefault());
					final DatePickerFragment datePicker = new DatePickerFragment(
							new DatePickerFragment.DateListener() {
								@Override
								public void onDatePicked(Date date) {
									birthday.setText(df.format(date));
									if (isFromFacebook) {
										SimpleDateFormat outputFormat = new SimpleDateFormat(
												"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
										mMember.setDob(outputFormat
												.format(new Date(date.getTime())));
									} else {
										params.put("birthday",
												"" + date.getTime());
										mMember.setDob(date.getTime() + "");
									}

									birthday.clearFocus();

									mLastSelectedBirthday = date;
								}
							}, mLastSelectedBirthday, false);

					datePicker.show(getSupportFragmentManager(), "datePicker");
				}
			}
		});

		registerNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				resetErrors();

				if (!isValidEmail(email.getText())) {
					isValid = false;
					findViewById(R.id.error_email).setVisibility(View.VISIBLE);
				} else {
					params.put("email", email.getText().toString());
					mMember.setEmail(email.getText().toString());
				}

				DebugUtility.showLog("email " + isValid);

				if (!isFromFacebook
						&& password.getText().length() < MIN_PASSWORD_LENGTH) {
					isValid = false;

					findViewById(R.id.error_password).setVisibility(
							View.VISIBLE);
				} else {
					if (!isFromFacebook) {
						params.put("password", password.getText().toString());
						mMember.setPassword(password.getText().toString());
					}
				}

				DebugUtility.showLog("pass " + isValid);

				if (firstname.length() < 2) {
					isValid = false;
					findViewById(R.id.error_firstname).setVisibility(
							View.VISIBLE);
				} else {
					params.put("firstname", firstname.getText().toString());
					mMember.setName(firstname.getText().toString());
				}

				DebugUtility.showLog("fname " + isValid);

				if (lastname.length() < 2) {
					isValid = false;
					findViewById(R.id.error_lastname).setVisibility(
							View.VISIBLE);
				} else {
					params.put("lastname", lastname.getText().toString());
					mMember.setSurname(lastname.getText().toString());
				}

				DebugUtility.showLog("lname " + isValid);

				if (birthday.length() == 0) {
					isValid = false;
					findViewById(R.id.error_birthday).setVisibility(
							View.VISIBLE);
				} else {
					final Calendar c = Calendar.getInstance();
					c.add(Calendar.YEAR, -13);

					if (mLastSelectedBirthday != null
							&& mLastSelectedBirthday.after(c.getTime())) {
						isCoppaCompliant = false;
					}
				}

				DebugUtility.showLog("bday " + isValid);

				if (!male.isChecked() && !female.isChecked()) {
					isValid = false;
					findViewById(R.id.error_gender).setVisibility(View.VISIBLE);
				}

				DebugUtility.showLog("gender " + isValid);

				DebugUtility.showLog("params " + params.toString());

				if (!isCoppaCompliant) {

					Toast.makeText(MemberSignupActivity.this,
							getString(R.string.account_coppa_compliant),
							Toast.LENGTH_LONG).show();

					return;
				}

				if (isValid) {
					if (!isFromFacebook) {
						new MemberSignupTask(MemberSignupActivity.this,
								buildMemberSignupBean(mMember),
								new MemberSignupTask.OnSignupListener() {
									@Override
									public void onSignupSucceed(String email) {
										Intent intent = new Intent(
												MemberSignupActivity.this,
												PostSignupActivity.class);
										intent.putExtra("email", email);
										startActivity(intent);
										finish();
										if (BeclubActivityChooser.memberEntryActivity != null) {
											BeclubActivityChooser.memberEntryActivity
													.finish();
										}
									}

									@Override
									public void onSignupFailed(
											AsyncTasks.Result errorResult) {
										switch (errorResult) {
										case NOT_AVAILABLE:
											Toast.makeText(
													MemberSignupActivity.this,
													getString(R.string.account_email_already_registered),
													Toast.LENGTH_LONG).show();
											break;
										case BANNED:
											new DeviceBannedDialog(
													MemberSignupActivity.this,
													false)
													.show(getSupportFragmentManager(),
															"banned-device");
											break;
										case OTHER:
											Toast.makeText(
													MemberSignupActivity.this,
													getString(R.string.email_not_valid),
													Toast.LENGTH_LONG).show();
											break;
										case FAILED:
											Toast.makeText(
													MemberSignupActivity.this,
													getString(R.string.account_coppa_compliant),
													Toast.LENGTH_LONG).show();
											break;
										case NETWORK_ERROR:
											Toast.makeText(
													MemberSignupActivity.this,
													getString(R.string.connection_error),
													Toast.LENGTH_LONG).show();
											break;
										}
									}
								}).execute();
					} else {
						new FacebookConnectTask(
								MemberSignupActivity.this,
								buildMemberFacebookSignup(mMember),
								new FacebookConnectTask.OnFacebookLoginListener() {
									@Override
									public void onFacebookLoginNewMember(
											String email) {
										Intent intent = new Intent(
												MemberSignupActivity.this,
												PostSignupActivity.class);
										intent.putExtra("email", email);
										startActivity(intent);
										finish();
										if (BeclubActivityChooser.memberEntryActivity != null) {
											BeclubActivityChooser.memberEntryActivity
													.finish();
										}
									}

									@Override
									public void onFacebookLoginSucceed() {
										DebugUtility.showLog("FB CONNECT OK");
										if (BeclubActivityChooser.memberEntryActivity != null) {
											finish();
											BeclubActivityChooser.memberEntryActivity
													.finish();
										}
										startActivity(new Intent(
												MemberSignupActivity.this,
												MainActivity.class));
									}

									@Override
									public void onFacebookLoginMissingInfo(
											MemberBean result) {
										DebugUtility
												.showLog("FB CONNECT MISSING PARAMS");

										/*
										 * This callback should never happen in
										 * this api call.
										 */
										// Intent intent = new
										// Intent(MemberSignupActivity.this,
										// MemberSignupActivity.class);
										// intent.putExtra("member",
										// result.toString());
										// startActivity(intent);
									}

									@Override
									public void onFacebookLoginFailed(
											AsyncTasks.Result statusResult) {
										switch (statusResult) {
										case BANNED:
											new DeviceBannedDialog(
													MemberSignupActivity.this,
													false)
													.show(getSupportFragmentManager(),
															"banned-device");
											break;
										case NETWORK_ERROR:
											Toast.makeText(
													MemberSignupActivity.this,
													getString(R.string.connection_error),
													Toast.LENGTH_LONG).show();
											break;
										default:
											Toast.makeText(
													MemberSignupActivity.this,
													"Unable to login with Facebook",
													Toast.LENGTH_LONG).show();
										}
									}
								}).execute();
					}
				}
			}
		});

		email.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (isValidEmail(charSequence.toString()))
					findViewById(R.id.error_email).setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b && email.length() > 0) {
					findViewById(R.id.warning_email)
							.setVisibility(View.VISIBLE);
				}
			}
		});

		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (charSequence.length() >= MIN_PASSWORD_LENGTH) {
					findViewById(R.id.error_password).setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		firstname.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (charSequence.length() >= 3) {
					findViewById(R.id.error_firstname).setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		lastname.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (charSequence.length() >= 3) {
					findViewById(R.id.error_lastname).setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		birthday.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (charSequence.length() >= 0) {
					findViewById(R.id.error_birthday).setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});
	}

	private void resetErrors() {
		isValid = true;
		isCoppaCompliant = true;
		findViewById(R.id.error_email).setVisibility(View.GONE);
		findViewById(R.id.error_password).setVisibility(View.GONE);
		findViewById(R.id.error_firstname).setVisibility(View.GONE);
		findViewById(R.id.error_lastname).setVisibility(View.GONE);
		findViewById(R.id.error_birthday).setVisibility(View.GONE);
		findViewById(R.id.error_gender).setVisibility(View.GONE);
	}

	private MemberBean buildMemberFacebookSignup(MemberBean memberBean) {
		memberBean.setAndroidid(DeviceId.getAndroidId(this));
		memberBean.setImei(DeviceId.getImei(this));
		memberBean.setMacaddress(DeviceId.getMACAddress(this));
		memberBean.setLocale(DeviceId.getLocale().getDisplayLanguage());

		Location location = BeLocationManager.getInstance(this)
				.getLastKnowLocation(this);
		Double latitude = null, longitude = null;
		Double altitude = null;
		Float haccuracy = null;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();
			haccuracy = location.getAccuracy();
		}

		if (latitude != null && longitude != null) {
			memberBean.setLatitude(latitude);
			memberBean.setLongitude(longitude);
			memberBean.setAltitude(altitude);
			memberBean.setHaccuracy(haccuracy);
		}

		memberBean.setMatid(MobileAppTracker.getInstance().getMatId());
		memberBean.setImagebig(null);
		memberBean.setImagesmall(null);

		return memberBean;
	}

	private MemberSignupBeClubBean buildMemberSignupBean(MemberBean memberBean) {
		MemberSignupBeClubBean beClubBean = new MemberSignupBeClubBean();
		beClubBean.setAndroidid(DeviceId.getAndroidId(this));
		beClubBean.setImei(DeviceId.getImei(this));
		beClubBean.setMacaddress(DeviceId.getMACAddress(this));
		beClubBean.setLocale(DeviceId.getLocale().getDisplayLanguage());

		Location location = BeLocationManager.getInstance(this)
				.getLastKnowLocation(this);
		Double latitude = null, longitude = null;
		Double altitude = null;
		Float haccuracy = null;
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			altitude = location.getAltitude();
			haccuracy = location.getAccuracy();
		}

		if (latitude != null && longitude != null) {
			beClubBean.setLatitude(latitude);
			beClubBean.setLongitude(longitude);
			beClubBean.setAltitude(altitude);
			beClubBean.setHaccuracy(haccuracy);
		}

		try {
			beClubBean.setMatid(MobileAppTracker.getInstance().getMatId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		beClubBean.setEmail(memberBean.getEmail());
		beClubBean.setName(memberBean.getName());
		beClubBean.setSurname(memberBean.getSurname());
		beClubBean.setPassword(memberBean.getPassword());
		beClubBean.setGender(memberBean.getGender());

		String birthday = "";
		SimpleDateFormat outputFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			birthday = outputFormat.format(new Date(Long.parseLong(memberBean
					.getDob())));
		} catch (Exception e) {
			e.printStackTrace();
		}

		beClubBean.setDob(birthday);
		return beClubBean;
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	public void checkFruitNinjaDialog() {
		if (!DataStore.getBoolean(this, "shown_fruitninja_dialog")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.register_fruit_ninja_msg)
					.setTitle(R.string.register_fruit_ninja_title)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// FIRE ZE MISSILES!
								}
							});
			// Create the AlertDialog object and return it
			builder.create().show();
			DataStore.saveBoolean(this, "shown_fruitninja_dialog", true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;// super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
