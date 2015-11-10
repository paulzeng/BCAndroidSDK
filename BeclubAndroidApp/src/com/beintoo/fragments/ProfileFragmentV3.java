package com.beintoo.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MemberBean;

public class ProfileFragmentV3 extends Fragment implements
		ViewPager.OnPageChangeListener {

	private ViewPager mViewPager;
	private ProfilePagerAdapter mAdapter;

	private LinearLayout mWalletIndicator;
	private LinearLayout mBalanceIndicator;
	private LinearLayout mCardsIndicator;

	private RelativeLayout mRlWallet;
	private RelativeLayout mRlBalance;
	private RelativeLayout mRlCards;

	private final String mScreenName = "PROFILE";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile_fragment_v3, container,
				false);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mAdapter = new ProfilePagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(2);

		mWalletIndicator = (LinearLayout) view
				.findViewById(R.id.ll_wallet_indicator);
		mBalanceIndicator = (LinearLayout) view
				.findViewById(R.id.ll_balance_indicator);
		mCardsIndicator = (LinearLayout) view
				.findViewById(R.id.ll_cards_indicator);

		mRlWallet = (RelativeLayout) view.findViewById(R.id.rl_wallet);
		mRlBalance = (RelativeLayout) view.findViewById(R.id.rl_balance);
		mRlCards = (RelativeLayout) view.findViewById(R.id.rl_cards);

		mRlWallet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewPager.setCurrentItem(0, true);
			}
		});

		mRlBalance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewPager.setCurrentItem(1, true);
			}
		});

		mRlCards.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mViewPager.setCurrentItem(2, true);
			}
		});

		MemberBean me = MemberAuthStore.getMember(getActivity());
		((TextView) view.findViewById(R.id.header_bedollars_amount)).setText(""
				+ me.getBedollars().intValue());

		/*
		 * [13 October 2014] - ENABLE CARDS SECTION ONLY FOR MEMBER THAT
		 * REGISTERED A CARD THROUGH CC UNIT.
		 */
		if (me.getHascctokens() != null && me.getHascctokens()) {
			view.findViewById(R.id.rl_cards).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.rl_cards).setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ProfileCardsFragment.REQUEST_MODIFY_CARD) {
			if (mViewPager != null) {
				mAdapter = new ProfilePagerAdapter(getChildFragmentManager());
				mViewPager.setAdapter(mAdapter);
				mViewPager.setCurrentItem(2);
			}
		} else if (requestCode == ProfileWalletFragment.ARCHIVE_CODE) {
			if (mViewPager != null && mAdapter != null) {
				mAdapter.getItem(mViewPager.getCurrentItem()).onActivityResult(
						requestCode, resultCode, data);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			if (args.getBoolean("showBalance", false)) {
				if (mViewPager != null) {
					mViewPager.setCurrentItem(1);
				}
			} else if (args.getBoolean("showCards", false)) {
				if (mViewPager != null) {
					mViewPager.setCurrentItem(2);
				}
			}
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			mWalletIndicator.setVisibility(View.VISIBLE);
			mBalanceIndicator.setVisibility(View.INVISIBLE);
			mCardsIndicator.setVisibility(View.INVISIBLE);
			break;
		case 1:
			mWalletIndicator.setVisibility(View.INVISIBLE);
			mBalanceIndicator.setVisibility(View.VISIBLE);
			mCardsIndicator.setVisibility(View.INVISIBLE);
			break;
		case 2:
			mWalletIndicator.setVisibility(View.INVISIBLE);
			mBalanceIndicator.setVisibility(View.INVISIBLE);
			mCardsIndicator.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	class ProfilePagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragments;

		public ProfilePagerAdapter(FragmentManager fm) {
			super(fm);
			fragments = new ArrayList<Fragment>();
			fragments.add(0, new ProfileWalletFragment());
			fragments.add(1, new ProfileBalanceFragment());

			if (MemberAuthStore.getMember(getActivity()).getHascctokens() != null
					&& MemberAuthStore.getMember(getActivity())
							.getHascctokens()) {
				fragments.add(2, new ProfileCardsFragment());
			}
		}

		@Override
		public Fragment getItem(int position) {
			if (fragments.get(position) != null) {
				return fragments.get(position);
			} else {
				if (position == 0) {
					fragments.add(0, new ProfileWalletFragment());
				} else if (position == 1) {
					fragments.add(1, new ProfileBalanceFragment());
				} else if (position == 2) {
					fragments.add(2, new ProfileCardsFragment());
				}
				return fragments.get(position);
			}
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
}
