package com.beintoo.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.social.GPlusManager;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectiveMissionCompleteActivity extends BeNotificationActivity {

	public static final int RESULT_OPEN_BESTORE_OR_WALLET = 111;
	private Bundle mSavedInstanceState;
	private AssignedMissionWrapper mAssignedMission;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSavedInstanceState = savedInstanceState; // needed from facebook

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		mAssignedMission = new Gson().fromJson(
				getIntent().getStringExtra("assigned_mission"),
				AssignedMissionWrapper.class);
		if (mAssignedMission.getReward() != null) {
			setContentView(R.layout.collective_complete_w_reward_activity);
		} else {
			setContentView(R.layout.collective_complete_activity);
		}

		TextView tvBrandName = (TextView) findViewById(R.id.tv_brand_name);
		ImageButton fbShare = (ImageButton) findViewById(R.id.facebook_share);
		ImageButton twitterShare = (ImageButton) findViewById(R.id.twitter_share);
		ImageButton gplusShare = (ImageButton) findViewById(R.id.gplus_share);

		int participants = mAssignedMission.getPartecipants();

		if (mAssignedMission.getReward() != null) {
			tvBrandName.setText(getString(
					R.string.collective_mission_thanks_friends_reward,
					participants));
			ImageLoader.getInstance().displayImage(
					mAssignedMission.getReward().getImagebig(),
					(ImageView) findViewById(R.id.collective_reward_image));
			findViewById(R.id.rl_visit_wallet).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(
									CollectiveMissionCompleteActivity.this,
									MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
							intent.putExtra("eventtype", "URL_SCHEME_LAUNCH");
							intent.putExtra("path", "/userprofile");
							startActivity(intent);
							finish();
						}
					});
			findViewById(R.id.reward_info).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(
									CollectiveMissionCompleteActivity.this,
									RewardActivity.class);
							intent.putExtra("reward", mAssignedMission
									.getReward().toString());
							intent.putExtra("is_detail_from_mission", true);
							startActivity(intent);
						}
					});
		} else {
			TextView tvBeDollars = (TextView) findViewById(R.id.tv_qnt_bedollars);
			RelativeLayout rlVisitBestore = (RelativeLayout) findViewById(R.id.rl_visit_bestore);
			int beDollars = mAssignedMission.getMissionswrapper().getMission()
					.getBedollars().intValue();
			tvBeDollars.setText(getString(
					R.string.collective_mission_qnt_bedollars, beDollars));
			tvBrandName.setText(getString(
					R.string.collective_mission_thanks_friends, participants));
			rlVisitBestore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(
							CollectiveMissionCompleteActivity.this,
							MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
					intent.putExtra("eventtype", "URL_SCHEME_LAUNCH");
					intent.putExtra("path", "/bestore");
					startActivity(intent);
					finish();
				}
			});
		}

		fbShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// FacebookManager
				// .getIstance()
				// .postOnFacebook(
				// CollectiveMissionCompleteActivity.this,
				// mSavedInstanceState,
				// CollectiveMissionCompleteActivity.this
				// .getString(R.string.social_share_app_rewards_giftcards_title),
				// null,
				// CollectiveMissionCompleteActivity.this
				// .getString(R.string.social_share_app_rewards_giftcards_desc),
				// CollectiveMissionCompleteActivity.this
				// .getString(R.string.social_share_app_link),
				// "https://s3-eu-west-1.amazonaws.com/static.beintoo.com/beclub-app/app_icon_android.png");
			}
		});

		twitterShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		gplusShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				GPlusManager
						.shareStatus(
								CollectiveMissionCompleteActivity.this,
								CollectiveMissionCompleteActivity.this
										.getString(R.string.social_share_app_rewards_giftcards_desc),
								CollectiveMissionCompleteActivity.this
										.getString(R.string.social_share_app_link));
			}
		});

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(NotificationID.PUSH_NOTIFICATION.getId());
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
}
