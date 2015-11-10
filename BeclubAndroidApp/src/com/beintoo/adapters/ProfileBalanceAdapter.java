package com.beintoo.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.beintoo.wrappers.MembersBedollarsWrapper;
import com.beintoo.wrappers.MissionDataWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileBalanceAdapter extends
		ArrayAdapter<MembersBedollarsWrapper> {

	private Context mContext;
	public List<MembersBedollarsWrapper> objectsList;
	SimpleDateFormat mDateFormatter;

	public ProfileBalanceAdapter(Context context, int textViewResourceId,
			List<MembersBedollarsWrapper> balanceList) {
		super(context, textViewResourceId, balanceList);
		mContext = context;
		objectsList = balanceList;

		mDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
				Locale.ENGLISH);
		mDateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public MembersBedollarsWrapper getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BalanceHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.balance_item, null);

			holder = new BalanceHolder();
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

		holder = (BalanceHolder) convertView.getTag();

		holder.tvStatus.setVisibility(View.GONE);
		((RelativeLayout.LayoutParams) holder.tvBalanceName.getLayoutParams())
				.addRule(RelativeLayout.CENTER_VERTICAL, 0);
		convertView.setOnClickListener(null);

		MembersBedollarsWrapper item = getItem(position);
		try {
			Date msgDate = mDateFormatter.parse(item.getDate());
			mDateFormatter.setTimeZone(TimeZone.getDefault());

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(msgDate);

			holder.tvBalanceDate.setText(calendar.getDisplayName(
					Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
					+ " "
					+ calendar.get(Calendar.DAY_OF_MONTH)
					+ ", "
					+ calendar.get(Calendar.YEAR));
			holder.tvBalanceDate.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (item.getReward() != null) { // it's a reward
			holder.tvBalanceName.setText(mContext
					.getString(R.string.profile_reward_title));
			if (item.getReward().getReward().getName() != null)
				holder.tvBalanceDesc.setText(item.getReward().getReward()
						.getName());
			else
				holder.tvBalanceDesc.setVisibility(View.GONE);
			holder.ivBalanceIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.carrello));
		} else if (item.getMission() != null
				&& item.getMission().getMission() != null
				&& !item.getReason().equals(
						EarnBedollarsWrapper.MissionActionTypeEnum.BEPOWERED
								.toString())) { // it's a mission
			if (item.getMission().getMission().getType() == MissionDataWrapper.MissionType.SPONSORED) { // check
																										// if
																										// is
																										// an
																										// in
																										// app
																										// mission
																										// or
																										// not
				if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.SCAN) {
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_scanned_title));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.scan_mission_1));
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE) {
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_online_title));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.web_icon));
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN) {
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_walkin_title));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.walk_mission_2));
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.MULTIPLESCAN) {
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_multiplescan));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.scan_mission_2));
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.CARDSPRING) {
					holder.tvBalanceName
							.setText(mContext
									.getString(R.string.profile_balance_cardspring_title));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.card_green));
					holder.tvStatus.setVisibility(View.VISIBLE);
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.TAKEAPICTURE) {
					holder.tvBalanceName
							.setText(mContext
									.getString(R.string.profile_balance_takeapicture_title));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.ic_picture_camera_green));
				}

				if (item.getMission().getMission().getName() != null)
					holder.tvBalanceDesc.setText(item.getMission().getMission()
							.getName());
				else
					holder.tvBalanceDesc.setVisibility(View.GONE);
			} else if (item.getMission().getMission().getType() == MissionDataWrapper.MissionType.INAPP) {
				if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.REWARDED) { // THIS
																								// IS
																								// COMING
																								// FROM
																								// AN
																								// APP
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_rewarded));
					((RelativeLayout.LayoutParams) holder.tvBalanceName
							.getLayoutParams())
							.addRule(RelativeLayout.CENTER_VERTICAL);
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.givebedollars));
					holder.tvBalanceDesc.setText("");
				} else if (item.getMission().getMission().getActioncheck()
						.getSubtype() == EarnBedollarsWrapper.MissionActionTypeEnum.BRANDED) { // THIS
																								// IS
																								// COMING
																								// FROM
																								// AN
																								// APP
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_rewarded));
					((RelativeLayout.LayoutParams) holder.tvBalanceName
							.getLayoutParams())
							.addRule(RelativeLayout.CENTER_VERTICAL);
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(
									R.drawable.givebedollars));
					holder.tvBalanceDesc.setText("");
				} else {
					holder.tvBalanceName.setText(mContext
							.getString(R.string.profile_balance_generic));
					holder.ivBalanceIcon.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.generic));
					holder.tvBalanceDesc.setText("");
				}
			}
		} else {
			if (item.getReason().equals(
					EarnBedollarsWrapper.MissionActionTypeEnum.ENTRYBONUS
							.toString())) {
				holder.tvBalanceName.setText(mContext
						.getString(R.string.profile_balance_registered));
				((RelativeLayout.LayoutParams) holder.tvBalanceName
						.getLayoutParams())
						.addRule(RelativeLayout.CENTER_VERTICAL);
				holder.ivBalanceIcon.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.user));
				holder.tvBalanceDesc.setText("");
			} else if (item.getReason().equals(
					EarnBedollarsWrapper.MissionActionTypeEnum.PREREGISTRATION
							.toString())) { // THIS IS COMING FROM AN APP
				holder.tvBalanceName.setText(mContext
						.getString(R.string.profile_balance_prereg));
				((RelativeLayout.LayoutParams) holder.tvBalanceName
						.getLayoutParams())
						.addRule(RelativeLayout.CENTER_VERTICAL);
				holder.ivBalanceIcon.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.givebedollars));
				holder.tvBalanceDesc.setText("");
			} else if (item.getReason().equals(
					EarnBedollarsWrapper.MissionActionTypeEnum.BEPOWERED
							.toString())) {
				holder.tvBalanceName.setText(item.getApp().getBepoweredinfo()
						.getTitle());
				ImageLoader.getInstance().displayImage(
						item.getApp().getBepoweredinfo().getImgurlbig(),
						holder.ivBalanceIcon);
				holder.tvBalanceDesc.setText(item.getApp().getBepoweredinfo()
						.getDescription());

				final String appName = item.getApp().getName();
				final String uri = item.getApp().getBepoweredinfo()
						.getUrischemaandroid();

				convertView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(
								mContext);
						builder.setCancelable(false);
						builder.setTitle("BePowered");
						builder.setPositiveButton("Open App",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										try {
											mContext.getPackageManager()
													.getPackageInfo(
															"com.facebook.katana",
															0);
											mContext.startActivity(new Intent(
													Intent.ACTION_VIEW, Uri
															.parse(uri)));
										} catch (Exception e) {
											e.printStackTrace();
										}
										dialogInterface.dismiss();
									}
								});

						builder.setNegativeButton("Close",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										dialogInterface.dismiss();
									}
								});

						builder.setMessage("Are you sure you want to open "
								+ appName);
						builder.show();
					}
				});
			}
		}

		if (item.getValue() > 0) {
			holder.tvBalanceAmount.setText("+" + item.getValue().intValue());
			holder.tvBalanceAmount.setTextColor(mContext.getResources()
					.getColor(R.color.b_yellow_bedollars));
			holder.ivBeDollarsIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.bed_small));
		} else {
			holder.tvBalanceAmount.setText("" + item.getValue().intValue());
		}

		if (item.getValue() < 0) {
			holder.tvBalanceAmount.setTextColor(mContext.getResources()
					.getColor(R.color.b_gray_texts));
			holder.ivBeDollarsIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.bedollar_grey));
		}

		return convertView;
		// return drawMissions(convertView, parent, position);
	}

	private class BalanceHolder {
		public TextView tvBalanceDate;
		public TextView tvBalanceName;
		public TextView tvBalanceDesc;
		public TextView tvBalanceAmount;
		public ImageView ivBalanceIcon;
		public ImageView ivBeDollarsIcon;
		public TextView tvStatus;
	}
}
