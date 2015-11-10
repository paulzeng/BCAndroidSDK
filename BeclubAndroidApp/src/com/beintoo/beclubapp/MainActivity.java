package com.beintoo.beclubapp;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.beintoo.activities.BeNotificationActivity;
import com.beintoo.activities.CollectiveMissionCompleteActivity;
import com.beintoo.activities.EmptyLoadingActivity;
import com.beintoo.activities.ExternalWebView;
import com.beintoo.activities.IntroducingCardLinkingActivity;
import com.beintoo.activities.MemberEntryActivity;
import com.beintoo.activities.RewardActivity;
import com.beintoo.activities.SettingsActivity;
import com.beintoo.activities.TellAFriendActivity;
import com.beintoo.adapters.BeViewPagerAdapter;
import com.beintoo.adapters.LateralMenuAdapter;
import com.beintoo.api.ApiConfiguration;
import com.beintoo.asynctasks.AsyncTasks;
import com.beintoo.asynctasks.MeTask;
import com.beintoo.asynctasks.NotificationClickedTask;
import com.beintoo.broadcasters.WidgetBedollars;
import com.beintoo.dialogs.TutorialDialog;
import com.beintoo.fragments.BestoreFragmentV2;
import com.beintoo.fragments.EarnBedollarsTabFragmentV2;
import com.beintoo.fragments.ProfileCardsFragment;
import com.beintoo.fragments.ProfileFragmentV3;
import com.beintoo.helper.MopubHelper;
import com.beintoo.services.GimbalBackgroundService;
import com.beintoo.utils.DataStore;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.FraudManager;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.RedirectingScreenUtils;
import com.beintoo.utils.notification.BeNotificationManager;
import com.beintoo.utils.notification.NotificationID;
import com.beintoo.utils.notification.NotificationTimeStore;
import com.beintoo.utils.ui.NonSwipeableViewPager;
import com.beintoo.wrappers.EventNotificationWrapper;
import com.beintoo.wrappers.MeAndAuthWrapper;
import com.beintoo.wrappers.MissionNearMeWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qualcommlabs.usercontext.Callback;
import com.qualcommlabs.usercontext.ContextCoreConnector;
import com.qualcommlabs.usercontext.ContextCoreConnectorFactory;

