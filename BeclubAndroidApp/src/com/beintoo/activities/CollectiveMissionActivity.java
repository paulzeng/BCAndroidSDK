package com.beintoo.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.asynctasks.CollectiveMissionDetailsTask;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.CollectiveMissionChooserDialog;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.beintoo.wrappers.PictureAcceptMissionWrapper;
import com.beintoo.wrappers.RewardWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class CollectiveMissionActivity extends BeNotificationActivity {

	private final static String mScreenName = "EARN-BEDOLLARS-COLLECTIVE";

	private String mBrandId;
	private String mBrandName;
	private String mMissionId;
	private boolean mIsAssigned;
	private int mMinFriends = 0;
	private String mMatURL;
	private String shareMessage;
	private RewardWrapper mReward;

	private View.OnClickListener mClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collective_mission_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getString("brand_id") != null) {
			mBrandId = bundle.getString("brand_id");

			if (bundle.getString("brand_name") != null) {
				mBrandName = bundle.getString("brand_name");
				setTitle(bundle.getString("brand_name"));
			}
		}

		loadData(false);

		mClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mIsAssigned) {
					findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
					findViewById(R.id.container).setVisibility(View.GONE);
					changeActionButtonState(false);

					RestAdapter restAdapter = BeRestAdapter
							.getMemberTokenRestAdapter();
					IMissionSponsorResource missionService = restAdapter
							.create(IMissionSponsorResource.class);

					missionService.acceptMission(
							MemberAuthStore.getAuth(
									CollectiveMissionActivity.this).getToken(),
							mMissionId, new PictureAcceptMissionWrapper(),
							new Callback<Response>() {
								@Override
								public void success(Response response,
										Response response2) {
									changeActionButtonState(true);
									loadData(true);
								}

								@Override
								public void failure(RetrofitError error) {
									if (error.isNetworkError()) {
										Toast.makeText(
												CollectiveMissionActivity.this,
												getString(R.string.connection_error),
												Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(
												CollectiveMissionActivity.this,
												getString(R.string.something_wrong),
												Toast.LENGTH_LONG).show();
									}
									changeActionButtonState(true);
								}
							});
				} else {
					CollectiveMissionChooserDialog
							.newInstance(mMinFriends, mMatURL, shareMessage,
									mMissionId)
							.show((CollectiveMissionActivity.this)
									.getSupportFragmentManager(),
									"collective_mission_dialog");
				}

			}
		};

		/**
		 * if the mission is not accepted we have to accept it first
		 */
		findViewById(R.id.actionButton).setOnClickListener(mClickListener);
	}

	private void changeActionButtonState(boolean enabled) {
		try {
			if (!enabled) {
				((RelativeLayout) findViewById(R.id.actionButton))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.actionButton)),
								this.getResources().getDrawable(
										R.drawable.disabled_button_selector));
				((TextView) findViewById(R.id.actionButtonText))
						.setTextColor(Color.WHITE);
				((TextView) findViewById(R.id.actionButtonText))
						.setText(getString(R.string.collective_mission_activity_invite));
				findViewById(R.id.actionButton).setOnClickListener(null);
			} else {
				((RelativeLayout) findViewById(R.id.actionButton))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.actionButton)),
								this.getResources().getDrawable(
										R.drawable.green_button));
				((TextView) findViewById(R.id.actionButtonText))
						.setTextColor(Color.WHITE);
				((TextView) findViewById(R.id.actionButtonText))
						.setText(getString(R.string.collective_mission_activity_invite));
				findViewById(R.id.actionButton).setOnClickListener(
						mClickListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadData(final boolean showDialogAfterLoading) {
		new CollectiveMissionDetailsTask(
				this,
				mBrandId,
				new CollectiveMissionDetailsTask.CollectiveMissionDetailCallback() {
					@Override
					public void onComplete(
							final AssignedMissionWrapper assignedMission) {
						try {
							mMissionId = assignedMission.getMissionswrapper()
									.getMission().getId();
							mMinFriends = assignedMission.getPartecipants();
							mIsAssigned = assignedMission.isAssigned();
							mMatURL = assignedMission.getMaturl();
							mReward = assignedMission.getReward();
							shareMessage = assignedMission.getSharedsentence();

							/**
							 * if the missions is completed we show the
							 * completed mission activity
							 */
							if (assignedMission.getAlreadyregistered() >= assignedMission
									.getPartecipants()) {
								Intent intent = new Intent(
										CollectiveMissionActivity.this,
										CollectiveMissionCompleteActivity.class);
								intent.putExtra("assigned_mission",
										assignedMission.toString());
								startActivity(intent);
								finish();
							}

							setupView(assignedMission);
						} catch (Exception e) {
							e.printStackTrace();
						}
						findViewById(R.id.progressBar).setVisibility(View.GONE);
						findViewById(R.id.container)
								.setVisibility(View.VISIBLE);

						findViewById(R.id.tv_view_reward_details)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent intent = new Intent(
												CollectiveMissionActivity.this,
												RewardActivity.class);
										intent.putExtra("reward",
												new Gson().toJson(mReward));
										intent.putExtra(
												"is_detail_from_mission", true);
										startActivity(intent);
									}
								});

						if (!mIsAssigned) {
							findViewById(R.id.collective_time_friends_count)
									.setVisibility(View.GONE);
							findViewById(R.id.collective_progress_bar)
									.setVisibility(View.GONE);
						} else {
							findViewById(R.id.collective_time_friends_count)
									.setVisibility(View.VISIBLE);
							findViewById(R.id.collective_progress_bar)
									.setVisibility(View.VISIBLE);
						}

						updateInvitedUsers();

						((TextView) findViewById(R.id.registered_friends_count))
								.setText(getString(
										R.string.collective_mission_registered_friends,
										assignedMission.getAlreadyregistered()));

						if (assignedMission.getAlreadyregistered() > 0) {
							((TextView) findViewById(R.id.collective_friends_progress_title))
									.setText(getString(R.string.collective_mission_progress_title_more));
							((ImageView) findViewById(R.id.friends_registered_icon))
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.friendreggreen));
						} else {
							((TextView) findViewById(R.id.collective_friends_progress_title))
									.setText(getString(R.string.collective_mission_progress_title_zero));
							((ImageView) findViewById(R.id.friends_registered_icon))
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.friendreggrey));
						}

						findViewById(R.id.reward_info).setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Intent intent = new Intent(
												CollectiveMissionActivity.this,
												RewardActivity.class);
										intent.putExtra("reward",
												assignedMission.getReward()
														.toString());
										intent.putExtra(
												"is_detail_from_mission", true);
										startActivity(intent);
									}
								});

						if (showDialogAfterLoading) {
							try {
								CollectiveMissionChooserDialog.newInstance(
										mMinFriends, mMatURL, shareMessage,
										mMissionId).show(
										CollectiveMissionActivity.this
												.getSupportFragmentManager(),
										"collective_mission_dialog");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onError() {

					}
				}).execute();
	}

	private void setupView(AssignedMissionWrapper assignedMissionWrapper)
			throws Exception {
		if (assignedMissionWrapper.getReward() != null) {

			ImageView ivBrand = (ImageView) findViewById(R.id.collective_reward_image);
			final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
			final int heightPx = (widthPx * 320) / 730;
			ivBrand.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
					heightPx));

			if (assignedMissionWrapper.getReward().getImagebig() != null) {
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.ic_image_placeholder_a)
						.cacheOnDisk(true)
						.displayer(new FadeInBitmapDisplayer(250))
						.preProcessor(new BitmapProcessor() {
							@Override
							public Bitmap process(Bitmap bitmap) {
								Bitmap temp = BeUtils.getRoundedCornerBitmap(
										CollectiveMissionActivity.this, bitmap,
										3, bitmap.getWidth(),
										bitmap.getHeight(), false, false, true,
										true);
								return Bitmap.createScaledBitmap(temp, widthPx,
										heightPx, true);
							}
						}).build();

				ImageLoader.getInstance().displayImage(
						assignedMissionWrapper.getReward().getImagebig(),
						ivBrand, options);
			}
		} else {
			findViewById(R.id.rl_only_bedollars_container).setVisibility(
					View.VISIBLE);
			findViewById(R.id.collective_reward_image).setVisibility(View.GONE);
			findViewById(R.id.linearLayout).setVisibility(View.GONE);
			TextView tvBeDollars = (TextView) findViewById(R.id.tv_qnt_bedollars);
			tvBeDollars.setText("+"
					+ assignedMissionWrapper.getMissionswrapper().getMission()
							.getBedollars().intValue());
		}

		// ((TextView)findViewById(R.id.collective_title)).setText(getString(R.string.collective_mission_activity_title,
		// mMinFriends, assignedMissionWrapper.getBrand().getName(),
		// assignedMissionWrapper.getReward().getName()));
		((TextView) findViewById(R.id.collective_title))
				.setText(assignedMissionWrapper.getMissionswrapper()
						.getMission().getDescription());

		/**
		 * Calculate the remaining time
		 */
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
			Date expireDate = dateFormatter.parse(assignedMissionWrapper
					.getMissionswrapper().getMission().getActioncheck()
					.getTiming().getDatetimingto());
			Date now = new Date();
			Calendar expire = Calendar.getInstance();
			expire.setTime(expireDate);
			long difference = expire.getTimeInMillis()
					- System.currentTimeMillis();
			long x = difference / 1000;
			Long seconds = x % 60;
			x /= 60;
			Long minutes = (x % 60);
			x /= 60;
			Long hours = (x % 24);
			x /= 24;
			Long days = x;

			String expireString = "";
			if (days > 0) {
				expireString = getString(R.string.collective_mission_activity_time_remaining_days);
				expireString = expireString.replace("{days}", days.toString())
						.replace("{hours}", hours.toString());
			} else {
				expireString = getString(R.string.collective_mission_activity_time_remaining_hours);
				expireString = expireString
						.replace("{hours}", hours.toString()).replace(
								"{minutes}", minutes.toString());
			}

			((TextView) findViewById(R.id.collective_time_remaining))
					.setText(expireString);

		} catch (Exception e) {
			e.printStackTrace();
		}

		String friendsCount = getString(
				R.string.collective_mission_activity_friend_total,
				(assignedMissionWrapper.getPartecipants() - assignedMissionWrapper
						.getAlreadyregistered()));
		((TextView) findViewById(R.id.collective_time_friends_count))
				.setText(friendsCount);

		if (assignedMissionWrapper.getAlreadyregistered() > 0) {
			Float progress = ((float) assignedMissionWrapper
					.getAlreadyregistered() / (float) assignedMissionWrapper
					.getPartecipants()) * 100.0f;
			((ProgressBar) findViewById(R.id.collective_progress_bar))
					.setProgress(progress.intValue());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void updateInvitedUsers() {
		List<String> invitedFriends = DataStore.getStringList(
				CollectiveMissionActivity.this, "collective_invited_friends_"
						+ mMissionId);
		((TextView) findViewById(R.id.invited_friends_count))
				.setText(getString(R.string.collective_mission_invited_friends,
						invitedFriends.size()));
		if (invitedFriends.size() < mMinFriends) {
			((TextView) findViewById(R.id.collective_spread))
					.setText(getString(
							R.string.collective_mission_activity_invite_title_zero,
							mMinFriends));
		} else {
			((TextView) findViewById(R.id.collective_spread))
					.setText(getString(R.string.collective_mission_activity_invite_title_more_than_zero));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateInvitedUsers();
	}

	public static Intent getCollectiveMissionActivityIntent(Context context,
			String brandID) {
		Intent intent = new Intent(context, CollectiveMissionActivity.class);
		intent.putExtra("brand_id", brandID);
		intent.putExtra("brand_name", "BeClub Collective");
		return intent;
	}
}