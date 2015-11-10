package com.beintoo.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beintoo.beclubapp.R;

public class TutorialDialog extends DialogFragment {

	private Context mContext;
	private final int STEPS = 5;
	private int mCurrentStep = 1;

	private final String mScreenName = "TUTORIAL";

	public TutorialDialog(Context context) {
		mContext = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.tutorial_dialog, null);

		final ImageView is1 = (ImageView) view.findViewById(R.id.imageSwitcher);
		final ImageView is3 = (ImageView) view
				.findViewById(R.id.imageSwitcher3);
		final ImageView is4 = (ImageView) view
				.findViewById(R.id.imageSwitcher4);
		final ImageView is5 = (ImageView) view
				.findViewById(R.id.imageSwitcher5);

		final TextView title = (TextView) view.findViewById(R.id.title);
		final TextView text = (TextView) view.findViewById(R.id.text);
		final LinearLayout skipNextContainer = (LinearLayout) view
				.findViewById(R.id.skip_next_container);
		final TextView getStarted = (TextView) view
				.findViewById(R.id.get_started);

		final Animation slideIn = AnimationUtils.loadAnimation(mContext,
				android.R.anim.slide_in_left);
		slideIn.setDuration(1000);
		final Animation slideOut = AnimationUtils.loadAnimation(mContext,
				android.R.anim.slide_out_right);
		final Animation fadeIn = AnimationUtils.loadAnimation(mContext,
				android.R.anim.fade_in);

		view.findViewById(R.id.next).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						++mCurrentStep;
						if (mCurrentStep == 2) {
							title.startAnimation(fadeIn);
							text.startAnimation(fadeIn);
							is3.startAnimation(slideIn);
							is1.setVisibility(View.GONE);
							is3.setVisibility(View.VISIBLE);
							title.setText(getString(R.string.tutorial_mainText3));
							text.setText(getString(R.string.tutorial_descText3));
						} else if (mCurrentStep == 3) {
							title.startAnimation(fadeIn);
							text.startAnimation(fadeIn);
							is4.startAnimation(slideIn);
							is3.setVisibility(View.GONE);
							is4.setVisibility(View.VISIBLE);
							title.setText(getString(R.string.tutorial_mainText4));
							text.setText(getString(R.string.tutorial_descText4));

						} else if (mCurrentStep == 4) {
							title.startAnimation(fadeIn);
							text.startAnimation(fadeIn);
							is5.startAnimation(slideIn);
							is4.setVisibility(View.GONE);
							is5.setVisibility(View.VISIBLE);
							title.setText(getString(R.string.tutorial_mainText5));
							text.setText(getString(R.string.tutorial_descText5));

							skipNextContainer.setVisibility(View.GONE);
							getStarted.setVisibility(View.VISIBLE);
						}
					}
				});

		view.findViewById(R.id.skip).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dismiss();
					}
				});

		view.findViewById(R.id.get_started).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dismiss();
					}
				});

		Dialog dialog = new AlertDialog.Builder(mContext).setView(view)
				.setCancelable(false).setInverseBackgroundForced(true).create();
		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}
}
