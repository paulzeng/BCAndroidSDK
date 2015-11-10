package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MeTask;
import com.beintoo.beclubapp.R;
import com.beintoo.broadcasters.WidgetBedollars;
import com.beintoo.dialogs.RewardBuyDialog;
import com.beintoo.social.GPlusManager;
import com.beintoo.social.Shares;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.MeAndAuthWrapper;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.RewardWrapper;
import com.google.gson.Gson;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

@SuppressLint("ResourceAsColor")
public class RewardActivity extends BeNotificationActivity {

	enum ACTION_BUTTON_BEHAVIOR {
		BUY, DISCOVER, ARCHIVE, ARCHIVE_NO_TASK, VERIFY_EMAIL
	}

	private ACTION_BUTTON_BEHAVIOR mActionButtonBehavior;

	public final static int RESULT_DISCOVER_HOW = 1000;

	private BuyWrapper mBoughtReward;
	private RewardWrapper mBestoreReward;
	private Integer mWalletPosition;

	private boolean mIsConverted = false;

	private final String mScreenName = "REWARD-DETAIL-IN-BESTORE";

	private boolean mIsDetailFromMission = false;
	private boolean mIsConvertedFromMission = false;

	private Bundle mSavedInstanceState;

	private MemberBean mMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSavedInstanceState = savedInstanceState; // needed from facebook

		setContentView(R.layout.reward_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		mMe = MemberAuthStore.getMember(this);

		((TextView) findViewById(R.id.header_bedollars_amount)).setText(""
				+ mMe.getBedollars().intValue());

		Bundle extras = getIntent().getExtras();
		RewardWrapper reward = null;
		boolean isArchived = false;
		if (extras != null) {
			if (extras.getString("reward") != null) {
				reward = new Gson().fromJson(extras.getString("reward"),
						RewardWrapper.class);
				mBestoreReward = reward;
				mIsDetailFromMission = extras
						.getBoolean("is_detail_from_mission");
			} else if (extras.getString("converted_reward") != null) {
				mBoughtReward = new Gson().fromJson(
						extras.getString("converted_reward"), BuyWrapper.class);
				mWalletPosition = extras.getInt("wallet_position");
				mIsConvertedFromMission = extras
						.getBoolean("is_converted_from_mission");
			}

			isArchived = extras.getBoolean("is_archived");
		}

		try {
			if (mBoughtReward == null) { // from bestore
				setupView(mMe, reward);

				/**
				 * If this is the detail of the reward from a mission we remove
				 * the redeem button
				 */
				if (mIsDetailFromMission == false)
					setupActionButton(mMe, reward);
				else
					findViewById(R.id.actionButton).setVisibility(View.GONE);

			} else { // from wallet
				setupView(mMe, mBoughtReward.getReward());
				showCoupon(mBoughtReward, false);
				if (!isArchived) {
					if (mIsConvertedFromMission) { // if it's from mission we
													// handle the archive
													// directly on the activity
						mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.ARCHIVE;
					} else { // if it is from wallet the fragment will archive
								// the reward and remove it from the listView
						mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.ARCHIVE_NO_TASK;
					}
					setupActionButton(mMe, reward);
				} else {
					findViewById(R.id.actionButton).setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setupView(MemberBean me, final RewardWrapper reward)
			throws Exception {

		if (reward.getName() != null) {
			((TextView) findViewById(R.id.rewardBrandName)).setText(reward
					.getName());
			((TextView) findViewById(R.id.rewardName))
					.setText(reward.getName());
		}

		if (reward.getBedollars() != null && reward.getBedollars() > 0)
			((TextView) findViewById(R.id.rewardPrice)).setText(this
					.getResources().getString(R.string.value_no_bedollars,
							reward.getBedollars().intValue()));
		else
			findViewById(R.id.rewardPriceContainer).setVisibility(View.GONE);

		final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPx = (widthPx * 320) / 730;
		ImageView ivBrand = ((ImageView) findViewById(R.id.rewardImage));
		ivBrand.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
				heightPx));

		if (reward.getImagebig() != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_image_placeholder_a)
					.displayer(new FadeInBitmapDisplayer(250))
					.cacheOnDisk(true).preProcessor(new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							Bitmap temp = BeUtils.getRoundedCornerBitmap(
									RewardActivity.this, bitmap, 3,
									bitmap.getWidth(), bitmap.getHeight(),
									false, false, false, false);
							return Bitmap.createScaledBitmap(temp, widthPx,
									heightPx, true);
						}
					}).build();

			ImageLoader.getInstance().displayImage(reward.getImagebig(),
					ivBrand, options);
		}

