package com.beintoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.beintoo.beclubapp.BeclubActivityChooser;
import com.beintoo.beclubapp.R;

public class MemberEntryActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BeclubActivityChooser.memberEntryActivity = this;

		setContentView(R.layout.member_entry_activity);
		getActionBar().hide();

		// 使用facebook进行第三方登陆
		findViewById(R.id.facebook_login_right).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							// TODO facebook登陆

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		findViewById(R.id.signup_by_email).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(MemberEntryActivity.this,
								MemberSignupActivity.class)); 
					}
				});

		findViewById(R.id.existing_account).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startActivity(new Intent(MemberEntryActivity.this,
								MemberLoginActivity.class)); 
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onStart() {
		super.onStart();
	}
}
