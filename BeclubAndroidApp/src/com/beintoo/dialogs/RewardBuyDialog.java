package com.beintoo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.beintoo.asynctasks.RewardBuyTask;
import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.RewardWrapper;

public class RewardBuyDialog extends DialogFragment {

	public interface RewardBuyCallback {
		public void onBuy(BuyWrapper buy);

		public void onCouponNotAvailable();

		public void onCancel(RewardWrapper reward);

		public void onError();
	}

	private RewardWrapper mReward;
	private RewardBuyCallback mCallback;

	public RewardBuyDialog(RewardWrapper reward, RewardBuyCallback callback) {
		mReward = reward;
		mCallback = callback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(
						this.getResources().getString(
								R.string.bestore_buydialog_title))
				.setMessage(
						String.format(
								getResources().getString(
										R.string.bestore_buydialog_message),
								mReward.getBedollars().intValue()))
				.setPositiveButton(R.string.bestore_buydialog_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								buyReward();
							}
						})
				.setNegativeButton(R.string.bestore_buydialog_negative,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mCallback.onCancel(mReward);

							}
						}).setInverseBackgroundForced(true).create();
	}

	private void buyReward() {
		try {
			new RewardBuyTask(getActivity(), mReward, mCallback).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