		if (reward.getBrandName() != null) {
			setTitle(reward.getBrandName());
		}

		if (reward.getDescription() != null)
			((TextView) findViewById(R.id.rewardDescription)).setText(Html
					.fromHtml(reward.getDescription()));

		if (reward.getExpiredate() != null) {
			String expires = getResources().getString(R.string.bestore_expire);
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
			dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date msgDate = dateFormatter.parse(reward.getExpiredate());
			dateFormatter.setTimeZone(TimeZone.getDefault());
			((TextView) findViewById(R.id.rewardExpires)).setText(expires
					+ " "
					+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
							DateFormat.SHORT, Locale.getDefault()).format(
							msgDate));
		} else {
			findViewById(R.id.rewardExpires).setVisibility(View.GONE);
		}

		if (reward.getTos() != null) {
			((TextView) findViewById(R.id.rewardTerms)).setText(Html
					.fromHtml(reward.getTos()));
		} else {
			findViewById(R.id.rewardTerms).setVisibility(View.GONE);
			findViewById(R.id.terms).setVisibility(View.GONE);
		}

		if (reward.getInstructions() != null) {
			((TextView) findViewById(R.id.red_instructions_text)).setText(Html
					.fromHtml(reward.getInstructions()));
		}

		// only if is not from the wallet
		if (mBoughtReward == null && !mIsDetailFromMission
				&& me.getBedollars() < reward.getBedollars()) {
			((ImageView) findViewById(R.id.bedollars_logo))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.bedollar_grey));
			((TextView) findViewById(R.id.rewardPrice))
					.setTextColor(getResources().getColor(R.color.b_gray_texts));
			((TextView) findViewById(R.id.actionButtonText))
					.setText(getResources()
							.getString(R.string.bestore_discover));
			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.reward_missing_bedollars_count))
					.setTextColor(R.color.b_coupon_titles);
			((TextView) findViewById(R.id.reward_missing_bedollars_count))
					.setText(getString(
							R.string.bestore_missing_bedollars_for_reward,
							(int) (reward.getBedollars() - me.getBedollars())));
			((ProgressBar) findViewById(R.id.reward_progress_bar))
					.setProgress((int) ((me.getBedollars() * 100) / reward
							.getBedollars()));
			((TextView) findViewById(R.id.tv_reward_percentage))
					.setText(getString(
							R.string.bestore_missing_bedollars_percentage,
							(int) ((me.getBedollars() * 100) / reward
									.getBedollars())));
		}

		// Only if is not from the wallet, show progress bar also if there are
		// enough bedollars to redeem
		if (mBoughtReward == null && !mIsDetailFromMission
				&& me.getBedollars() >= reward.getBedollars()) {
			findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.reward_missing_bedollars_count))
					.setTextColor(getResources().getColor(R.color.b_green));
			((TextView) findViewById(R.id.reward_missing_bedollars_count))
					.setText(getString(R.string.bestore_bedollars_for_reward));
			((ProgressBar) findViewById(R.id.reward_progress_bar))
					.setProgress(100);
			((TextView) findViewById(R.id.tv_reward_percentage))
					.setText(getString(
							R.string.bestore_missing_bedollars_percentage, 100));
		}

		/**
		 * Check if the email is verified, if not we show the button to send the
		 * verification email again
		 */
		if (!me.isEmailverified() && !mIsDetailFromMission) {
			findViewById(R.id.rewardNotEnough).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.rewardNotEnough))
					.setText(getString(R.string.email_not_verified));
			((TextView) findViewById(R.id.actionButtonText))
					.setText(getResources().getString(
							R.string.email_not_verified_resend));
			mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.VERIFY_EMAIL;
		}

		findViewById(R.id.storesButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(RewardActivity.this,
								MapStoresActivity.class);
						intent.putExtra("brand", reward.getBrandsid());
						intent.putExtra("brandName", reward.getBrandName());
						startActivity(intent);

					}
				});

		findViewById(R.id.facebook_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// FacebookManager
						// .getIstance()
						// .postOnFacebook(
						// RewardActivity.this,
						// mSavedInstanceState,
						// RewardActivity.this
						// .getString(R.string.social_share_app_rewards_giftcards_title),
						// null,
						// RewardActivity.this
						// .getString(R.string.social_share_app_rewards_giftcards_desc),
						// RewardActivity.this
						// .getString(R.string.social_share_app_link),
						// "https://s3-eu-west-1.amazonaws.com/static.beintoo.com/beclub-app/app_icon_android.png");
					}
				});
		findViewById(R.id.twitter_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
		findViewById(R.id.gplus_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						GPlusManager
								.shareStatus(
										RewardActivity.this,
										RewardActivity.this
												.getString(R.string.social_share_app_rewards_giftcards_desc),
										RewardActivity.this
												.getString(R.string.social_share_app_link));
					}
				});
		findViewById(R.id.sms_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Shares.smsShare(
								RewardActivity.this,
								RewardActivity.this
										.getString(R.string.social_share_app_rewards_giftcards_desc)
										+ " "
										+ RewardActivity.this
												.getString(R.string.social_share_app_link));
					}
				});
		findViewById(R.id.email_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Shares.emailShare(
								RewardActivity.this,
								RewardActivity.this
										.getString(R.string.social_share_app_rewards_giftcards_desc)
										+ " "
										+ RewardActivity.this
												.getString(R.string.social_share_app_link),
								RewardActivity.this
										.getString(R.string.social_share_app_rewards_giftcards_title));
					}
				});
	}

	private void setupActionButton(final MemberBean me,
			final RewardWrapper reward) {
		if (mActionButtonBehavior == null) {
			if (me.getBedollars() >= reward.getBedollars()) { // enough
																// bedollars buy
																// it
				mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.BUY;
			} else {
				mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.DISCOVER;
			}
		}

		findViewById(R.id.actionButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						DebugUtility
								.showLog("mActionButtonBehaviormActionButtonBehaviormActionButtonBehavior ----> "
										+ mActionButtonBehavior);
						if (mActionButtonBehavior == ACTION_BUTTON_BEHAVIOR.BUY) { // enough
																					// bedollars
																					// buy
																					// it
							new RewardBuyDialog(reward,
									new RewardBuyDialog.RewardBuyCallback() {
										@Override
										public void onBuy(BuyWrapper buy) {
											DebugUtility.showLog("GOT CODE "
													+ buy);
											mBoughtReward = buy;
											showCoupon(mBoughtReward, true);
											((TextView) findViewById(R.id.header_bedollars_amount))
													.setText(""
															+ MemberAuthStore
																	.getMember(
																			RewardActivity.this)
																	.getBedollars()
																	.intValue());

											findViewById(R.id.ll_progress)
													.setVisibility(View.GONE);
											// scrolling to the coupon position
											findViewById(R.id.scrollView)
													.scrollTo(
															0,
															findViewById(
																	R.id.couponContainer)
																	.getTop());

											mobileAppTracking();

											WidgetBedollars
													.updateWidgetBalance(RewardActivity.this);
										}

										@Override
										public void onCouponNotAvailable() {
											Toast.makeText(
													RewardActivity.this,
													getString(R.string.bestore_coupon_not_available),
													Toast.LENGTH_LONG).show();
										}

										@Override
										public void onCancel(
												RewardWrapper reward) {

										}

										@Override
										public void onError() {
											Toast.makeText(
													RewardActivity.this,
													getString(R.string.something_wrong),
													Toast.LENGTH_LONG).show();
										}
									}).show(getSupportFragmentManager(),
									"RewardBuy");

						} else if (mActionButtonBehavior == ACTION_BUTTON_BEHAVIOR.DISCOVER) { // not
																								// enough
																								// bedollars

							setResult(RESULT_OK);
							finish();
						} else if (mActionButtonBehavior == ACTION_BUTTON_BEHAVIOR.ARCHIVE) { // archive
							if (mBoughtReward != null) {
								DebugUtility.showLog("--->"
										+ mBoughtReward.getMembersRewardsId());
								if (mWalletPosition == null) {
									mWalletPosition = 0;
								}
								archiveReward(mBoughtReward, mWalletPosition);
							}
						} else if (mActionButtonBehavior == ACTION_BUTTON_BEHAVIOR.ARCHIVE_NO_TASK) { // archive
							archiveReward(mBoughtReward, mWalletPosition);
						} else if (mActionButtonBehavior == ACTION_BUTTON_BEHAVIOR.VERIFY_EMAIL) {
							RestAdapter restAdapter = BeRestAdapter
									.getMemberTokenRestAdapter();
							AuthResource.IAuthResource service = restAdapter
									.create(AuthResource.IAuthResource.class);

							service.sendVerificationEmail(MemberAuthStore
									.getAuth(RewardActivity.this).getToken(),
									new Callback<Object>() {
										@Override
										public void success(Object o,
												Response response) {

											Toast.makeText(
													RewardActivity.this,
													getString(R.string.verification_email_sent),
													Toast.LENGTH_LONG).show();
										}

										@Override
										public void failure(RetrofitError error) {

											if (error.isNetworkError()) {
												Toast.makeText(
														RewardActivity.this,
														getString(R.string.connection_error),
														Toast.LENGTH_LONG)
														.show();
											} else {
												Toast.makeText(
														RewardActivity.this,
														getString(R.string.something_wrong),
														Toast.LENGTH_LONG)
														.show();
											}
										}
									});
						}
					}
				});
	}

	private void archiveReward(final BuyWrapper reward,
			final Integer walletPosition) {
		final ProgressDialog progressDialog = ProgressDialog.show(
				RewardActivity.this, "Archive", "Please wait...", true, false);
		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		IMemberResource service = restAdapter.create(IMemberResource.class);

		service.archiveReward(MemberAuthStore.getAuth(RewardActivity.this)
				.getToken(), MemberAuthStore.getMember(RewardActivity.this)
				.getId(), reward.getMembersRewardsId(), "archive",
				new Callback<Object>() {
					@Override
					public void success(Object o, Response response) {

						Toast.makeText(RewardActivity.this,
								getString(R.string.bestore_archived_message),
								Toast.LENGTH_LONG).show();
						progressDialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("wallet_position", walletPosition);
						setResult(RESULT_OK, intent);
						finish();
					}

					@Override
					public void failure(RetrofitError error) {
						if (error.isNetworkError()) {
							Toast.makeText(RewardActivity.this,
									getString(R.string.connection_error),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(RewardActivity.this,
									getString(R.string.something_wrong),
									Toast.LENGTH_LONG).show();

						}
						progressDialog.dismiss();

					}
				});
	}

	private void showCoupon(final BuyWrapper buy, boolean showToast) {
		if (buy.getCode().getAlphanumeric() != null) {
			((TextView) findViewById(R.id.couponCode)).setText(buy.getCode()
					.getAlphanumeric());
			findViewById(R.id.couponCode).setVisibility(View.VISIBLE);
		}

		if (buy.getCode().getImagecode() != null) {
			findViewById(R.id.couponQrImage).setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					buy.getCode().getImagecode(),
					((ImageView) findViewById(R.id.couponQrImage)));
		}

		if (buy.getCode().getCardcode() != null) {
			if (buy.getCode().getCardcode().getNumber() != null) {
				((TextView) findViewById(R.id.couponCode)).setText(buy
						.getCode().getCardcode().getNumber());
				findViewById(R.id.couponCode).setVisibility(View.VISIBLE);
				findViewById(R.id.couponCodeDesc).setVisibility(View.VISIBLE);
			}

			if (buy.getCode().getCardcode().getPin() != null) {
				((TextView) findViewById(R.id.couponPin)).setText(buy.getCode()
						.getCardcode().getPin());
				findViewById(R.id.couponPin).setVisibility(View.VISIBLE);
				findViewById(R.id.couponPinDesc).setVisibility(View.VISIBLE);
			}
		}

		if (buy.getCode().getUrlcode() != null) {
			if (buy.getCode().getUrlcode().getNumber() != null) {
				((TextView) findViewById(R.id.couponCode)).setText(buy
						.getCode().getUrlcode().getNumber());
				findViewById(R.id.couponCode).setVisibility(View.VISIBLE);
				findViewById(R.id.couponCodeDesc).setVisibility(View.VISIBLE);
			}
			if (buy.getCode().getUrlcode().getUrl() != null) {
				findViewById(R.id.coupon_url_button)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.coupon_url_button).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Intent intent = new Intent(RewardActivity.this,
										RewardExternalWebView.class);
								intent.putExtra("url", buy.getCode()
										.getUrlcode().getUrl());
								startActivity(intent);
							}
						});
				((TextView) findViewById(R.id.coupon_payoff))
						.setText(getString(R.string.bestore_url_follow));
			}

			if (buy.getCode().getUrlcode().getNumber() != null) {
				((TextView) findViewById(R.id.bestore_url_button_text))
						.setText(getString(R.string.bestore_url_and_number_coupon));
			}
		}

		if (buy.getCode().getUrl() != null) {
			findViewById(R.id.coupon_url_button).setVisibility(View.VISIBLE);
			findViewById(R.id.coupon_url_button).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(RewardActivity.this,
									RewardExternalWebView.class);
							intent.putExtra("url", buy.getCode().getUrl());
							startActivity(intent);
						}
					});
			((TextView) findViewById(R.id.coupon_payoff))
					.setText(getString(R.string.bestore_url_follow));
		}

		/**
		 * We start the animation only if the reward was paid right now not if
		 * it's from the wallet or from a mission
		 */
		if (mBoughtReward == null) {
			Animation fadeIn = AnimationUtils.loadAnimation(
					RewardActivity.this, android.R.anim.fade_in);
			fadeIn.setDuration(1000);
			findViewById(R.id.couponContainer).startAnimation(fadeIn);
		}
		findViewById(R.id.couponContainer).setVisibility(View.VISIBLE);

		if (showToast) {
			Toast.makeText(
					RewardActivity.this,
					RewardActivity.this.getResources().getString(
							R.string.bestore_buydialog_bought),
					Toast.LENGTH_LONG).show();
		}

		mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.ARCHIVE;

		((TextView) findViewById(R.id.actionButtonText))
				.setText(RewardActivity.this.getResources().getString(
						R.string.bestore_buydialog_archive));

		findViewById(R.id.storesButton).setVisibility(View.VISIBLE);

		if (mBoughtReward != null
				&& mBoughtReward.getReward().getInstructions() != null) {
			findViewById(R.id.red_instructions_title).setVisibility(
					View.VISIBLE);
			findViewById(R.id.red_instructions_text)
					.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	private void mobileAppTracking() {
		try {
			MATEventItem mei = new MATEventItem("reward-purchased",
					MemberAuthStore.getMember(this).getId(), mBoughtReward
							.getReward().getId(), mBoughtReward.getReward()
							.getName(), null, null);
			MobileAppTracker.getInstance().measureAction("reward-purchased",
					mei);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// AppEventsLogger logger = AppEventsLogger.newLogger(this);
		// Bundle args = new Bundle();
		// args.putString("memberID", MemberAuthStore.getMember(this).getId());
		// args.putString("rewardID", mBoughtReward.getReward().getId());
		// args.putString("rewardName", mBoughtReward.getReward().getName());
		// logger.logEvent("reward-purchased", args);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// if (requestCode == 64206) { // FACEBOOK LOGIN
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
		// } else
		if (requestCode == 64207 && resultCode == RESULT_OK) { // Facebook
																// Share
																// Dialog
			// FacebookManager.getIstance().facebookUiHelperActivityResultHandler(
			// requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!mMe.isEmailverified()) {
			new MeTask(this, new MeTask.MeCallback() {
				@Override
				public void onComplete(MeAndAuthWrapper meAndAuth) {
					mMe = meAndAuth.getMe().getMember();
					if (mMe.isEmailverified()) {
						findViewById(R.id.rewardNotEnough).setVisibility(
								View.GONE);
						((TextView) findViewById(R.id.actionButtonText))
								.setText(getResources().getString(
										R.string.bestore_redeem));
						mActionButtonBehavior = ACTION_BUTTON_BEHAVIOR.BUY;
					}
				}

				@Override
				public void onError(AsyncTasks.Result result) {

				}
			}).execute();
		}

	}
}
