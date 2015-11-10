package com.beintoo.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.CardsDeleteCardTask;
import com.beintoo.asynctasks.CardsRetrieveBalanceTask;
import com.beintoo.asynctasks.CardsRetrieveTransactionsTask;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.ProfileCardsFragment;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.EndlessScrollListener;
import com.beintoo.wrappers.CardTransactionBean;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.PaginatedList;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class CardsTransactionActivity extends FragmentActivity {

	private String mLastKey;
	private EndlessScrollListener mListener;

	private final String mScreenName = "CARD_TRANSACTIONS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards_transactions_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle(
				getString(R.string.cards_transaction_activity_title));

		final CardsInfoResponse card = new Gson().fromJson(getIntent()
				.getStringExtra("card"), CardsInfoResponse.class);
		if (card == null) {
			finish();
			return;
		}

		ImageView ivHeader = (ImageView) findViewById(R.id.iv_header);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_placeholder_a)
				.displayer(new FadeInBitmapDisplayer(250)).cacheOnDisk(true)
				.postProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bitmap) {
						return Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), 106);
					}
				}).build();
		ImageLoader.getInstance().displayImage(card.getCardimageurl(),
				ivHeader, options);

		final TextView tvTotalEarned = (TextView) findViewById(R.id.textView2);
		new CardsRetrieveBalanceTask(this, card.getToken(), 0, null,
				new CardsRetrieveBalanceTask.BalanceListener() {
					@Override
					public void onBalanceLoaded(String balance) {
						tvTotalEarned.setText(balance);
					}
				}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		TextView tvCardName = (TextView) findViewById(R.id.tv_card_name);
		tvCardName.setText(card.getCardtype().toUpperCase());

		TextView tvCardNumber = (TextView) findViewById(R.id.tv_card_number);
		tvCardNumber.setText("**** **** **** " + card.getFourdigit());

		final RelativeLayout rlListContainer = (RelativeLayout) findViewById(R.id.rl_list_layout);
		final RelativeLayout rlNoCardLayout = (RelativeLayout) findViewById(R.id.rl_no_card_layout);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);

		final ListView lvTransactions = (ListView) findViewById(R.id.lv_transaction);
		this.mLastKey = null;

		try {
			new CardsRetrieveTransactionsTask(
					this,
					card.getToken(),
					mLastKey,
					new CardsRetrieveTransactionsTask.CardTransactionsListener() {
						@Override
						public void onTransactionsLoaded(
								PaginatedList<CardTransactionBean> transactions) {
							mLastKey = transactions.getLastKey();
							if (transactions.getList().size() == 0) {
								rlNoCardLayout.setVisibility(View.VISIBLE);
								rlListContainer.setVisibility(View.GONE);
								pb.setVisibility(View.GONE);
							} else {
								rlNoCardLayout.setVisibility(View.GONE);
								rlListContainer.setVisibility(View.VISIBLE);
								pb.setVisibility(View.GONE);
								TransactionAdapter adapter = new TransactionAdapter(
										CardsTransactionActivity.this,
										transactions);
								if (lvTransactions != null) {
									lvTransactions.setAdapter(adapter);
								}
							}
						}

						@Override
						public void onError() {

						}
					}).execute();

			mListener = new EndlessScrollListener(1,
					new EndlessScrollListener.EndlessCallback() {
						@Override
						public void OnLoadNeeded() {
							DebugUtility
									.showLog("Load more credit card transactions...");
							try {
								if (mLastKey != null) {
									new CardsRetrieveTransactionsTask(
											CardsTransactionActivity.this,
											card.getToken(),
											mLastKey,
											new CardsRetrieveTransactionsTask.CardTransactionsListener() {
												@Override
												public void onTransactionsLoaded(
														PaginatedList<CardTransactionBean> transactions) {
													mLastKey = transactions
															.getLastKey();
													if (lvTransactions != null) {
														if (lvTransactions
																.getAdapter() != null) {
															TransactionAdapter adapter = (TransactionAdapter) lvTransactions
																	.getAdapter();
															adapter.mTransactions
																	.getList()
																	.addAll(transactions
																			.getList());
															adapter.notifyDataSetChanged();
														} else {
															TransactionAdapter adapter = new TransactionAdapter(
																	CardsTransactionActivity.this,
																	transactions);
															lvTransactions
																	.setAdapter(adapter);
														}
													}
												}

												@Override
												public void onError() {

												}
											}).execute();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			lvTransactions.setOnScrollListener(mListener);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView tvDeleteCard = (TextView) findViewById(R.id.action_button_text);
		tvDeleteCard
				.setText(getString(R.string.cards_transaction_activity_empty_show));

		RelativeLayout rlTakeToEarn = (RelativeLayout) findViewById(R.id.action_button_content);
		rlTakeToEarn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});

		RelativeLayout rlDeleteCard = (RelativeLayout) findViewById(R.id.rl_delete_card);
		rlDeleteCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						CardsTransactionActivity.this);
				final View dialog = View.inflate(CardsTransactionActivity.this,
						R.layout.card_confirm_delete_dialog, null);

				builder.setView(dialog);

				final AlertDialog alertDialog = builder.show();

				Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
				btnYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new CardsDeleteCardTask(CardsTransactionActivity.this,
								card.getToken(),
								new CardsDeleteCardTask.DeleteCardsListener() {
									@Override
									public void onCardDeleted(String cardToken) {
										Toast.makeText(
												CardsTransactionActivity.this,
												"Card deleted successfully",
												Toast.LENGTH_LONG).show();
										alertDialog.dismiss();
										Intent intent = new Intent();
										setResult(
												ProfileCardsFragment.RESULT_MODIFIED_CARD,
												intent);
										finish();
									}
								}).execute();
					}
				});

				Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
				btnNo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						alertDialog.dismiss();
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

	private class TransactionAdapter extends BaseAdapter {

		private Context mContext;
		private PaginatedList<CardTransactionBean> mTransactions;
		private SimpleDateFormat mDateFormatter;

		public TransactionAdapter(Context context,
				PaginatedList<CardTransactionBean> list) {
			this.mContext = context;
			this.mTransactions = list;
			this.mDateFormatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		}

		@Override
		public int getCount() {
			return mTransactions.getList().size();
		}

		@Override
		public CardTransactionBean getItem(int i) {
			return mTransactions.getList().get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View convertView, ViewGroup viewGroup) {
			TransactionHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.cards_transaction_item, null);

				holder = new TransactionHolder();
				holder.tvBalanceDate = ((TextView) convertView
						.findViewById(R.id.textView4));
				holder.tvBalanceName = ((TextView) convertView
						.findViewById(R.id.textView));
				holder.tvBalanceDesc = ((TextView) convertView
						.findViewById(R.id.textView2));
				holder.tvBalanceAmount = ((TextView) convertView
						.findViewById(R.id.textView5));
				holder.ivBalanceIcon = ((ImageView) convertView
						.findViewById(R.id.imageView));
				holder.ivBeDollarsIcon = ((ImageView) convertView
						.findViewById(R.id.imageView2));
				holder.tvStatus = (TextView) convertView
						.findViewById(R.id.textView3);

				convertView.setTag(holder);
			}

			holder = (TransactionHolder) convertView.getTag();
			CardTransactionBean ct = getItem(i);

			holder.tvBalanceAmount.setText("+" + ct.getBedollars().intValue());
			holder.tvBalanceAmount.setTextColor(getResources().getColor(
					R.color.b_yellow_bedollars));
			holder.ivBeDollarsIcon.setImageResource(R.drawable.bed_small);

			holder.tvBalanceName.setText("Lorem ipsum dolor sit amet");
			holder.tvBalanceDesc.setText(ct.getBrandName() + " - "
					+ ct.getBrandAddress());

			if (ct.getStatus().equals("Accepted")) {
				holder.tvStatus.setText(ct.getStatus());
				holder.tvStatus.setTextColor(getResources().getColor(
						R.color.cards_assigned));
			} else if (ct.getStatus().equals("Returned")) {
				holder.tvStatus.setText(ct.getStatus());
				holder.tvStatus.setTextColor(getResources().getColor(
						R.color.cards_returned));
			} else if (ct.getStatus().equals("Pending")) {
				holder.tvStatus.setText(ct.getStatus());
				holder.tvStatus.setTextColor(getResources().getColor(
						R.color.cards_pending));
				holder.tvBalanceAmount.setTextColor(getResources().getColor(
						R.color.b_gray_texts));
				holder.ivBeDollarsIcon
						.setImageResource(R.drawable.bedollar_grey);
			}

			Calendar calendar = Calendar.getInstance();
			try {
				Date transactionDate = mDateFormatter.parse(ct.getDate()
						.toString());
				calendar.setTime(transactionDate);

				holder.tvBalanceDate.setText(calendar.getDisplayName(
						Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
						+ " "
						+ calendar.get(Calendar.DAY_OF_MONTH)
						+ ", "
						+ calendar.get(Calendar.YEAR));

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private class TransactionHolder {
		public TextView tvBalanceDate;
		public TextView tvBalanceName;
		public TextView tvBalanceDesc;
		public TextView tvBalanceAmount;
		public ImageView ivBalanceIcon;
		public ImageView ivBeDollarsIcon;
		public TextView tvStatus;
	}

}
