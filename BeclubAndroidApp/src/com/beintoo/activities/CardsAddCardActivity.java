package com.beintoo.activities;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.SpreedlyRegistrationTask;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.ProfileCardsFragment;
import com.beintoo.utils.BeLocationManager;
import com.beintoo.utils.CardSpringUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.BeTextWatcher;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.creditcard.SpreedlyResponse;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MobileAppTracker;

public class CardsAddCardActivity extends FragmentActivity {

	private static final String mScreenName = "ADD_NEW_CARD";
	private static final int SCAN_REQUEST_CODE = 1101;

	private EditText etBox1;
	private EditText etBox2;
	private EditText etBox3;
	private EditText etBox4;

	private EditText etMonth;
	private EditText etYear;

	private TextView tvCardError;
	private TextView tvTos;

	private CheckBox cbTos;

	private RelativeLayout rlInvalidCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards_add_card_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("Add a card");

		changeActionButtonState(false);

		final MemberBean me = MemberAuthStore.getMember(this);
		if (!me.isEmailverified()) {
			startActivity(new Intent(this, CardsVerifyEmail.class));
			finish();
		}

		rlInvalidCard = (RelativeLayout) findViewById(R.id.rl_invalid_credit_number);
		tvCardError = (TextView) findViewById(R.id.tv_card_error);
		cbTos = (CheckBox) findViewById(R.id.cbTos);
		tvTos = (TextView) findViewById(R.id.tv_tos);