public class MainActivity extends BeNotificationActivity implements
		ActionBar.TabListener {

	private DrawerLayout mDrawerLayout;
	@SuppressWarnings("deprecation")
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private String mTitle;
	private Fragment mCurrentFragment;
	private NonSwipeableViewPager mViewPager;
	private BeViewPagerAdapter mAdapter;

	private long backHandleTime;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(mTitle);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		MopubHelper.getInstance().onCreate(this);

		mTitle = getString(R.string.lateral_menu_earn_bedollars);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new LateralMenuAdapter(this));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mViewPager = (NonSwipeableViewPager) findViewById(R.id.view_pager);
		mViewPager.setOffscreenPageLimit(1);

		getActionBar().addTab(
				getActionBar().newTab().setText("VIEW ALL")
						.setTabListener(this));
		getActionBar()
				.addTab(getActionBar().newTab().setText("NEAR ME")
						.setTabListener(this));

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */) {
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(
						getResources().getString(R.string.app_name));
				((LateralMenuAdapter) mDrawerList.getAdapter())
						.notifyDataSetChanged();
				invalidateOptionsMenu();

				/**
				 * If the email is not verified we call the MeTask to check if
				 * the user has verified the email
				 */
				if (!MemberAuthStore.isUserVerified(MainActivity.this)) {
					new MeTask(MainActivity.this).execute();
				}
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		this.mCurrentFragment = new EarnBedollarsTabFragmentV2();
		mAdapter = new BeViewPagerAdapter(getSupportFragmentManager(),
				mCurrentFragment);
		mViewPager.setAdapter(mAdapter);

		new MeTask(this, new MeTask.MeCallback() {
			@Override
			public void onComplete(MeAndAuthWrapper meAndAuth) {
				checkIfShowTutorial();
			}

			@Override
			public void onError(AsyncTasks.Result result) {

			}
		}).execute();

		GCMUtils.getInstance(getApplicationContext()).initGCM();

		BeNotificationManager.getInstance(this).clearAllNotifications();
		BeNotificationManager.getInstance(getApplicationContext())
				.scheduleNotification();

		handleNewIntent(getIntent().getExtras());

		if (MemberAuthStore.hasLocationBaseNotificationEnable(this)
				&& NotificationTimeStore.getWhenTriggerNotification(this,
						NotificationID.WAKE_UP_GIMBAL_SDK.getId()) == 0) {
			ContextCoreConnector mContextConnector = ContextCoreConnectorFactory
					.get(this);
			if (!mContextConnector.isPermissionEnabled()) {
				mContextConnector.enable(this, new Callback<Void>() {
					@Override
					public void success(Void aVoid) {
						startService(new Intent(MainActivity.this,
								GimbalBackgroundService.class));
						DebugUtility.showLog("Gimbal Enabled!");
					}

					@Override
					public void failure(int i, String s) {
						DebugUtility.showLog("Gimbal activation failed! " + s);
					}
				});
			} else {
				startService(new Intent(this, GimbalBackgroundService.class));
				DebugUtility.showLog("Gimbal is ready, no activation needed!");
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				((LateralMenuAdapter) mDrawerList.getAdapter())
						.setCurrentFragment(null);
				((LateralMenuAdapter) mDrawerList.getAdapter())
						.notifyDataSetChanged();
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				((LateralMenuAdapter) mDrawerList.getAdapter())
						.setCurrentFragment(mCurrentFragment);
				((LateralMenuAdapter) mDrawerList.getAdapter())
						.notifyDataSetChanged();
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {
		getActionBar()
				.setTitle(getString(R.string.lateral_menu_earn_bedollars));
		mDrawerLayout.closeDrawer(mDrawerList);
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {

	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			invalidateOptionsMenu();
			selectItem(position, null);
		}
	}

	private void selectItem(int position, Bundle args) {

		switch (position) {
		case 0:
			return;
		case 3:
			if (mCurrentFragment instanceof ProfileFragmentV3) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return;
			}

			mCurrentFragment = new ProfileFragmentV3();
			if (args != null) {
				mCurrentFragment.setArguments(args);
			}
			mTitle = getString(R.string.profile_wallet);
			getActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mViewPager.setCurrentItem(0);
			break;
		case 2:
			if (mCurrentFragment instanceof BestoreFragmentV2) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return;
			}

			mCurrentFragment = new BestoreFragmentV2();
			mTitle = this.getResources().getString(
					R.string.lateral_menu_bestore);
			getActionBar()
					.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mViewPager.setCurrentItem(0);
			break;
		case 1:
			if (mCurrentFragment instanceof EarnBedollarsTabFragmentV2) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return;
			}

			mCurrentFragment = new EarnBedollarsTabFragmentV2();
			mTitle = getString(R.string.lateral_menu_earn_bedollars);
			getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			getActionBar().setSelectedNavigationItem(0);
			break;
		case 5:
			startActivityForResult(new Intent(MainActivity.this,
					SettingsActivity.class), SettingsActivity.REQUEST_LOGOUT);
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		case 6:
			try {
				Intent intent = new Intent(this, ExternalWebView.class);
				String env = "";
				if (ApiConfiguration.workingEnvironment == ApiConfiguration.Environment.SANDBOX) {
					env = "sandbox/";
				}
				intent.putExtra(
						"url",
						"http://static.beintoo.com.s3.amazonaws.com/beclub-app/help/"
								+ env
								+ "index.html?nav=mobile&fromSdk=true&appversion="
								+ getPackageManager().getPackageInfo(
										getPackageName(), 0).versionName
										.toString() + "&os=android&lang="
								+ Locale.getDefault().getDisplayName()
								+ "&token="
								+ MemberAuthStore.getAuth(this).getToken());
				intent.putExtra("title", "Help");
				startActivity(intent);
				mDrawerLayout.closeDrawer(mDrawerList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		case 4:
			startActivity(new Intent(MainActivity.this,
					TellAFriendActivity.class));
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}

		// getSupportFragmentManager().beginTransaction().add(R.id.content_frame,
		// mCurrentFragment).commit();

		mAdapter.refreshFragment(mCurrentFragment);

		// mDrawerList.setItemChecked(position, true);
		getActionBar().setTitle(mTitle);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		DebugUtility.showLog("onActivityResult " + requestCode + " ---- "
				+ resultCode);

		if (requestCode == SettingsActivity.REQUEST_LOGOUT
				&& resultCode == RESULT_OK) {
			finish();
		} else if (requestCode == RewardActivity.RESULT_DISCOVER_HOW
				&& resultCode == RESULT_OK) { // Coming from BeStore
												// RewardActivity -> Earn
												// BeDollars
			selectItem(1, null);
		} else if (requestCode == ProfileCardsFragment.REQUEST_MODIFY_CARD
				&& resultCode == RESULT_OK) { // Coming from
												// CardsTransactionActivity ->
												// Take me to Earn
			selectItem(1, null);
		} else if (requestCode == CollectiveMissionCompleteActivity.RESULT_OPEN_BESTORE_OR_WALLET
				&& resultCode == 0) {
			selectItem(3, null);
		} else if (requestCode == CollectiveMissionCompleteActivity.RESULT_OPEN_BESTORE_OR_WALLET
				&& resultCode == 1) {
			selectItem(2, null);
		} else if (requestCode == 64207 && resultCode == RESULT_OK) {

		} else if (resultCode == FraudManager.BANNED_ACTIVITY_RESULT) {
			MemberAuthStore.logoutMember(this);
			finish();
			startActivity(new Intent(this, MemberEntryActivity.class));
		} else if (requestCode == ProfileCardsFragment.REQUEST_MODIFY_CARD) {
			if (mAdapter != null && mViewPager != null) {
				mAdapter.getItem(mViewPager.getCurrentItem()).onActivityResult(
						requestCode, resultCode, data);
			}
		} else if (requestCode == IntroducingCardLinkingActivity.REQUEST_CREDIT_CARD_LINKING
				&& resultCode == RESULT_OK) {
			Bundle args = new Bundle();
			args.putBoolean("showCards", true);
			selectItem(0, args);
		}

		DebugUtility.showLog("REQUEST " + requestCode + " ACTIVITY RESULT "
				+ resultCode);
	}

	private void checkIfShowTutorial() {
		try {
			String memberId = MemberAuthStore.getMember(this).getId();
			List<String> tutorialList = DataStore.getStringList(this,
					"tutorial_users");
			if (!tutorialList.contains(memberId)) {
				if (!isFinishing()) {
					new TutorialDialog(this).show(getFragmentManager(),
							"Tutorial");
					tutorialList.add(memberId);
					DataStore.saveStringList(this, "tutorial_users",
							tutorialList);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (backHandleTime == 0) {
				backHandleTime = System.currentTimeMillis();
				Toast.makeText(this,
						getString(R.string.beclub_tap_againt_to_exit),
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				long now = System.currentTimeMillis();
				if (now - backHandleTime < 3000) {
					backHandleTime = 0;
					return super.onKeyDown(keyCode, event);
				}
				backHandleTime = System.currentTimeMillis();
				Toast.makeText(this,
						getString(R.string.beclub_tap_againt_to_exit),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void handleNewIntent(Bundle bundle) {
		if (bundle == null) {
			return;
		}
		// User is not logged in. Redirect to login screen.
		if (MemberAuthStore.getAuth(this) == null
				&& MemberAuthStore.getMember(this) == null) {
			Intent intent = new Intent(this, BeclubActivityChooser.class);
			startActivity(intent);
			finish();
			return;
		}

		// Track open notification
		if (bundle.getString("notificationWrapper") != null) {
			EventNotificationWrapper notificationWrapper = new Gson().fromJson(
					bundle.getString("notificationWrapper"),
					EventNotificationWrapper.class);
			new NotificationClickedTask(this, notificationWrapper, "open")
					.execute();
		}

		String eventtype = bundle.getString("eventtype");
		if ("PUSH_NOTIFICATION".equals(eventtype)) { // Coming from Gimbal
														// Notification in
														// AlarmReceiver
			try {
				MissionNearMeWrapper missionNearMe = new Gson()
						.fromJson(bundle.getString("mission"),
								MissionNearMeWrapper.class);
				String type = missionNearMe.getMissions().getMission()
						.getActioncheck().getSubtype().toString();
				Intent intent = RedirectingScreenUtils.redirectingFromParams(
						this, type, missionNearMe.getMissions().getMission()
								.getBrandsid());
				startActivity(intent);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		} else if ("URL_SCHEME_LAUNCH".equals(eventtype)) {
			String path = bundle.getString("path");
			Intent intent = null;
			Bundle args = new Bundle();
			if (path != null) {
				if (path.equals("/bestore")) {
					if (bundle.getString("rewardID") != null) {
						args.putString("rewardID", bundle.getString("rewardID"));
						intent = new Intent(this, EmptyLoadingActivity.class);
						intent.putExtras(args);
						startActivity(intent);
					}
					selectItem(2, null);
				} else if (path.equals("/earnbedollars")) {
					selectItem(1, null);
				} else if (path.equals("/earnbedollars/brands")) {
					if (bundle.getString("brandID") != null) {
						args.putString("brandID", bundle.getString("brandID"));
						if (bundle.getString("missionType") != null) {
							args.putString("missionType",
									bundle.getString("missionType"));
						}
						intent = new Intent(this, EmptyLoadingActivity.class);
						intent.putExtras(args);
						startActivity(intent);
					}
				} else if (path.equals("/earnbedollars/missions")) {
					if (bundle.getString("missionID") != null) {
						args.putString("missionID",
								bundle.getString("missionID"));
						intent = new Intent(this, EmptyLoadingActivity.class);
						intent.putExtras(args);
						startActivity(intent);
					}
				} else if (path.equals("/userprofile")) {
					selectItem(3, null);
				} else if (path.equals("/userprofile/balance")) {
					args.putBoolean("showBalance", true);
					selectItem(3, args);
				} else if (path.equals("/userprofile/cards")) {
					args.putBoolean("showCards", true);
					selectItem(3, args);
				} else if (path.equals("/userprofile/settings")) {
					startActivityForResult(new Intent(MainActivity.this,
							SettingsActivity.class),
							SettingsActivity.REQUEST_LOGOUT);
				} else if (path.equals("/help")) {
					try {
						intent = new Intent(this, ExternalWebView.class);
						intent.putExtra(
								"url",
								"http://static.beintoo.com.s3.amazonaws.com/beclub-app/help/index.html?nav=mobile&appversion="
										+ getPackageManager().getPackageInfo(
												getPackageName(), 0).versionName
												.toString()
										+ "&os=android&lan="
										+ Locale.getDefault().getDisplayName()
										+ "&token="
										+ MemberAuthStore.getAuth(this)
												.getToken());
						intent.putExtra("title", "Help");
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (path.equals("/tellafriend")) {
					startActivity(new Intent(MainActivity.this,
							TellAFriendActivity.class));
				} else if (path.equals("/whatsnew")) {
					// Not implemented yet.
				} else if (path.equals("/earnbedollars/map")) {
					if (mViewPager != null) {
						mViewPager.setCurrentItem(1);
						getActionBar().setSelectedNavigationItem(1);
					}
				}
			}

			WidgetBedollars.updateWidgetBalance(MainActivity.this);
		}
	}

	@Override
	protected void onDestroy() {
		MopubHelper.getInstance().onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}
}