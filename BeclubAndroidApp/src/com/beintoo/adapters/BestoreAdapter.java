package com.beintoo.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.activities.RewardActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.RewardWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class BestoreAdapter extends ArrayAdapter<RewardWrapper> {

	private Activity mActivity;
	public List<RewardWrapper> objectList;
	private MemberBean me;

	public BestoreAdapter(Activity context, int textViewResourceId,
			List<RewardWrapper> objects) {
		super(context, textViewResourceId, objects);
		objectList = objects;
		mActivity = context;
		me = MemberAuthStore.getMember(mActivity);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public RewardWrapper getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		BeStoreHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mActivity, R.layout.bestore_item_row,
					null);

			holder = new BeStoreHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.itemName);
			holder.tvPrice = (TextView) convertView
					.findViewById(R.id.itemPrice);
			holder.ivBrand = (ImageView) convertView
					.findViewById(R.id.itemImage);
			holder.ivBeDollars = (ImageView) convertView
					.findViewById(R.id.bedollars_logo);

			convertView.setTag(holder);
		}

		holder = (BeStoreHolder) convertView.getTag();

		final RewardWrapper reward = objectList.get(position);

		String image = reward.getImagebig();
		String name = reward.getName();
		Double bedollars = reward.getBedollars();

		final int widthPx = getContext().getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPx = (widthPx * 320) / 730;
		holder.ivBrand.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
				heightPx));

		ImageViewAware iv = new ImageViewAware(holder.ivBrand, false);

		if (image != null) {
			if (!image.equals(holder.imgUrl)) {
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.ic_image_placeholder_a)
						.displayer(new FadeInBitmapDisplayer(250))
						.cacheOnDisk(true).preProcessor(new BitmapProcessor() {
							@Override
							public Bitmap process(Bitmap bitmap) {
								Bitmap temp = BeUtils.getRoundedCornerBitmap(
										mActivity, bitmap, 3,
										bitmap.getWidth(), bitmap.getHeight(),
										false, false, true, true);
								return Bitmap.createScaledBitmap(temp, widthPx,
										heightPx, true);
							}
						}).build();

				ImageLoader.getInstance().displayImage(image, iv, options);
			} else {
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.cacheOnDisk(true).preProcessor(new BitmapProcessor() {
							@Override
							public Bitmap process(Bitmap bitmap) {
								Bitmap temp = BeUtils.getRoundedCornerBitmap(
										mActivity, bitmap, 3,
										bitmap.getWidth(), bitmap.getHeight(),
										false, false, true, true);
								return Bitmap.createScaledBitmap(temp, widthPx,
										heightPx, true);
							}
						}).build();

				ImageLoader.getInstance().displayImage(image, iv, options);
			}
		}
		holder.imgUrl = image;

		if (name != null)
			holder.tvName.setText(name);

		if (bedollars != null) {
			holder.tvPrice.setText(mActivity.getResources().getString(
					R.string.value_no_bedollars, bedollars.intValue()));
		}

		if (me.getBedollars() < bedollars) {
			holder.ivBeDollars.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.bedollar_grey));
			holder.tvPrice.setTextColor(mActivity.getResources().getColor(
					R.color.b_gray_texts));
		} else {
			holder.ivBeDollars.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.bed_small));
			holder.tvPrice.setTextColor(mActivity.getResources().getColor(
					R.color.b_yellow_bedollars));
		}

		convertView.findViewById(R.id.bestoreItem).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(mActivity,
								RewardActivity.class);
						intent.putExtra("reward", new Gson().toJson(reward));
						mActivity.startActivityForResult(intent,
								RewardActivity.RESULT_DISCOVER_HOW);

					}
				});

		return convertView;
	}

	private class BeStoreHolder {
		public ImageView ivBrand;
		public TextView tvName;
		public TextView tvPrice;
		public ImageView ivBeDollars;
		public String imgUrl;
	}
}
