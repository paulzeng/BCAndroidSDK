package com.beintoo.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.beintoo.adapters.CollectiveMissionContactsAdapter;
import com.beintoo.asynctasks.PhoneContactsTask;
import com.beintoo.beclubapp.R;
import com.beintoo.social.Shares;
import com.beintoo.utils.DataStore;
import com.beintoo.wrappers.ShareContactWrapper;

public class CollectiveMissionContactsActivity extends BeNotificationActivity
		implements AdapterView.OnItemClickListener {

	public enum ContactSources {
		FACEBOOK(0), SMS(1);

		private int value;

		private ContactSources(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private ContactSources mContactsSource;
	private List<ShareContactWrapper> mContacts;
	private ListView mListView;
	private EditText etFilter;
	private CollectiveMissionContactsAdapter mAdapter;
	private Integer mMinFriends = 0;
	private Integer mSelectedFriends = 0;
	private String mMatURL;
	private String mShareMessage;
	private String mMissionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.collective_mission_contacts_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("Select friends to invite");

		etFilter = (EditText) findViewById(R.id.et_name);
		mListView = (ListView) findViewById(R.id.listView);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getInt("source", -1) != -1) {
			mMinFriends = extras.getInt("min_friends");
			mMatURL = extras.getString("mat_url");
			mShareMessage = extras.getString("share_message");
			mMissionId = extras.getString("mission_id");

			// if(extras.getInt("source") ==
			// ContactSources.FACEBOOK.getValue()){
			// try{
			// FacebookManager.getFriends(this, new
			// Request.GraphUserListCallback(){
			// @Override
			// public void onCompleted(List<GraphUser> users, Response response)
			// {
			// mContacts = new ArrayList<ShareContactWrapper>();
			// if(users != null ) {
			// for(GraphUser g : users){
			// try {
			// JSONObject picture = (JSONObject)g.getProperty("picture");
			// ShareContactWrapper contact = new ShareContactWrapper(g.getId(),
			// g.getName(), g.getId(),
			// picture.getJSONObject("data").getString("url"));
			// mContacts.add(contact);
			// }catch (Exception e){
			// e.printStackTrace();
			// }
			// }
			// }
			//
			// Collections.sort(mContacts, new Comparator<ShareContactWrapper>()
			// {
			// public int compare(ShareContactWrapper s1, ShareContactWrapper
			// s2) {
			// return s1.getName().compareToIgnoreCase(s2.getName());
			// }
			// });
			//
			// mAdapter = new
			// CollectiveMissionContactsAdapter(CollectiveMissionContactsActivity.this,
			// R.layout.collective_contact_row, mContacts, mMissionId, false,
			// null);
			// mListView.setAdapter(mAdapter);
			// mListView.setOnItemClickListener(CollectiveMissionContactsActivity.this);
			// findViewById(R.id.progressBar).setVisibility(View.GONE);
			// findViewById(R.id.content).setVisibility(View.VISIBLE);
			// }
			// });
			// }catch (Exception e){
			// e.printStackTrace();
			// }
			// }else
			if (extras.getInt("source") == ContactSources.SMS.getValue()) {
				new PhoneContactsTask(this,
						new PhoneContactsTask.PhoneContactsCallback() {
							@Override
							public void onContacts(
									List<ShareContactWrapper> contacts) {
								mContacts = contacts;
								Collections.sort(mContacts,
										new Comparator<ShareContactWrapper>() {
											public int compare(
													ShareContactWrapper s1,
													ShareContactWrapper s2) {
												return s1.getName()
														.compareToIgnoreCase(
																s2.getName());
											}
										});
								mAdapter = new CollectiveMissionContactsAdapter(
										CollectiveMissionContactsActivity.this,
										R.layout.collective_contact_row,
										mContacts,
										mMissionId,
										true,
										new CollectiveMissionContactsAdapter.ItemCheckedCallback() {
											@Override
											public void onItemChecked(int pos) {
												mSelectedFriends++;
												((TextView) findViewById(R.id.text_selected_friends))
														.setText(getString(
																R.string.collective_mission_contacts_friends_selected)
																.replace(
																		"{n}",
																		mSelectedFriends
																				.toString()));
											}

											@Override
											public void onItemUnChecked(int pos) {
												mSelectedFriends--;
												((TextView) findViewById(R.id.text_selected_friends))
														.setText(getString(
																R.string.collective_mission_contacts_friends_selected)
																.replace(
																		"{n}",
																		mSelectedFriends
																				.toString()));
											}
										});
								mListView.setAdapter(mAdapter);
								findViewById(R.id.progressBar).setVisibility(
										View.GONE);
								findViewById(R.id.content).setVisibility(
										View.VISIBLE);
								findViewById(R.id.selected_container)
										.setVisibility(View.VISIBLE);
								((TextView) findViewById(R.id.text_min_friends))
										.setText(getString(
												R.string.collective_mission_contacts_friends_min)
												.replace("{n}",
														mMinFriends.toString()));
								((TextView) findViewById(R.id.text_selected_friends))
										.setText(getString(
												R.string.collective_mission_contacts_friends_selected)
												.replace(
														"{n}",
														mSelectedFriends
																.toString()));
								findViewById(R.id.actionButton).setVisibility(
										View.VISIBLE);
								findViewById(R.id.actionButton)
										.setOnClickListener(
												new View.OnClickListener() {
													@Override
													public void onClick(
															View view) {
														ArrayList<String> contactNumbers = new ArrayList<String>();
														List<String> alreadyInvited = DataStore
																.getStringList(
																		CollectiveMissionContactsActivity.this,
																		"collective_invited_friends_"
																				+ mMissionId);
														for (ShareContactWrapper scw : mContacts) {
															if (scw.isSelected()) {
																contactNumbers
																		.add(scw.getContact());
																alreadyInvited
																		.add(scw.getId());
															}
														}
														DataStore
																.saveStringList(
																		CollectiveMissionContactsActivity.this,
																		"collective_invited_friends_"
																				+ mMissionId,
																		alreadyInvited);

														Shares.groupSmsShare(
																CollectiveMissionContactsActivity.this,
																mShareMessage
																		+ " "
																		+ mMatURL,
																contactNumbers);
													}
												});
							}
						}).execute();
			}
		}

		etFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				if (mAdapter != null) {
					mAdapter.getFilter().filter(charSequence);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Session.getActiveSession().onActivityResult(this, requestCode,
		// resultCode, data);
		// FacebookManager.getIstance().facebookUiHelperActivityResultHandler(
		// requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		// used only for facebook
		// FacebookManager.getIstance().postOnFacebookApi(
		// this,
		// ((ShareContactWrapper) mListView.getAdapter().getItem(i))
		// .getContact(), mMissionId,
		// "Let's buddy up with the BeClub!", null, mShareMessage,
		// mMatURL, null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
