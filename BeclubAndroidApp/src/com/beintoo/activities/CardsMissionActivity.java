package com.beintoo.activities;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.IntentCompat;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.asynctasks.ProfileCardsTask;
import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.CardItemFragment;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.CirclePageIndicator;
import com.beintoo.utils.ui.SmartViewPager;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.MissionWrapper;
import com.beintoo.wrappers.PaginatedList;

@SuppressLint("NewApi")
public class CardsMissionActivity extends BeNotificationActivity {

	private SmartViewPager mViewPager;
	private CirclePageIndicator mPageIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards_mission_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		String brandID = "";
		if (getIntent().getExtras() != null) {
			brandID = getIntent().getExtras().getString("brand_id", "");
			setTitle(getIntent().getExtras().getString("brand_name", "BeClub"));
		}

		ArrayList<String> type = new ArrayList<String>();
		type.add("CARDSPRING");

		RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
		IMissionSponsorResource missionService = restAdapter
				.create(IMissionSponsorResource.class);

		missionService.getBrandMissions(MemberAuthStore.getAuth(this)
				.getToken(), LocationUtils.getLocationParams(this), null, null,
				type, brandID, null,
				new Callback<PaginatedList<MissionWrapper>>() {
					@Override
					public void success(PaginatedList<MissionWrapper> missions,
							Response response) {
						if (missions == null || missions.getList() == null) {
							finish();
							return;
						}
						MissionWrapper wrapper = missions.getList().get(0);
						fillView(wrapper);
					}

					@Override
					public void failure(RetrofitError error) {
						if (error.isNetworkError()) {
							Toast.makeText(CardsMissionActivity.this,
									getString(R.string.connection_error),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(CardsMissionActivity.this,
									getString(R.string.something_wrong),
									Toast.LENGTH_LONG).show();
						}
					}
				});

		mViewPager = (SmartViewPager) findViewById(R.id.vp_cards);

		final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPx = (widthPx * 450) / 730;
		mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(widthPx,
				heightPx));

		mPageIndicator = (CirclePageIndicator) findViewById(R.id.vp_cards_indicator);

		new ProfileCardsTask(this, new ProfileCardsTask.CardsListListener() {
			@Override
			public void onCardsListLoaded(
					PaginatedList<CardsInfoResponse> wrapper) {
				if (wrapper != null && wrapper.getList() != null) {
					if (wrapper.getList().size() > 0) {
						findViewById(R.id.rl_cards_pager).setVisibility(
								View.VISIBLE);
						findViewById(R.id.rl_cards_section_no).setVisibility(
								View.GONE);
						mViewPager.setAdapter(new CustomAdapter(
								getSupportFragmentManager(), wrapper.getList()));
						mPageIndicator.setViewPager(mViewPager);
						findViewById(R.id.tv_card_section_add_card)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Bundle bundle = new Bundle();
										bundle.putString("eventtype",
												"URL_SCHEME_LAUNCH");
										bundle.putString("path",
												"/userprofile/cards");
										Intent intent = new Intent(
												CardsMissionActivity.this,
												MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
										intent.putExtras(bundle);
										startActivity(intent);
									}
								});
					} else {
						findViewById(R.id.rl_cards_pager).setVisibility(
								View.GONE);
						findViewById(R.id.rl_cards_section_no).setVisibility(
								View.VISIBLE);
						findViewById(R.id.tv_card_section_add)
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Bundle bundle = new Bundle();
										bundle.putString("eventtype",
												"URL_SCHEME_LAUNCH");
										bundle.putString("path",
												"/userprofile/cards");
										Intent intent = new Intent(
												CardsMissionActivity.this,
												MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
										intent.putExtras(bundle);
										startActivity(intent);
									}
								});
					}
				}
			}

			@Override
			public void onError() {

			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void fillView(final MissionWrapper mission) {
		TextView tvHeadline1 = (TextView) findViewById(R.id.tv_headline1);
		TextView tvHeadline2 = (TextView) findViewById(R.id.tv_headline2);

		ImageView ivHeader = (ImageView) findViewById(R.id.itemImage);
		final int widthPhoto = getResources().getDisplayMetrics().widthPixels - 20;
		final int heightPhoto = (widthPhoto * 320) / 730;
		ivHeader.setLayoutParams(new RelativeLayout.LayoutParams(widthPhoto,
				heightPhoto));

		if (mission.getMission().getHeadline1() != null
				&& !mission.getMission().getHeadline1().equals("")) {
			tvHeadline1.setText(Html.fromHtml(mission.getMission()
					.getHeadline1()));
			tvHeadline2.setText(Html.fromHtml(mission.getMission()
					.getHeadline2()));

			ivHeader.setImageResource(R.drawable.cards_mission_header);

			findViewById(R.id.tv_qnt_bedollars).setVisibility(View.GONE);
		} else {
			tvHeadline1.setVisibility(View.GONE);
			tvHeadline2.setVisibility(View.GONE);

			ivHeader.setImageResource(R.drawable.cards_bedollars_header);

			TextView tvQntBeDollars = (TextView) findViewById(R.id.tv_qnt_bedollars);
			tvQntBeDollars.setText("+"
					+ mission.getMission().getBedollars().intValue()
					+ "\nBeDollars");
			tvQntBeDollars.setVisibility(View.VISIBLE);
		}

		TextView tvBeDollarsHeadline = (TextView) findViewById(R.id.tv_headline3);
		if (mission.getMission().getBedollarsHeadline1() != null) {
			tvBeDollarsHeadline.setText(Html.fromHtml(mission.getMission()
					.getBedollarsHeadline1()));
		} else {
			tvBeDollarsHeadline.setVisibility(View.GONE);
			findViewById(R.id.rl_warning_partner).setVisibility(View.GONE);
		}

		TextView tvSchedule = (TextView) findViewById(R.id.tv_schedule);
		if (mission.getMission().getSchedule() != null) {
			tvSchedule.setText(Html
					.fromHtml(mission.getMission().getSchedule()));
		} else {
			tvSchedule.setVisibility(View.GONE);
			findViewById(R.id.rl_discount_time).setVisibility(View.GONE);
		}

		TextView tvDisclaimer = (TextView) findViewById(R.id.tv_disclaimer);
		tvDisclaimer.setText(Html
				.fromHtml(mission.getMission().getDisclaimer()));

		findViewById(R.id.storesButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(CardsMissionActivity.this,
								MapStoresActivity.class);
						intent.putExtra("brand", mission.getMission()
								.getBrandsid());
						intent.putExtra("brandName", getIntent().getExtras()
								.getString("brand_name", "BeClub"));
						startActivity(intent);

					}
				});

		findViewById(R.id.rl_root_container).setVisibility(View.VISIBLE);
		findViewById(R.id.progressBar).setVisibility(View.GONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private class CustomAdapter extends FragmentPagerAdapter {

		private List<CardsInfoResponse> cardsContainer;

		public CustomAdapter(FragmentManager fm, List<CardsInfoResponse> cards) {
			super(fm);
			this.cardsContainer = cards;
		}

		@Override
		public Fragment getItem(int position) {
			return new CardItemFragment(cardsContainer.get(position));
		}

		@Override
		public int getCount() {
			return cardsContainer.size();
		}
	}

}
