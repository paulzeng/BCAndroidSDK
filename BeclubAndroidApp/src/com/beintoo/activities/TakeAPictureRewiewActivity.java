package com.beintoo.activities;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;

/**
 * Created by Giulio Bider on 17/09/14. Copyright (c) 2014 Beintoo. All rights
 * reserved.
 */
public class TakeAPictureRewiewActivity extends FragmentActivity {

	private String brandID;
	private String brandName;

	private Bitmap mPhoto;

	private String mSelectedImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takeapicture_review);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		Bundle extra = getIntent().getExtras();

		if (extra != null && extra.getString("brand_id") != null
				&& extra.getString("brand_name") != null) {
			brandID = extra.getString("brand_id");
			brandName = extra.getString("brand_name");
			setTitle(extra.getString("brand_name"));
		}

		final EditText etMessage = (EditText) findViewById(R.id.et_message);

		setupLayout(extra);

		findViewById(R.id.rl_share_on_facebook).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// if(Session.getActiveSession() == null) {
						// FacebookManager.facebookLoginWithoutBeClubRegistration(TakeAPictureRewiewActivity.this);
						// } else {
						// final ProgressDialog dialog =
						// ProgressDialog.show(TakeAPictureRewiewActivity.this,
						// "Posting...", "...your picture on Facebook.", true,
						// false);
						// FacebookManager.publishPhoto(TakeAPictureRewiewActivity.this,
						// etMessage.getText().toString(), mPhoto, new
						// FacebookManager.OnFacebookListener() {
						// @Override
						// public void onPhotoPosted(String postID, String
						// imageID) {
						// Analytics.trackEvent(TakeAPictureRewiewActivity.this,
						// "PICTURECONTEST", "Click",
						// "PICTURE-CONTEST-TAKE-A-PHOTO-SHARING", 1L,
						// brandName, brandID);
						// String token =
						// Session.getActiveSession().getAccessToken();
						//
						// Intent data = new Intent();
						// data.putExtra("token", token);
						// data.putExtra("post_id", postID);
						// data.putExtra("image_id", imageID);
						//
						// dialog.dismiss();
						// setResult(RESULT_OK, data);
						// finish();
						// }
						// });
						// }
					}
				});

		findViewById(R.id.rl_take_another).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								TakeAPictureRewiewActivity.this);
						final AlertDialog dialog = builder.create();
						View dialogView = View.inflate(
								TakeAPictureRewiewActivity.this,
								R.layout.take_picture_from_dialog, null);
						dialogView.findViewById(R.id.rl_picture_camera)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent cameraIntent = new Intent(
												android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
										if (cameraIntent
												.resolveActivity(getPackageManager()) != null) {
											File photoFile = null;
											try {
												photoFile = BeUtils
														.createImageFile();
												mSelectedImage = photoFile
														.getAbsolutePath();
											} catch (IOException ex) {
												ex.printStackTrace();
											}

											if (photoFile != null) {
												cameraIntent
														.putExtra(
																MediaStore.EXTRA_OUTPUT,
																Uri.fromFile(photoFile));
												startActivityForResult(
														cameraIntent, 4445);
											}
										} else {
											Toast.makeText(
													TakeAPictureRewiewActivity.this,
													"No camera apps installed on this device.",
													Toast.LENGTH_SHORT).show();
										}

										dialog.dismiss();
									}
								});

						dialogView.findViewById(R.id.rl_picture_galley)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent galleryIntent = new Intent(
												Intent.ACTION_GET_CONTENT, null);
										galleryIntent.setType("image/*");
										galleryIntent
												.addCategory(Intent.CATEGORY_OPENABLE);
										if (galleryIntent
												.resolveActivity(getPackageManager()) != null) {
											startActivityForResult(
													galleryIntent, 4444);
										} else {
											Toast.makeText(
													TakeAPictureRewiewActivity.this,
													"No gallery apps installed on this device.",
													Toast.LENGTH_SHORT).show();
										}
										dialog.dismiss();
									}
								});

						dialog.setView(dialogView);
						dialog.show();
					}
				});
	}

	private void setupLayout(Bundle extra) {
		final ImageView mImageView = (ImageView) findViewById(R.id.iv_image);

		if (extra != null) {
			if (extra.get("photo") != null
					&& extra.get("photo") instanceof String) {
				String mSelectedImage = extra.getString("photo");

				// Get the dimensions of the View
				int targetW = getResources().getDisplayMetrics().widthPixels;
				int targetH = 300;

				// Get the dimensions of the bitmap
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(mSelectedImage, bmOptions);
				int photoW = bmOptions.outWidth;
				int photoH = bmOptions.outHeight;

				// Determine how much to scale down the image
				int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

				// Decode the image file into a Bitmap sized to fill the View
				bmOptions.inJustDecodeBounds = false;
				bmOptions.inSampleSize = scaleFactor;
				bmOptions.inPurgeable = true;

				mPhoto = BitmapFactory.decodeFile(mSelectedImage, bmOptions);

				try {

					ExifInterface exif = new ExifInterface(mSelectedImage);
					int exifOrientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					int rotate = 0;

					switch (exifOrientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;

					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;

					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					}

					if (rotate != 0) {
						int w = mPhoto.getWidth();
						int h = mPhoto.getHeight();

						// Setting pre rotate
						Matrix mtx = new Matrix();
						mtx.preRotate(rotate);

						// Rotating Bitmap & convert to ARGB_8888, required by
						// tess
						mPhoto = Bitmap.createBitmap(mPhoto, 0, 0, w, h, mtx,
								false);
						mPhoto = mPhoto.copy(Bitmap.Config.ARGB_8888, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				mImageView.setImageBitmap(mPhoto);
			} else {
				ParcelFileDescriptor parcelFileDescriptor;
				try {
					Uri selectedImageUri = extra.getParcelable("photo");
					parcelFileDescriptor = getContentResolver()
							.openFileDescriptor(selectedImageUri, "r");
					FileDescriptor fileDescriptor = parcelFileDescriptor
							.getFileDescriptor();

					mPhoto = BitmapFactory.decodeFileDescriptor(fileDescriptor);

					parcelFileDescriptor.close();
					mImageView.setImageBitmap(mPhoto);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if(Session.getActiveSession() != null) {
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
		// }

		if (resultCode == RESULT_OK) {
			Bundle bundle;
			switch (requestCode) {
			case 4445:
				bundle = new Bundle();
				bundle.putString("photo", mSelectedImage);
				setupLayout(bundle);
				break;
			case 4444:
				bundle = new Bundle();
				Uri selectedImageUri = data.getData();
				if (Build.VERSION.SDK_INT < 19) {
					mSelectedImage = BeUtils.getPicturePathFromURI(this,
							selectedImageUri);
					bundle.putString("photo", mSelectedImage);
				} else {
					bundle.putParcelable("photo", selectedImageUri);
				}
				setupLayout(bundle);
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
