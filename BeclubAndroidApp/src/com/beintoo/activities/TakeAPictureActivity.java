package com.beintoo.activities;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.PictureItemFragment;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.CirclePageIndicator;
import com.beintoo.utils.ui.SmartViewPager;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.PictureAcceptMissionWrapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by Giulio Bider on 15/09/14. Copyright (c) 2014 Beintoo. All rights
 * reserved.
 */
public class TakeAPictureActivity extends BeNotificationActivity {

	private String brandID;
	private String brandName;

	private String mSelectedImage;
	private MissionWrapper mMission;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takeapicture_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("brand_id") != null
				&& extras.getString("brand_name") != null) {
			brandID = extras.getString("brand_id");
			brandName = extras.getString("brand_name");
			setTitle(extras.getString("brand_name"));
		}

		ArrayList<String> type = new ArrayList<String>();
		type.add(EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE
				.toString());

		ImageView ivMoreBedollars = (ImageView) findViewById(R.id.iv_more_bedollars);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_picture_bedollars_background);

		int x = getResources().getDisplayMetrics().widthPixels;
		int y = (x * bitmap.getHeight()) / bitmap.getWidth();

		Bitmap output = Bitmap.createScaledBitmap(bitmap, x, y, true);
		ivMoreBedollars.setImageBitmap(output);
		bitmap.recycle();

		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		final IMissionSponsorResource missionService = restAdapter
				.create(IMissionSponsorResource.class);

		missionService.getBrandMissions(MemberAuthStore.getAuth(this)
				.getToken(), LocationUtils.getLocationParams(this), null, null,
				type, brandID, null,
				new Callback<PaginatedList<MissionWrapper>>() {
					@Override
					public void success(PaginatedList<MissionWrapper> missions,
							Response response) {
						if (missions == null || missions.getList() == null) {
							finish();
							return;
						}
						final MissionWrapper mission = missions.getList()
								.get(0);
						mMission = mission;
						ImageView ivImageHeader = (ImageView) findViewById(R.id.iv_image_header);

						DisplayImageOptions options = new DisplayImageOptions.Builder()
								.showImageOnLoading(
										R.drawable.ic_image_placeholder_a)
								.cacheOnDisk(true)
								.displayer(new FadeInBitmapDisplayer(250))
								.preProcessor(new BitmapProcessor() {
									@Override
									public Bitmap process(Bitmap bitmap) {
										int widthPx = getResources()
												.getDisplayMetrics().widthPixels;
										int heightPx = (widthPx * bitmap
												.getHeight())
												/ bitmap.getWidth();

										return Bitmap
												.createScaledBitmap(bitmap,
														widthPx, heightPx, true);
									}
								}).build();

						ImageLoader.getInstance().displayImage(
								mission.getMission().getImagebig(),
								ivImageHeader, options);

						TextView tvPictureBrand = (TextView) findViewById(R.id.tv_picture_brand);
						tvPictureBrand
								.setText(getString(R.string.picture_mission_take_photo_with_brand));

						// Setup Reward Layout
						setupRewardLayout(mission);

						missionService.getMissionDetails(
								MemberAuthStore.getAuth(
										TakeAPictureActivity.this).getToken(),
								LocationUtils
										.getLocationParams(TakeAPictureActivity.this),
								mMission.getMission().getId(),
								new Callback<AssignedMissionWrapper>() {
									@Override
									public void success(
											AssignedMissionWrapper assignedMission,
											Response response) {
										if (assignedMission != null) {
											String closureDate = assignedMission
													.getMissionswrapper()
													.getMission()
													.getClosuredate();
											if (closureDate != null
													&& !closureDate.equals("")) {
												try {
													SimpleDateFormat format = new SimpleDateFormat(
															"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
													format.setTimeZone(TimeZone
															.getTimeZone("UTC"));

													Date date = format
															.parse(closureDate);

													Calendar cal = Calendar
															.getInstance(TimeZone
																	.getTimeZone("UTC"));
													long now = cal
															.getTimeInMillis();

													long difference = (date
															.getTime() - now) / 1000;
													setupTimeLayout(difference);

													// Looser Layout
													if (difference < 0
															&& assignedMission
																	.getWinner() != null
															&& !assignedMission
																	.getWinner()) {
														setupNonParticipatingLayout();
														setupLooserLayout(assignedMission);
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														findViewById(
																R.id.scrollView)
																.setVisibility(
																		View.VISIBLE);
														return;
													}

													// Winner Layout
													if (assignedMission
															.isAssigned()
															&& difference < 0
															&& assignedMission
																	.getWinner() != null
															&& assignedMission
																	.getWinner()) {
														setupNonParticipatingLayout();
														setupWinnerLayout(assignedMission);
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														findViewById(
																R.id.scrollView)
																.setVisibility(
																		View.VISIBLE);
														return;
													}

													// Non participating Layout
													if (!assignedMission
															.isAssigned()
															&& difference < 0) {
														setupNonParticipatingLayout();
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														findViewById(
																R.id.scrollView)
																.setVisibility(
																		View.VISIBLE);
														return;
													}

													// Participating Time is up
													// Layout
													if (assignedMission
															.isAssigned()
															&& difference < 0) {
														setupTimeIsUpLayout();
														setupPhotoPosted(assignedMission
																.getAssignedMissions()
																.getPictureurl());
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														findViewById(
																R.id.scrollView)
																.setVisibility(
																		View.VISIBLE);
														return;
													}

													// Participating Layout
													if (assignedMission
															.isAssigned()
															&& difference > 0) {
														setupPhotoPosted(assignedMission
																.getAssignedMissions()
																.getPictureurl());
														findViewById(
																R.id.progressBar)
																.setVisibility(
																		View.GONE);
														findViewById(
																R.id.scrollView)
																.setVisibility(
																		View.VISIBLE);
														return;
													}

													findViewById(
															R.id.progressBar)
															.setVisibility(
																	View.GONE);
													findViewById(
															R.id.scrollView)
															.setVisibility(
																	View.VISIBLE);
												} catch (ParseException e) {
													e.printStackTrace();
												}
											}
										}
									}

									@Override
									public void failure(RetrofitError error) {

									}
								});

						TextView tvAction = (TextView) findViewById(R.id.action_button_text);
						tvAction.setText("Let's get started!");

						findViewById(R.id.action_button_content)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												TakeAPictureActivity.this);
										final AlertDialog dialog = builder
												.create();
										View dialogView = View
												.inflate(
														TakeAPictureActivity.this,
														R.layout.take_picture_from_dialog,
														null);
										dialogView
												.findViewById(
														R.id.rl_picture_camera)
												.setOnClickListener(
														new View.OnClickListener() {
															@Override
															public void onClick(
																	View view) {
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
																				cameraIntent,
																				4445);
																	}
																} else {
																	Toast.makeText(
																			TakeAPictureActivity.this,
																			"No camera apps installed on this device.",
																			Toast.LENGTH_SHORT)
																			.show();
																}

																dialog.dismiss();
															}
														});

										dialogView
												.findViewById(
														R.id.rl_picture_galley)
												.setOnClickListener(
														new View.OnClickListener() {
															@Override
															public void onClick(
																	View view) {
																Intent galleryIntent = new Intent(
																		Intent.ACTION_GET_CONTENT,
																		null);
																galleryIntent
																		.setType("image/*");
																galleryIntent
																		.addCategory(Intent.CATEGORY_OPENABLE);
																if (galleryIntent
																		.resolveActivity(getPackageManager()) != null) {
																	startActivityForResult(
																			galleryIntent,
																			4444);
																} else {
																	Toast.makeText(
																			TakeAPictureActivity.this,
																			"No gallery apps installed on this device.",
																			Toast.LENGTH_SHORT)
																			.show();
																}
																dialog.dismiss();
															}
														});

										dialog.setView(dialogView);
										dialog.show();
									}
								});

						findViewById(R.id.rl_terms_conditions)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent intent = new Intent(
												TakeAPictureActivity.this,
												ExternalWebView.class);
										intent.putExtra("url",
												"http://beclub.com/en/tos?nav=mobile");
										startActivity(intent);
									}
								});
					}

					@Override
					public void failure(RetrofitError error) {
						if (error.isNetworkError()) {
							Toast.makeText(TakeAPictureActivity.this,
									getString(R.string.connection_error),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(TakeAPictureActivity.this,
									getString(R.string.something_wrong),
									Toast.LENGTH_LONG).show();
						}
						finish();
					}
				});
	}

	private void setupWinnerLayout(final AssignedMissionWrapper assignedMission) {
		findViewById(R.id.rl_photos_pager).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_steps).setVisibility(View.GONE);

		TextView tvHeaderMessage = (TextView) findViewById(R.id.tv_header_message);
		tvHeaderMessage.setText(getString(R.string.picture_mission_winner));

		findViewById(R.id.rl_time_is_up).setVisibility(View.GONE);
		findViewById(R.id.rl_time).setVisibility(View.GONE);
		findViewById(R.id.action_button_content).setVisibility(View.GONE);
		findViewById(R.id.ll_mission_finished).setVisibility(View.GONE);
		findViewById(R.id.ll_divider).setVisibility(View.GONE);
		findViewById(R.id.rl_winner).setVisibility(View.VISIBLE);

		final SmartViewPager pager = (SmartViewPager) findViewById(R.id.vp_photos);
		final CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.vp_photos_indicator);

		pager.setAdapter(new PhotoPager(getSupportFragmentManager(),
				assignedMission));

		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				PictureItemFragment fragment = (PictureItemFragment) ((PhotoPager) pager
						.getAdapter()).getItem(position);
				if (fragment.getHeight() != 0) {
					pager.setLayoutParams(new RelativeLayout.LayoutParams(
							fragment.getWidth(), fragment.getHeight()));
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	private void setupLooserLayout(AssignedMissionWrapper assignedMission) {
		findViewById(R.id.rl_photos_pager).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_steps).setVisibility(View.GONE);

		TextView tvHeaderMessage = (TextView) findViewById(R.id.tv_header_message);
		tvHeaderMessage
				.setText(getString(R.string.picture_mission_and_winner_is));

		findViewById(R.id.rl_time_is_up).setVisibility(View.GONE);
		findViewById(R.id.rl_time).setVisibility(View.GONE);
		findViewById(R.id.action_button_content).setVisibility(View.GONE);
		findViewById(R.id.ll_mission_finished).setVisibility(View.GONE);
		findViewById(R.id.ll_divider).setVisibility(View.GONE);
		findViewById(R.id.rl_looser).setVisibility(View.VISIBLE);

		final SmartViewPager pager = (SmartViewPager) findViewById(R.id.vp_photos);
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.vp_photos_indicator);

		pager.setAdapter(new PhotoPager(getSupportFragmentManager(),
				assignedMission));
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				PictureItemFragment fragment = (PictureItemFragment) ((PhotoPager) pager
						.getAdapter()).getItem(position);
				if (fragment.getHeight() != 0) {
					pager.setLayoutParams(new RelativeLayout.LayoutParams(
							fragment.getWidth(), fragment.getHeight()));
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void setupNonParticipatingLayout() {
		setupTimeIsUpLayout();
		findViewById(R.id.ll_mission_finished).setVisibility(View.VISIBLE);

		RelativeLayout rlActionButton = (RelativeLayout) findViewById(R.id.action_button_content);
		rlActionButton.setBackgroundColor(Color.parseColor("#CCCCCC"));
		rlActionButton.setOnClickListener(null);
	}

	private void setupTimeIsUpLayout() {
		findViewById(R.id.rl_time).setVisibility(View.GONE);
		findViewById(R.id.rl_time_is_up).setVisibility(View.VISIBLE);
	}

	private void setupTimeLayout(long difference) {

		int days = (int) (difference / (3600 * 24));
		int hours = (int) ((difference - (3600 * 24 * days)) / 3600);
		int mins = (int) (((difference - (3600 * 24 * days) - (3600 * hours)) / 60) % 60);

		DebugUtility.showLog("####: D: " + days + " H: " + hours + " M: "
				+ mins);

		TextView tvDays1 = (TextView) findViewById(R.id.tvD1);
		TextView tvDays2 = (TextView) findViewById(R.id.tvD2);
		if (days > 9) {
			String dayS = String.valueOf(days);
			tvDays1.setText(dayS.substring(0, 1));
			tvDays2.setText(dayS.substring(1, 2));
		} else {
			tvDays1.setText("0");
			tvDays2.setText("" + days);
		}

		TextView tvHour1 = (TextView) findViewById(R.id.tvH1);
		TextView tvHour2 = (TextView) findViewById(R.id.tvH2);
		if (hours > 9) {
			String hourS = String.valueOf(hours);
			tvHour1.setText(hourS.substring(0, 1));
			tvHour2.setText(hourS.substring(1, 2));
		} else {
			tvHour1.setText("0");
			tvHour2.setText("" + hours);
		}

		TextView tvMin1 = (TextView) findViewById(R.id.tvM1);
		TextView tvMin2 = (TextView) findViewById(R.id.tvM2);
		if (mins > 9) {
			String minS = String.valueOf(mins);
			tvMin1.setText(minS.substring(0, 1));
			tvMin2.setText(minS.substring(1, 2));
		} else {
			tvMin1.setText("0");
			tvMin2.setText("" + mins);
		}
	}

	private void setupRewardLayout(MissionWrapper mission) {
		if (mission.getReward() != null) {
			ImageView ivReward = (ImageView) findViewById(R.id.itemImage);

			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_image_placeholder_a)
					.cacheOnDisk(true)
					.displayer(new FadeInBitmapDisplayer(250))
					.preProcessor(new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							int widthPx = getResources().getDisplayMetrics().widthPixels;
							int heightPx = (widthPx * bitmap.getHeight())
									/ bitmap.getWidth();

							return Bitmap.createScaledBitmap(bitmap, widthPx,
									heightPx, true);
						}
					}).build();

			ImageLoader.getInstance().displayImage(
					mission.getReward().getImagebig(), ivReward, options);

			findViewById(R.id.tv_qnt_bedollars).setVisibility(View.GONE);

			if (mission.getMission().getBedollars() != null
					&& mission.getMission().getBedollars() != 0) {
				TextView tvBeDollarsBottom = (TextView) findViewById(R.id.tv_headline3);
				tvBeDollarsBottom.setText(""
						+ mission.getMission().getBedollars().intValue()
						+ " BeDollars");
			} else {
				findViewById(R.id.rl_warning_partner).setVisibility(View.GONE);
			}

		} else {
			ImageView ivReward = (ImageView) findViewById(R.id.itemImage);

			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.popup_img2);
			int x = getResources().getDisplayMetrics().widthPixels;
			int h = (x * bitmap.getHeight()) / bitmap.getWidth();
			Bitmap output = Bitmap.createScaledBitmap(bitmap, x, h, true);

			bitmap.recycle();

			ivReward.setImageBitmap(output);

			TextView tvQntBedollars = (TextView) findViewById(R.id.tv_qnt_bedollars);
			tvQntBedollars.setText("+"
					+ mission.getMission().getBedollars().intValue()
					+ " BeDollars");

			findViewById(R.id.rl_warning_partner).setVisibility(View.GONE);
		}
	}

	private void setupPhotoPosted(String pictureUrl) {
		findViewById(R.id.action_button_content).setVisibility(View.GONE);
		findViewById(R.id.ll_photo_posted).setVisibility(View.VISIBLE);

		ImageView ivPhoto = (ImageView) findViewById(R.id.iv_personal_photo);

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_placeholder_a)
				.cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(250))
				.preProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bitmap) {
						int widthPx = getResources().getDisplayMetrics().widthPixels;
						int heightPx = (widthPx * bitmap.getHeight())
								/ bitmap.getWidth();

						return Bitmap.createScaledBitmap(bitmap, widthPx,
								heightPx, true);
					}
				}).build();

		ImageLoader.getInstance().displayImage(pictureUrl, ivPhoto, options);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Intent intent;
			switch (requestCode) {
			case 4445:
				intent = new Intent(this, TakeAPictureRewiewActivity.class);
				intent.putExtras(getIntent().getExtras());
				intent.putExtra("photo", mSelectedImage);
				startActivityForResult(intent, 4447);
				break;
			case 4444:
				intent = new Intent(this, TakeAPictureRewiewActivity.class);
				intent.putExtras(getIntent().getExtras());

				Uri selectedImageUri = data.getData();
				if (Build.VERSION.SDK_INT < 19) {
					mSelectedImage = BeUtils.getPicturePathFromURI(this,
							selectedImageUri);
					intent.putExtra("photo", mSelectedImage);
				} else {
					intent.putExtra("photo", selectedImageUri);
				}
				startActivityForResult(intent, 4447);
				break;
			case 4447:
				String token = data.getStringExtra("token");
				String imageID = data.getStringExtra("image_id");

				final String pictureUrl = "https://graph.facebook.com/"
						+ imageID + "/picture?size=big&access_token=" + token;

				RestAdapter restAdapter = BeRestAdapter
						.getMemberTokenRestAdapter();
				IMissionSponsorResource missionService = restAdapter
						.create(IMissionSponsorResource.class);

				PictureAcceptMissionWrapper pamw = new PictureAcceptMissionWrapper();
				pamw.setPostid(imageID);
				pamw.setFbaccesstoken(token);
				pamw.setPictureurl(pictureUrl);

				missionService.acceptMission(MemberAuthStore.getAuth(this)
						.getToken(), mMission.getMission().getId(), pamw,
						new Callback<Response>() {
							@Override
							public void success(Response response,
									Response response2) {
								setupPhotoPosted(pictureUrl);
							}

							@Override
							public void failure(RetrofitError error) {
								if (error.isNetworkError()) {
									Toast.makeText(
											TakeAPictureActivity.this,
											getString(R.string.connection_error),
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(
											TakeAPictureActivity.this,
											getString(R.string.something_wrong),
											Toast.LENGTH_LONG).show();
								}
							}
						});
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

	public static Intent getTakeAPictureActivityIntent(Context context,
			String brandID) {
		Intent intent = new Intent(context, TakeAPictureActivity.class);
		intent.putExtra("brand_id", brandID);
		intent.putExtra("brand_name", "BeClub");
		return intent;
	}

	public class PhotoPager extends FragmentPagerAdapter {

		private ArrayList<PictureItemFragment> mFragments;

		private AssignedMissionWrapper mMission;

		public PhotoPager(FragmentManager fm,
				AssignedMissionWrapper pictureMission) {
			super(fm);
			this.mMission = pictureMission;
			mFragments = new ArrayList<PictureItemFragment>();
			for (int i = 0; i < mMission.getWinnersPictures().size(); i++) {
				mFragments.add(i, new PictureItemFragment(mMission
						.getWinnersPictures().get(i), i));
			}
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mMission.getWinnersPictures().size();
		}
	}
}