		RelativeLayout rlScanCards = (RelativeLayout) findViewById(R.id.relativeLayout3);
		rlScanCards.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CardsAddCardActivity.this,
						CardIOActivity.class);
				intent.putExtra(CardIOActivity.EXTRA_APP_TOKEN,
						getString(R.string.card_io_app_token));
				intent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,
						true);
				intent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION,
						true);
				intent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
				startActivityForResult(intent, SCAN_REQUEST_CODE);
			}
		});

		final TextView tvActionText = (TextView) findViewById(R.id.action_button_text);
		tvActionText.setText(getString(R.string.cards_add_card_link_my_card));

		etBox1 = (EditText) findViewById(R.id.editText);
		etBox2 = (EditText) findViewById(R.id.editText2);
		etBox3 = (EditText) findViewById(R.id.editText3);
		etBox4 = (EditText) findViewById(R.id.editText4);

		etMonth = (EditText) findViewById(R.id.et_month);
		etYear = (EditText) findViewById(R.id.et_year);

		final BeTextWatcher beTextWatcher = new BeTextWatcher(etBox1, etBox2,
				etBox3, etBox4, etMonth, etYear,
				new BeTextWatcher.CardInsertListener() {
					@Override
					public void onCardNumberInserted(boolean validMonth,
							boolean validYear) {
						boolean isValid = CardSpringUtils.luhnValidator(etBox1
								.getText().toString()
								+ etBox2.getText().toString()
								+ etBox3.getText().toString()
								+ etBox4.getText().toString());
						if (isValid) {
							if (!validMonth) {
								rlInvalidCard.setVisibility(View.VISIBLE);
								tvCardError.setText("Invalid month.");
							} else if (!validYear) {
								rlInvalidCard.setVisibility(View.VISIBLE);
								tvCardError.setText("Invalid year.");
							} else {
								rlInvalidCard.setVisibility(View.GONE);
							}
						} else {
							tvCardError
									.setText(getString(R.string.cards_add_card_invalid_card_number));
							rlInvalidCard.setVisibility(View.VISIBLE);
						}
						changeActionButtonState(isValid && validMonth
								&& validYear && cbTos.isChecked());
					}

					@Override
					public void onCardNumberInvalid() {
						changeActionButtonState(false);
					}
				});

		etBox1.addTextChangedListener(beTextWatcher);
		etBox2.addTextChangedListener(beTextWatcher);
		etBox3.addTextChangedListener(beTextWatcher);
		etBox4.addTextChangedListener(beTextWatcher);
		etMonth.addTextChangedListener(beTextWatcher);
		etYear.addTextChangedListener(beTextWatcher);

		cbTos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,
					boolean b) {
				beTextWatcher.onTextChanged("", 0, 0, 0);
			}
		});

		tvTos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CardsAddCardActivity.this,
						RewardExternalWebView.class);
				intent.putExtra("url", "http://www.google.it");
				startActivity(intent);
			}
		});
	}

	private void changeActionButtonState(boolean enabled) {
		try {
			if (!enabled) {
				((RelativeLayout) findViewById(R.id.action_button_content))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.action_button_content)),
								this.getResources().getDrawable(
										R.drawable.disabled_button_selector));
				((TextView) findViewById(R.id.action_button_text))
						.setTextColor(Color.WHITE);
				RelativeLayout rlActionContent = (RelativeLayout) findViewById(R.id.action_button_content);
				rlActionContent.setOnClickListener(null);
			} else {
				((RelativeLayout) findViewById(R.id.action_button_content))
						.getClass()
						.getMethod(
								android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground"
										: "setBackgroundDrawable",
								Drawable.class)
						.invoke(((RelativeLayout) findViewById(R.id.action_button_content)),
								this.getResources().getDrawable(
										R.drawable.green_button));
				((TextView) findViewById(R.id.action_button_text))
						.setTextColor(Color.WHITE);
				RelativeLayout rlActionContent = (RelativeLayout) findViewById(R.id.action_button_content);
				rlActionContent.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new SpreedlyRegistrationTask(
								CardsAddCardActivity.this,
								etBox1.getText().toString()
										+ etBox2.getText().toString()
										+ etBox3.getText().toString()
										+ etBox4.getText().toString(),
								etMonth.getText().toString(),
								etYear.getText().toString(),
								new SpreedlyRegistrationTask.CardsRegistrationListener() {
									@Override
									public void onRegistrationCompleted(
											SpreedlyResponse cardsInfoResponse) {
										if (cardsInfoResponse == null) {
											Toast.makeText(
													CardsAddCardActivity.this,
													"Registration error. Please check information and retry.",
													Toast.LENGTH_LONG).show();
											return;
										}

										Location loc = BeLocationManager
												.getInstance(
														CardsAddCardActivity.this)
												.getLastKnowLocation(
														CardsAddCardActivity.this);
										MATEventItem eventItem = new MATEventItem(
												"credit-card-linked",
												MemberAuthStore
														.getMember(
																CardsAddCardActivity.this)
														.getId(), ""
														+ loc.getLatitude(), ""
														+ loc.getLongitude(),
												cardsInfoResponse
														.getTransaction()
														.getPayment_method()
														.getCard_type(), null);
										MobileAppTracker.getInstance()
												.measureAction(
														"credit-card-linked",
														eventItem);

										Intent intent = new Intent(
												CardsAddCardActivity.this,
												CardsCardAddedActivity.class);
										startActivityForResult(
												intent,
												ProfileCardsFragment.REQUEST_MODIFY_CARD);
										finish();
									}

									@Override
									public void onRegistrationError() {
										Toast.makeText(
												CardsAddCardActivity.this,
												"Card already registered! Please insert another one.",
												Toast.LENGTH_LONG).show();
									}
								}).execute();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SCAN_REQUEST_CODE) {
			String resultDisplayStr;
			if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
				CreditCard scanResult = data
						.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

				// Never log a raw card number. Avoid displaying it, but if
				// necessary use getFormattedCardNumber()
				resultDisplayStr = "Card Number: "
						+ scanResult.getRedactedCardNumber() + "\n";

				String cardNumber[] = scanResult.getFormattedCardNumber()
						.split(" ");
				etBox1.setText(cardNumber[0]);
				etBox2.setText(cardNumber[1]);
				etBox3.setText(cardNumber[2]);
				etBox4.setText(cardNumber[3]);

				etMonth.setText("" + scanResult.expiryMonth);
				etYear.setText("" + scanResult.expiryYear);

				// Do something with the raw number, e.g.:
				// new SpreedlyRegistrationTask(this, scanResult.cardNumber, new
				// SpreedlyRegistrationTask.CardsRegistrationListener() {
				// @Override
				// public void onRegistrationCompleted(CardsInfoResponse
				// cardsInfoResponse) {
				// Location loc =
				// BeLocationManager.getInstance(CardsAddCardActivity.this).getLastKnowLocation(CardsAddCardActivity.this);
				// MATEventItem eventItem = new
				// MATEventItem("credit-card-linked",
				// MemberAuthStore.getMember(CardsAddCardActivity.this).getId(),
				// ""+ loc.getLatitude(), ""+loc.getLongitude(),
				// cardsInfoResponse.getBrand_string(), null);
				// MobileAppTracker.getInstance().measureAction("credit-card-linked",
				// eventItem);
				// Analytics.trackEvent(CardsAddCardActivity.this,
				// "CREDIT_CARD", "action", "LINKED_NEW_CARD", 1L);
				//
				// try {
				// AppEventsLogger logger =
				// AppEventsLogger.newLogger(CardsAddCardActivity.this);
				// Bundle args = new Bundle();
				// args.putString("memberID",
				// MemberAuthStore.getMember(CardsAddCardActivity.this).getId());
				// args.putString("lat", "" + loc.getLatitude());
				// args.putString("lon", "" + loc.getLongitude());
				// args.putString("card_type",
				// cardsInfoResponse.getBrand_string());
				// logger.logEvent("credit-card-linked", args);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				//
				// Intent intent = new Intent(CardsAddCardActivity.this,
				// CardsCardAddedActivity.class);
				// startActivityForResult(intent,
				// ProfileCardsFragment.REQUEST_MODIFY_CARD);
				// finish();
				// // Intent intent = new Intent();
				// // intent.putExtra("card", cardsInfoResponse.toString());
				// // setResult(ProfileCardsFragment.RESULT_MODIFIED_CARD,
				// intent);
				// // finish();
				// }
				//
				// @Override
				// public void onRegistrationError() {
				// Toast.makeText(CardsAddCardActivity.this,
				// "Card already registered! Please insert another one.",
				// Toast.LENGTH_LONG).show();
				// }
				// }).execute();

				if (scanResult.isExpiryValid()) {
					resultDisplayStr += "Expiration Date: "
							+ scanResult.expiryMonth + "/"
							+ scanResult.expiryYear + "\n";
				}

				if (scanResult.cvv != null) {
					// Never log or display a CVV
					resultDisplayStr += "CVV has " + scanResult.cvv.length()
							+ " digits.\n";
				}

				if (scanResult.postalCode != null) {
					resultDisplayStr += "Postal Code: " + scanResult.postalCode
							+ "\n";
				}
			} else {
				resultDisplayStr = "Scan was canceled.";
			}
		}
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
