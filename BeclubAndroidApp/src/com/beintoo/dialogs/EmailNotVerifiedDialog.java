package com.beintoo.dialogs;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.beintoo.activities.WhyRequired;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;

public class EmailNotVerifiedDialog extends DialogFragment {

	private Context mContext;

	public EmailNotVerifiedDialog(Context context) {
		mContext = context;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.email_not_verified_dialog,
				null);

		view.findViewById(R.id.send_verification_email).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						RestAdapter restAdapter = BeRestAdapter
								.getMemberTokenRestAdapter();
						AuthResource.IAuthResource service = restAdapter
								.create(AuthResource.IAuthResource.class);

						service.sendVerificationEmail(
								MemberAuthStore.getAuth(mContext).getToken(),
								new Callback<Object>() {
									@Override
									public void success(Object o,
											Response response) {
										Toast.makeText(
												getActivity(),
												getString(R.string.verification_email_sent),
												Toast.LENGTH_LONG).show();
										dismiss();
									}

									@Override
									public void failure(RetrofitError error) {
										if (error.isNetworkError()) {
											Toast.makeText(
													getActivity(),
													getString(R.string.connection_error),
													Toast.LENGTH_LONG).show();
										} else {
											Toast.makeText(
													getActivity(),
													getString(R.string.something_wrong),
													Toast.LENGTH_LONG).show();
										}
										dismiss();
									}
								});
					}
				});
		view.findViewById(R.id.email_not_verified_popup_whyreq)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(getActivity(),
								WhyRequired.class));

					}
				});

		Dialog dialog = new AlertDialog.Builder(mContext)
				.setView(view)
				.setNegativeButton(
						getString(R.string.email_not_verified_popup_later),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
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

	@Override
	public void onStart() {
		super.onStart();

	}
}
