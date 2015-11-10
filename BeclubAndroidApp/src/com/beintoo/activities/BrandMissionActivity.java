package com.beintoo.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.beintoo.beclubapp.R;
import com.beintoo.fragments.BrandInStoreFragment;
import com.beintoo.fragments.BrandOnlineFragment;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.FraudManager;
import com.beintoo.wrappers.EarnBedollarsWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class BrandMissionActivity extends BeNotificationActivity implements
		ViewPager.OnPageChangeListener {

	private EarnBedollarsWrapper mMission = null;

	private LinearLayout mInStoreIndicator;
	private LinearLayout mOnlineIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brand_mission_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString("missions") != null) {
			mMission = new Gson().fromJson(extras.getString("missions"),
					EarnBedollarsWrapper.class);
			setTitle(mMission.getBrands().getName());
		}

		applyFixforWalkinTimeType(mMission);

		final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		LinearLayout llTabs = (LinearLayout) findViewById(R.id.tabs);
		mInStoreIndicator = (LinearLayout) findViewById(R.id.ll_instore_indicator);
		mOnlineIndicator = (LinearLayout) findViewById(R.id.ll_online_indicator);
		RelativeLayout mRlInStore = (RelativeLayout) findViewById(R.id.rl_instore);
		RelativeLayout mRlOnline = (RelativeLayout) findViewById(R.id.rl_online);

		mRlInStore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewPager.setCurrentItem(0, true);
			}
		});

		mRlOnline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewPager.setCurrentItem(1, true);
			}
		});

		ImageView ivBrand = (ImageView) findViewById(R.id.iv_brand_logo);

		final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPx = (widthPx * 320) / 730;
		ivBrand.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
				heightPx));

		if (mMission.getBrands().getImagebig() != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_image_placeholder_a)
					.cacheOnDisk(true)
					.displayer(new FadeInBitmapDisplayer(250))
					.preProcessor(new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							Bitmap temp = BeUtils.getRoundedCornerBitmap(
									BrandMissionActivity.this, bitmap, 3,
									bitmap.getWidth(), bitmap.getHeight(),
									false, false, false, false);
							return Bitmap.createScaledBitmap(temp, widthPx,
									heightPx, true);
						}
					}).build();

			ImageLoader.getInstance().displayImage(
					mMission.getBrands().getImagebig(), ivBrand, options);
		}

		BrandPagerOptions options = new BrandPagerOptions(true, false, 1);
		llTabs.setVisibility(View.GONE);

		List<EarnBedollarsWrapper.MissionActionTypeEnum> enabledMissions = mMission
				.getSubtype();
		if (enabledMissions != null
				&& enabledMissions.size() > 1
				&& enabledMissions
						.contains(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE)) {
			llTabs.setVisibility(View.VISIBLE);
			options = new BrandPagerOptions(true, true, 2);
		} else if (enabledMissions != null
				&& enabledMissions.size() == 1
				&& enabledMissions
						.contains(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE)) {
			llTabs.setVisibility(View.GONE);
			options = new BrandPagerOptions(false, true, 1);
		}

		BrandPagerAdapter mAdapter = new BrandPagerAdapter(
				getSupportFragmentManager(), mMission, options);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);

		if (enabledMissions != null
				&& enabledMissions
						.contains(EarnBedollarsWrapper.MissionActionTypeEnum.ONLINE)) {
			mViewPager.setCurrentItem(1, true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == FraudManager.BANNED_ACTIVITY_RESULT) {
			setResult(FraudManager.BANNED_ACTIVITY_RESULT);
			finish();
		}
	}

	@Override
	public void onPageScrolled(int i, float v, int i2) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			mInStoreIndicator.setVisibility(View.VISIBLE);
			mOnlineIndicator.setVisibility(View.INVISIBLE);
			break;
		case 1:
			mInStoreIndicator.setVisibility(View.INVISIBLE);
			mOnlineIndicator.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void applyFixforWalkinTimeType(EarnBedollarsWrapper object) {
		if (object.getSubtype().contains(
				EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME)) {
			if (object.getSubtype().contains(
					EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN)) {
				int index = object.getSubtype().indexOf(
						EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
				object.getSubtype().remove(index);
			} else {
				int index = object.getSubtype().indexOf(
						EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
				object.getSubtype().set(index,
						EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN);
			}
		}

		if (object.getBrands().getSponsoredactiontype() == null)
			return;

		if (object
				.getBrands()
				.getSponsoredactiontype()
				.contains(
						EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME)) {
			if (object
					.getBrands()
					.getSponsoredactiontype()
					.contains(EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN)) {
				int index = object
						.getBrands()
						.getSponsoredactiontype()
						.indexOf(
								EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
				object.getBrands().getSponsoredactiontype().remove(index);
			} else {
				int index = object
						.getBrands()
						.getSponsoredactiontype()
						.indexOf(
								EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN_TIME);
				object.getBrands()
						.getSponsoredactiontype()
						.set(index,
								EarnBedollarsWrapper.MissionActionTypeEnum.WALKIN);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}

	public class BrandPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragments;
		private BrandPagerOptions mOptions;

		public BrandPagerAdapter(FragmentManager fm,
				EarnBedollarsWrapper mission, BrandPagerOptions options) {
			super(fm);
			this.mOptions = options;
			fragments = new ArrayList<Fragment>();

			if (mOptions.enableInStoreFrag) {
				Fragment f = BrandInStoreFragment.newInstance(mission);
				f.setRetainInstance(true);
				fragments.add(f);
			}
			if (mOptions.enableOnlineFrag) {
				Fragment f = BrandOnlineFragment.newInstance(mission);
				f.setRetainInstance(true);
				fragments.add(f);
			}
		}

		@Override
		public Fragment getItem(int i) {
			return fragments.get(i);
		}

		@Override
		public int getCount() {
			return mOptions.count;
		}
	}

	public class BrandPagerOptions {
		private boolean enableInStoreFrag;
		private boolean enableOnlineFrag;
		private int count;

		public BrandPagerOptions(boolean enableInStoreFrag,
				boolean enableOnlineFrag, int count) {
			this.enableInStoreFrag = enableInStoreFrag;
			this.enableOnlineFrag = enableOnlineFrag;
			this.count = count;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
