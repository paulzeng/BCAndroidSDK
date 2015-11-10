package com.beintoo.adapters;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.asynctasks.CardsDeleteCardTask;
import com.beintoo.asynctasks.CardsRetrieveBalanceTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;
import com.beintoo.wrappers.CardsInfoResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ProfileCardsAdapter extends BaseAdapter {

	private Context mContext;
	private List<CardsInfoResponse> mCards;

	public ProfileCardsAdapter(Context context, List<CardsInfoResponse> cards) {
		this.mContext = context;
		this.mCards = cards;
	}

	@Override
	public int getCount() {
		return mCards.size();
	}

	@Override
	public CardsInfoResponse getItem(int i) {
		return this.mCards.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		CardHolder holder;
		if (view == null) {
			view = View.inflate(mContext, R.layout.cards_item, null);
			holder = new CardHolder();
			holder.tvTotalEarned = (TextView) view.findViewById(R.id.itemPrice);
			holder.tvLast4 = (TextView) view.findViewById(R.id.itemName);
			holder.ivCard = (ImageView) view.findViewById(R.id.itemImage);
			holder.ivDelete = (ImageView) view.findViewById(R.id.imageView);
			holder.position = i;
			view.setTag(holder);
		}

		holder = (CardHolder) view.getTag();
		holder.position = i;

		final CardsInfoResponse card = getItem(i);

		holder.tvLast4.setText("**** **** **** " + card.getFourdigit());

		new CardsRetrieveBalanceTask(mContext, card.getToken(), i, holder, null)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		final int widthPx = mContext.getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPx = (widthPx * 320) / 730;
		holder.ivCard.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
				heightPx));
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_image_placeholder_a)
				.cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(250))
				.postProcessor(new BitmapProcessor() {
					@Override
					public Bitmap process(Bitmap bitmap) {
						return BeUtils.getRoundedCornerBitmap(mContext, bitmap,
								3, bitmap.getWidth(), bitmap.getHeight(),
								false, false, true, true);
					}
				}).build();
		ImageLoader.getInstance().displayImage(card.getCardimageurl(),
				holder.ivCard, options);

		holder.ivDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				final View dialog = View.inflate(mContext,
						R.layout.card_confirm_delete_dialog, null);

				builder.setView(dialog);

				final AlertDialog alertDialog = builder.show();

				Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
				btnYes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						new CardsDeleteCardTask(mContext, card.getToken(),
								new CardsDeleteCardTask.DeleteCardsListener() {
									@Override
									public void onCardDeleted(String cardToken) {
										Toast.makeText(mContext,
												"Card deleted successfully",
												Toast.LENGTH_LONG).show();
										alertDialog.dismiss();
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

		return view;
	}

	public class CardHolder {
		public int position;
		public TextView tvTotalEarned;
		public TextView tvLast4;
		public ImageView ivCard;
		public ImageView ivDelete;
	}
}
