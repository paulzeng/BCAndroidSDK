package com.beintoo.activities;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MemberBean;

public class CardsVerifyEmail extends FragmentActivity {

	private static final String mScreenName = "CARDS-VERIFY-EMAIL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards_email_verification);

		getActionBar().setTitle("Email verification");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		MemberBean me = MemberAuthStore.getMember(this);

		TextView tvName = (TextView) findViewById(R.id.tvName);
		tvName.setText(getString(R.string.cards_verify_email_name, me.getName()));

		final TextView tvActionText = (TextView) findViewById(R.id.action_button_text);
		tvActionText.setText("Resend the email");

		TextView tvWhy = (TextView) findViewById(R.id.tv_why);
		tvWhy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(CardsVerifyEmail.this,
						WhyRequired.class));
			}
		});

		final RelativeLayout rlActionContainer = (RelativeLayout) findViewById(R.id.action_button_content);
		rlActionContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				RestAdapter restAdapter = BeRestAdapter
						.getMemberTokenRestAdapter();
				AuthResource.IAuthResource service = restAdapter
						.create(AuthResource.IAuthResource.class);

				service.sendVerificationEmail(
						MemberAuthStore.getAuth(CardsVerifyEmail.this)
								.getToken(), new Callback<Object>() {
							@Override
							public void success(Object o, Response response) {
								Toast.makeText(
										CardsVerifyEmail.this,
										getString(R.string.verification_email_sent),
										Toast.LENGTH_LONG).show();
								tvActionText
										.setText("Mail sent - Check your mailbox");
								rlActionContainer.setOnClickListener(null);
							}

							@Override
							public void failure(RetrofitError error) {
								if (error.isNetworkError()) {
									Toast.makeText(
											CardsVerifyEmail.this,
											getString(R.string.connection_error),
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(
											CardsVerifyEmail.this,
											getString(R.string.something_wrong),
											Toast.LENGTH_LONG).show();
								}
							}
						});
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
