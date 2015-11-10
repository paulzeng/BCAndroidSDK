package com.beintoo.adapters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.activities.RewardActivity;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.ProfileWalletFragment;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.RewardWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ProfileWalletAdapter extends ArrayAdapter<BuyWrapper> {

	private Activity mActivity;
	public List<BuyWrapper> objectList;
	private View mView;
	private MemberBean me;
	private ProfileWalletFragment mFragment;
	private RewardWrapper.RewardTypes mRewardsType;

	private SimpleDateFormat mDateFormatter;

	public ProfileWalletAdapter(Activity activity, View view,
			RewardWrapper.RewardTypes type, int textViewResourceId,
			List<BuyWrapper> objects, ProfileWalletFragment f) {
		super(activity, textViewResourceId, objects);
		objectList = objects;
		mActivity = activity;
		me = MemberAuthStore.getMember(activity);
		mView = view;
		mRewardsType = type;
		mFragment = f;

		mDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
				Locale.ENGLISH);
		mDateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public BuyWrapper getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.profile_wallet_item_row, parent, false);

			WalletHolder holder = new WalletHolder();
			holder.ivBrand = (ImageView) convertView
					.findViewById(R.id.itemImage);
			holder.ivArchive = (ImageView) convertView
					.findViewById(R.id.archiveItem);
			holder.tvName = (TextView) convertView.findViewById(R.id.itemName);
			holder.rlBestore = (RelativeLayout) convertView
					.findViewById(R.id.bestoreItem);
			convertView.setTag(holder);
		}

		WalletHolder holder = (WalletHolder) convertView.getTag();

		final RewardWrapper reward = getItem(position).getReward();

		try {
			holder.tvName.setText(reward.getName());

			String image = reward.getImagebig();
			ImageViewAware iv = new ImageViewAware(holder.ivBrand, false);

			if (image != null) {
				if (!image.equals(holder.imgUrl)) {
					final int widthPx = getContext().getResources()
							.getDisplayMetrics().widthPixels - 20;
					final int heightPx = (widthPx * 320) / 730;
					holder.ivBrand
							.setLayoutParams(new RelativeLayout.LayoutParams(
									widthPx, heightPx));

					DisplayImageOptions options = new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.ic_image_placeholder_a)
							.displayer(new FadeInBitmapDisplayer(250))
							.cacheOnDisk(true)
							.postProcessor(new BitmapProcessor() {
								@Override
								public Bitmap process(Bitmap bitmap) {
									return BeUtils.getRoundedCornerBitmap(
											mActivity, bitmap, 3,
											bitmap.getWidth(),
											bitmap.getHeight(), false, false,
											true, true);
								}
							}).build();
					ImageLoader.getInstance().displayImage(image, iv, options);
				} else {
					DisplayImageOptions options = new DisplayImageOptions.Builder()
							.cacheOnDisk(true)
							.postProcessor(new BitmapProcessor() {
								@Override
								public Bitmap process(Bitmap bitmap) {
									return BeUtils.getRoundedCornerBitmap(
											mActivity, bitmap, 3,
											bitmap.getWidth(),
											bitmap.getHeight(), false, false,
											true, true);
								}
							}).build();
					ImageLoader.getInstance().displayImage(image, iv, options);
				}
			}
			holder.imgUrl = image;

			if (mRewardsType == RewardWrapper.RewardTypes.REGULAR) {
				holder.ivArchive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								mActivity);
						builder.setMessage(mActivity
								.getString(R.string.profile_wallet_archive_dialog));
						builder.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										final BuyWrapper buyWrapper = getItem(position);
										final ProgressDialog progressDialog = ProgressDialog
												.show(mActivity, "Archive",
														"Please wait...", true,
														false);
										RestAdapter restAdapter = BeRestAdapter
												.getMemberTokenRestAdapter();
										IMemberResource service = restAdapter
												.create(IMemberResource.class);

										service.archiveReward(
												MemberAuthStore.getAuth(
														mActivity).getToken(),
												MemberAuthStore.getMember(
														mActivity).getId(),
												buyWrapper
														.getMembersRewardsId(),
												"archive",
												new Callback<Object>() {
													@Override
													public void success(
															Object o,
															Response response) {
														Toast.makeText(
																mActivity,
																mActivity
																		.getString(R.string.bestore_archived_message),
																Toast.LENGTH_LONG)
																.show();
														progressDialog
																.dismiss();
														objectList
																.remove(position);
														notifyDataSetChanged();
													}

													@Override
													public void failure(
															RetrofitError error) {
														if (error
																.isNetworkError()) {
															Toast.makeText(
																	mActivity,
																	mActivity
																			.getString(R.string.connection_error),
																	Toast.LENGTH_LONG)
																	.show();
														} else {
															Toast.makeText(
																	mActivity,
																	mActivity
																			.getString(R.string.something_wrong),
																	Toast.LENGTH_LONG)
																	.show();

														}
														progressDialog
																.dismiss();
													}
												});
									}
								});
						builder.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User cancelled the dialog
									}
								});

						builder.create().show();
					}
				});
			} else {
				holder.ivArchive.setVisibility(View.GONE);
			}

			holder.rlBestore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(mActivity, RewardActivity.class);
					intent.putExtra("converted_reward",
							new Gson().toJson(getItem(position)));
					intent.putExtra("wallet_position", position); // remove the
																	// header
																	// from
																	// position
					if (mRewardsType == RewardWrapper.RewardTypes.ARCHIVED) {
						intent.putExtra("is_archived", true);
					}
					mFragment.startActivityForResult(intent,
							ProfileWalletFragment.ARCHIVE_CODE);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	public void removeItemAtPosition(int position) {
		objectList.remove(position);
		notifyDataSetChanged();
	}

	private class WalletHolder {
		public ImageView ivBrand;
		public TextView tvName;
		public ImageView ivArchive;
		public RelativeLayout rlBestore;
		public String imgUrl;
	}
}
