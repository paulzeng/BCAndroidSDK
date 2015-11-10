package com.beintoo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beintoo.activities.RewardActivity;
import com.beintoo.asynctasks.MissionCompleteTask;
import com.beintoo.beclubapp.R;
import com.beintoo.social.GPlusManager;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AssignedMissionWrapper;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MissionUnlockedDialog extends DialogFragment {

	private Context mContext;
	final private AssignedMissionWrapper mMssion;
	final private String mPopupTitle;
	private MissionCompleteTask.MissionCompleteCallback mCallback;

	public MissionUnlockedDialog(Context context, String title,
			AssignedMissionWrapper mission,
			MissionCompleteTask.MissionCompleteCallback callback) {
		mContext = context;
		mPopupTitle = title;
		mMssion = mission;
		mCallback = callback;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view;
		if (mMssion.getReward() == null) {
			view = inflater.inflate(R.layout.scan_product_scanned_dialog, null);
			((TextView) view.findViewById(R.id.scan_amount_won))
					.setText(mContext.getString(
							R.string.earn_bedollars_scan_popup3, mMssion
									.getBedollars().intValue()));
			((TextView) view.findViewById(R.id.mission_dialog_title))
					.setText(mPopupTitle);
		} else {
			view = inflater.inflate(R.layout.mission_complete_w_reward, null);
			((TextView) view.findViewById(R.id.mission_dialog_title))
					.setText(mPopupTitle);
			((TextView) view.findViewById(R.id.mission_bedollars_amount))
					.setText("+"
							+ getString(R.string.value_bedollars, mMssion
									.getBedollars().intValue()));

			ImageView brandImage = ((ImageView) view
					.findViewById(R.id.mission_completed_offer_image));
			ImageLoader.getInstance().displayImage(
					mMssion.getReward().getImagebig(), brandImage);

			((TextView) view.findViewById(R.id.mission_completed_offer_name))
					.setText(mMssion.getReward().getName());

			if (mMssion.getMembersrewards() != null) {
				view.findViewById(R.id.coupon_url_button).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View view) {

								Intent intent = new Intent(mContext,
										RewardActivity.class);
								mMssion.getMembersrewards()
										.setMembersRewardsId(
												mMssion.getMembersrewardsid());
								mMssion.getMembersrewards().setReward(
										mMssion.getReward());
								intent.putExtra("converted_reward", mMssion
										.getMembersrewards().toString());
								intent.putExtra("wallet_position", 0);
								intent.putExtra("is_converted_from_mission",
										true);
								startActivity(intent);
							}
						});
			}
		}

		view.findViewById(R.id.facebook_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// FacebookManager
						// .getIstance()
						// .postOnFacebook(
						// getActivity(),
						// savedInstanceState,
						// mContext.getString(R.string.social_share_app_rewards_giftcards_title),
						// null,
						// mContext.getString(R.string.social_share_app_rewards_giftcards_desc),
						// mContext.getString(R.string.social_share_app_link),
						// "https://s3-eu-west-1.amazonaws.com/static.beintoo.com/beclub-app/app_icon_android.png");
					}
				});
		view.findViewById(R.id.twitter_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
		view.findViewById(R.id.gplus_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						GPlusManager.shareStatus(
								getActivity(),
								mContext.getString(R.string.social_share_app_rewards_giftcards_desc),
								mContext.getString(R.string.social_share_app_link));
					}
				});

		Dialog dialog = new AlertDialog.Builder(mContext)
				.setView(view)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (mCallback != null)
									mCallback.onPopupClosed();
							}
						}).setCancelable(false)
				.setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			MATEventItem item = new MATEventItem("bedollars", 1,
					mMssion.getBedollars(), 0, MemberAuthStore.getMember(
							getActivity()).getId(), mMssion.getId(), null,
					null, null);
			MobileAppTracker.getInstance().measureAction("mission-completed",
					item);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try {
		// AppEventsLogger logger = AppEventsLogger.newLogger(getActivity());
		// Bundle args = new Bundle();
		// args.putString("memberID", MemberAuthStore.getMember(getActivity())
		// .getId());
		// args.putString("missionID", mMssion.getId());
		// args.putDouble("bedollars", mMssion.getBedollars());
		// logger.logEvent("mission-completed", args);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
