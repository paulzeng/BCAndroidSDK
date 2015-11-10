package com.beintoo.activities;

import java.text.DecimalFormat;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMissionSponsorResource;
import com.beintoo.beclubapp.R;
import com.beintoo.dialogs.EmailNotVerifiedDialog;
import com.beintoo.utils.BeUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.OnlineProductBean;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

@SuppressLint("NewApi")
public class OnlineMissionActivity extends BeNotificationActivity {

	private String mBrandID, mBrandName, mMissionID;
	private static OnlineProductBean mProduct;
	private static boolean mShowingBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.online_product_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);

		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.getString("brand_id") != null
				&& extras.getString("brand_name") != null
				&& extras.getString("mission_id") != null) {
			mBrandID = extras.getString("brand_id");
			mBrandName = extras.getString("brand_name");
			mMissionID = extras.getString("mission_id");
			setTitle(mBrandName);
		}

		if (extras != null && extras.getString("product") != null) {
			mProduct = new Gson().fromJson(extras.getString("product"),
					OnlineProductBean.class);
		}

		Fragment fragment = new CardFrontFragment();
		getFragmentManager().beginTransaction().add(R.id.container, fragment)
				.commit();
		mShowingBack = false;

		TextView tvProductName = (TextView) findViewById(R.id.tv_product_name);
		tvProductName.setText(mProduct.getName());

		TextView tvBrandName = (TextView) findViewById(R.id.tv_brand_name);
		tvBrandName.setText(mBrandName);

		DecimalFormat myFormatter = new DecimalFormat(".00");

		TextView tvCost = (TextView) findViewById(R.id.tv_cost);
		tvCost.setText("$" + myFormatter.format(mProduct.getCost()));

		TextView tvBedollars = (TextView) findViewById(R.id.tv_qnt_bedollars);
		tvBedollars.setText("" + mProduct.getBedollars().intValue());

		TextView tvAction = (TextView) findViewById(R.id.action_button_text);
		tvAction.setText(getString(R.string.online_send_link_my_address));

		final RelativeLayout llActionButton = (RelativeLayout) findViewById(R.id.action_button_content);
		llActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (MemberAuthStore.getMember(OnlineMissionActivity.this)
						.isEmailverified()) {

					RestAdapter restAdapter = BeRestAdapter
							.getMemberTokenRestAdapter();
					IMissionSponsorResource missionService = restAdapter
							.create(IMissionSponsorResource.class);

					final ProgressDialog dialog = ProgressDialog.show(
							OnlineMissionActivity.this, "Sending",
							"Please wait...", true, false);
					missionService.sendOnlineMissionEmail(MemberAuthStore
							.getAuth(OnlineMissionActivity.this).getToken(),
							mMissionID, mProduct, new Callback<Object>() {
								@Override
								public void success(Object o, Response response) {
									dialog.dismiss();
									Toast.makeText(
											OnlineMissionActivity.this,
											getString(R.string.online_check_your_inbox),
											Toast.LENGTH_LONG).show();
								}

								@Override
								public void failure(RetrofitError error) {
									dialog.dismiss();
									if (error.isNetworkError()) {
										Toast.makeText(
												OnlineMissionActivity.this,
												getString(R.string.connection_error),
												Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(
												OnlineMissionActivity.this,
												getString(R.string.something_wrong),
												Toast.LENGTH_LONG).show();
									}
								}
							});

					llActionButton.setBackgroundResource(R.color.b_gray_button);
					llActionButton.setOnClickListener(null);
				} else {
					new EmailNotVerifiedDialog(OnlineMissionActivity.this)
							.show(getSupportFragmentManager(),
									"email-not-verified");
				}
			}
		});

		findViewById(R.id.rl_all_participant_store).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						String url = mProduct.getUrl();

						if (!url.startsWith("http://")
								&& !url.startsWith("https://"))
							url = "http://" + url;

						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}
				});

	}

	private static void flipCard(FragmentManager manager) {
		if (mShowingBack) {
			manager.popBackStack();
			return;
		}

		// Flip to the back.

		mShowingBack = true;

		// Create and commit a new fragment transaction that adds the fragment
		// for the back of
		// the card, uses custom animations, and is part of the fragment
		// manager's back stack.

		Fragment fragment = new CardBackFragment();

		manager.beginTransaction()

		// Replace the default fragment animations with animator resources
		// representing
		// rotations when switching to the back of the card, as well as animator
		// resources representing rotations when flipping back to the front
		// (e.g. when
		// the system Back button is pressed).
				.setCustomAnimations(R.animator.card_flip_right_in,
						R.animator.card_flip_right_out,
						R.animator.card_flip_left_in,
						R.animator.card_flip_left_out)

				// Replace any fragments currently in the container view with a
				// fragment
				// representing the next page (indicated by the just-incremented
				// currentPage
				// variable).
				.replace(R.id.container, fragment)

				// Add this transaction to the back stack, allowing users to
				// press Back
				// to get to the front of the card.
				.addToBackStack(null)

				// Commit the transaction.
				.commit();
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

	public static class CardFrontFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.online_product_front,
					container, false);

			final ImageView imageView = (ImageView) view
					.findViewById(R.id.image);

			DisplayImageOptions options = new DisplayImageOptions.Builder()
					// .showImageOnLoading(R.drawable.ic_image_placeholder_a)
					.displayer(new FadeInBitmapDisplayer(250))
					.cacheOnDisk(true).preProcessor(new BitmapProcessor() {
						@Override
						public Bitmap process(Bitmap bitmap) {
							Bitmap temp = bitmap;
							// if (temp.getHeight() > (int) (300 *
							// getActivity().getResources().getDisplayMetrics().density))
							// {
							// File f =
							// ImageLoader.getInstance().getDiskCache().get(mProduct.getImagebig());
							// if (f != null) {
							// try {
							// BitmapRegionDecoder brd =
							// BitmapRegionDecoder.newInstance(f.getAbsolutePath(),
							// false);
							// temp = brd.decodeRegion(new Rect(0, 0,
							// temp.getWidth(), (int) (300 *
							// getActivity().getResources().getDisplayMetrics().density)),
							// null);
							// brd.recycle();
							// } catch (IOException e) {
							// e.printStackTrace();
							// }
							// }
							// } else {

							final float x = getActivity().getResources()
									.getDisplayMetrics().widthPixels - 32;
							final float y = (x * temp.getHeight())
									/ temp.getWidth();

							temp = Bitmap.createScaledBitmap(temp, (int) x,
									(int) y, true);
							return BeUtils.getRoundedCornerBitmap(
									getActivity(), temp, 3, temp.getWidth(),
									temp.getHeight(), false, false, false,
									false);
							// }
							// return
							// BeUtils.getRoundedCornerBitmap(getActivity(),
							// temp, 3, temp.getWidth(), temp.getHeight(),
							// false, false, false, false);
						}
					}).build();

			ImageLoader.getInstance().displayImage(mProduct.getImagebig(),
					imageView, options);

			view.findViewById(R.id.iv_online_info).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							flipCard(getFragmentManager());
						}
					});

			return view;
		}
	}

	public static class CardBackFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.online_product_back,
					container, false);

			view.findViewById(R.id.iv_online_close).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							getFragmentManager().popBackStack();
							mShowingBack = false;
						}
					});

			TextView tvDescription = (TextView) view
					.findViewById(R.id.tv_product_description);
			tvDescription.setText("DESCRIPTION\n\n" + mProduct.getDescription()
					+ "\n\nTERMS OF SERVICE\n\n"
					+ (mProduct.getTos() != null ? mProduct.getTos() : ""));
			tvDescription.setMovementMethod(new ScrollingMovementMethod());
			tvDescription.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent ev) {
					if (ev.getAction() == MotionEvent.ACTION_DOWN) {
						view.getParent().requestDisallowInterceptTouchEvent(
								true);
					}
					if (ev.getAction() == MotionEvent.ACTION_UP) {
						view.getParent().requestDisallowInterceptTouchEvent(
								false);
					}

					return view.onTouchEvent(ev);
				}
			});

			return view;
		}
	}
}
