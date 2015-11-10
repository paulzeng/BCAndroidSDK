package com.beintoo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.beintoo.beclubapp.R;

public class IntroducingCardLinkingActivity extends Activity {

	public static final int REQUEST_CREDIT_CARD_LINKING = 4200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introducing_card_linking_activity);

		View linkInclude = findViewById(R.id.link_include);
		((TextView) linkInclude.findViewById(R.id.action_button_text))
				.setText(getString(R.string.cards_link_card_now));
		linkInclude.findViewById(R.id.action_button_content)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						setResult(RESULT_OK);
						finish();
					}
				});

		findViewById(R.id.rl_continue_to_app).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});

	}
}
