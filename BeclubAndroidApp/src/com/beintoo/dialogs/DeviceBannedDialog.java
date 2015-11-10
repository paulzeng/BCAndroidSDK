package com.beintoo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.FraudManager;

public class DeviceBannedDialog extends DialogFragment {

	private Context mContext;
	private boolean mShowLogout;

	private final static String SCREEN_NAME = "BANNED-DEVICE";
	private final static String SCREEN_NAME_W_LOGOUT = "BANNED-DEVICE-WITH-LOGOUT";

	public DeviceBannedDialog(Context context, boolean showLogout) {
		mContext = context;
		mShowLogout = showLogout;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.device_banned_dialog, null);

		view.findViewById(R.id.banned_logout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getActivity().setResult(
								FraudManager.BANNED_ACTIVITY_RESULT);
						getActivity().finish();
						dismiss();

					}
				});

		view.findViewById(R.id.close).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dismiss();
					}
				});

		if (mShowLogout) {
			view.findViewById(R.id.close).setVisibility(View.GONE);
			view.findViewById(R.id.banned_logout).setVisibility(View.VISIBLE);
			view.findViewById(R.id.logout_img).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.close).setVisibility(View.VISIBLE);
			view.findViewById(R.id.banned_logout).setVisibility(View.GONE);
			view.findViewById(R.id.warning_img).setVisibility(View.VISIBLE);
		}

		/*
		 * Dialog dialog = new AlertDialog.Builder(mContext) .setView(view)
		 * .setCancelable(false) .setInverseBackgroundForced(true) .create();
		 */
		Dialog dialog = new Dialog(mContext);

		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Window window = getDialog().getWindow();
		WindowManager.LayoutParams attributes = window.getAttributes();
		// must setBackgroundDrawable(TRANSPARENT) in onActivityCreated()
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);

		getDialog().setCancelable(false);
		getDialog().setCanceledOnTouchOutside(false);

	}
}
