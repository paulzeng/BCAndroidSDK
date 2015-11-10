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

import com.beintoo.activities.CollectiveMissionContactsActivity;
import com.beintoo.beclubapp.R;

public class CollectiveMissionChooserDialog extends DialogFragment {

	private final static String mScreenName = "EARN-BEDOLLARS-COLLECTIVE-FRIENDS-POPUP-CHOOSE-HOW";

	private int mMinFriends;
	private String mMatURL;
	private String mShareMessage;
	private String mMissionId;

	public static CollectiveMissionChooserDialog newInstance(int minFriends,
			String mathUrl, String shareMessage, String missionId) {
		CollectiveMissionChooserDialog dialog = new CollectiveMissionChooserDialog();
		dialog.mMinFriends = minFriends;
		dialog.mMatURL = mathUrl;
		dialog.mShareMessage = shareMessage;
		dialog.mMissionId = missionId;
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(
				R.layout.collective_mission_chooser_dialog, null);

		view.findViewById(R.id.collective_mission_fb_share).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(),
								CollectiveMissionContactsActivity.class);
						intent.putExtra(
								"source",
								CollectiveMissionContactsActivity.ContactSources.FACEBOOK
										.getValue());
						intent.putExtra("min_friends", mMinFriends);
						intent.putExtra("mat_url", mMatURL);
						intent.putExtra("share_message", mShareMessage);
						intent.putExtra("mission_id", mMissionId);

						startActivity(intent);

					}
				});

		view.findViewById(R.id.collective_mission_text_share)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getActivity(),
								CollectiveMissionContactsActivity.class);
						intent.putExtra(
								"source",
								CollectiveMissionContactsActivity.ContactSources.SMS
										.getValue());
						intent.putExtra("min_friends", mMinFriends);
						intent.putExtra("mat_url", mMatURL);
						intent.putExtra("share_message", mShareMessage);
						intent.putExtra("mission_id", mMissionId);

						startActivity(intent);

					}
				});

		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setView(view)
				.setPositiveButton(R.string.collective_mission_dialog_later,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).setCancelable(false)
				.setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
