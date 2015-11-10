package com.beintoo.activities;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beintoo.asynctasks.SettingsProfilePictureUploadTask;
import com.beintoo.asynctasks.SettingsProfileUpdateTask;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.DatePickerFragment;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.CircleImageView;
import com.beintoo.wrappers.MemberBean;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingsEditProfileActivity extends BeNotificationActivity {

	private boolean isValid = true;
	private boolean isFormEnabled = false;
	private CircleImageView mMemberImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_edit_profile_activity);

		MemberBean me = MemberAuthStore.getMember(this);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		mMemberImage = (CircleImageView) findViewById(R.id.user_image_profile);
		final EditText firstname = (EditText) findViewById(R.id.firstname);
		final EditText lastname = (EditText) findViewById(R.id.lastname);
		final EditText birthday = (EditText) findViewById(R.id.birthday);
		final RadioButton male = (RadioButton) findViewById(R.id.male);
		final RadioButton female = (RadioButton) findViewById(R.id.female);

		setDisabled();

		final Map<String, String> params = new HashMap<String, String>();
		final MemberBean newMemberData = new MemberBean();

		if (me.getGender() != null && me.getGender().equals("M")) {
			male.setChecked(true);
		} else if (me.getGender() != null && me.getGender().equals("F")) {
			female.setChecked(true);
		}

		if (me.getName() != null) {
			firstname.setText(me.getName());
		}

		if (me.getSurname() != null) {
			lastname.setText(me.getSurname());
		}

		if (me.getImagesmall() != null && me.getImagebig() != null) {
			ImageLoader.getInstance().displayImage(me.getImagesmall(),
					mMemberImage);
		}

		Date userBirthday = null;
		if (me.getDob() != null) {
			try {
				SimpleDateFormat dateFormatter = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
				dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
				userBirthday = dateFormatter.parse(me.getDob());
				dateFormatter.setTimeZone(TimeZone.getDefault());
				birthday.setText(DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.getDefault()).format(userBirthday));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		male.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isFormEnabled)
					return;

				if (female.isChecked()) {
					female.setChecked(false);
				}

				findViewById(R.id.error_gender).setVisibility(View.GONE);

				params.put("gender", "M");
				newMemberData.setGender("M");
			}
		});

		female.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isFormEnabled)
					return;

				if (male.isChecked()) {
					male.setChecked(false);
				}

				findViewById(R.id.error_gender).setVisibility(View.GONE);

				params.put("gender", "F");

				newMemberData.setGender("F");
			}
		});

		final Date defaultBirthday = userBirthday;
		birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				final DateFormat df = DateFormat.getDateInstance(
						DateFormat.SHORT, Locale.getDefault());
				DatePickerFragment datePicker = new DatePickerFragment(
						new DatePickerFragment.DateListener() {
							@Override
							public void onDatePicked(Date date) {
								birthday.setText(df.format(date));
								params.put("birthday", date.toString());
								newMemberData.setDobUnix(date.getTime());
							}
						}, defaultBirthday, true);

				datePicker.show(getSupportFragmentManager(), "datePicker");
			}
		});

		findViewById(R.id.update_profile).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						resetErrors();

						if (firstname.length() < 2) {
							isValid = false;
							findViewById(R.id.error_firstname).setVisibility(
									View.VISIBLE);
							newMemberData.setName(null);
						} else {
							params.put("firstname", firstname.getText()
									.toString());
							newMemberData.setName(firstname.getText()
									.toString());
						}

						if (lastname.length() < 3) {
							isValid = false;
							findViewById(R.id.error_lastname).setVisibility(
									View.VISIBLE);
							newMemberData.setSurname(null);
						} else {
							params.put("lastname", lastname.getText()
									.toString());
							newMemberData.setSurname(lastname.getText()
									.toString());
						}

						if (birthday.length() == 0) {
							isValid = false;
							findViewById(R.id.error_birthday).setVisibility(
									View.VISIBLE);
							newMemberData.setDob(null);
						}

						if (!male.isChecked() && !female.isChecked()) {
							isValid = false;
							findViewById(R.id.error_gender).setVisibility(
									View.VISIBLE);
							newMemberData.setGender(null);
						}

						if (isValid) {
							setDisabled();
							new SettingsProfileUpdateTask(
									SettingsEditProfileActivity.this,
									new SettingsProfileUpdateTask.OnProfileUpdateListener() {
										@Override
										public void onProfileUpdate() {
											Toast.makeText(
													SettingsEditProfileActivity.this,
													getString(R.string.settings_profile_updated),
													Toast.LENGTH_LONG).show();
										}

										@Override
										public void onProfileUpdateFailed() {
											Toast.makeText(
													SettingsEditProfileActivity.this,
													getString(R.string.something_wrong),
													Toast.LENGTH_LONG).show();
										}
									}).execute(newMemberData);
						}
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

		findViewById(R.id.change_profile_pict).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getImageFromGallery();
					}
				});
		mMemberImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getImageFromGallery();
			}
		});
	}

	private void setEditable() {
		isFormEnabled = true;

		final EditText firstname = (EditText) findViewById(R.id.firstname);
		final EditText lastname = (EditText) findViewById(R.id.lastname);
		final EditText birthday = (EditText) findViewById(R.id.birthday);
		final RadioButton male = (RadioButton) findViewById(R.id.male);
		final RadioButton female = (RadioButton) findViewById(R.id.female);

		firstname.setEnabled(true);
		lastname.setEnabled(true);
		birthday.setEnabled(true);
		firstname.setClickable(true);
		lastname.setClickable(true);
		birthday.setClickable(true);

		male.setClickable(true);
		male.setEnabled(true);
		female.setClickable(true);
		female.setEnabled(true);

		findViewById(R.id.update_profile).setVisibility(View.VISIBLE);
	}

	private void setDisabled() {
		isFormEnabled = false;

		final EditText firstname = (EditText) findViewById(R.id.firstname);
		final EditText lastname = (EditText) findViewById(R.id.lastname);
		final EditText birthday = (EditText) findViewById(R.id.birthday);
		final RadioButton male = (RadioButton) findViewById(R.id.male);
		final RadioButton female = (RadioButton) findViewById(R.id.female);

		firstname.setEnabled(false);
		lastname.setEnabled(false);
		birthday.setEnabled(false);
		firstname.setClickable(false);
		lastname.setClickable(false);
		birthday.setClickable(false);
		male.setClickable(false);
		male.setEnabled(false);
		female.setClickable(false);
		female.setEnabled(false);

		findViewById(R.id.update_profile).setVisibility(View.GONE);
	}

	private void resetErrors() {
		isValid = true;
		findViewById(R.id.error_firstname).setVisibility(View.GONE);
		findViewById(R.id.error_lastname).setVisibility(View.GONE);
		findViewById(R.id.error_birthday).setVisibility(View.GONE);
		findViewById(R.id.error_gender).setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.edit_profile:
			setEditable();

			break;
		}
		return true;
	}

	private void getImageFromGallery() {
		Intent in = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(in, 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK && null != data) {

			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				if (picturePath == null) {
					return;
				}

				File image = new File(picturePath);

				// Bitmap resized = ResizeImage.decodeFile(image, 512, 512,
				// false);//.getResizedBitmap(BitmapFactory.decodeFile(image.getAbsolutePath()),
				// 512, 512);

				new SettingsProfilePictureUploadTask(
						this,
						new SettingsProfilePictureUploadTask.OnProfileUpdateListener() {
							@Override
							public void onProfileUpdate(String image) {
								ImageLoader.getInstance().displayImage(image,
										mMemberImage);
								Toast.makeText(
										SettingsEditProfileActivity.this,
										getString(R.string.settings_profile_picture_updated),
										Toast.LENGTH_LONG).show();

							}

							@Override
							public void onProfileUpdateFailed() {
								Toast.makeText(
										SettingsEditProfileActivity.this,
										getString(R.string.something_wrong),
										Toast.LENGTH_LONG).show();

							}
						}).execute(image);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}
}
