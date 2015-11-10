package com.beintoo.dialogs;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beintoo.activities.MemberLoginActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.ui.CircleImageView;
import com.beintoo.wrappers.MemberBean;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ExistingMembersDialog extends DialogFragment implements
		AdapterView.OnItemClickListener {

	private static final float GESTURE_THRESHOLD_DP = 16.0f;
	private float scale;

	private List<MemberBean> mMembers;

	public ExistingMembersDialog(List<MemberBean> members) {
		mMembers = members;
	}

	private final String mScreenName = "EXISTING-USERS";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.existing_member_dialog, null);

		ListView listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		MemberBean[] array = new MemberBean[mMembers.size()];
		mMembers.toArray(array);
		MembersAdapter adapter = new MembersAdapter(getActivity(), array);
		listView.setAdapter(adapter);

		return new AlertDialog.Builder(getActivity())
				.setTitle(
						this.getResources()
								.getString(R.string.already_existing))
				.setView(view)
				.setPositiveButton(R.string.already_use_another,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent(getActivity(),
										MemberLoginActivity.class);
								startActivity(intent);
								dismiss();

							}
						}).setInverseBackgroundForced(true).create();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Intent intent = new Intent(getActivity(), MemberLoginActivity.class);
		intent.putExtra("email", mMembers.get(i).getEmail());
		startActivity(intent);
		dismiss();

	}

	public class MembersAdapter extends ArrayAdapter<MemberBean> {

		public MembersAdapter(Context context, MemberBean[] objects) {
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_already_member, null);

			final CircleImageView avatar = (CircleImageView) convertView
					.findViewById(R.id.avatar);
			final TextView email = (TextView) convertView
					.findViewById(R.id.email);

			MemberBean member = getItem(position);
			email.setText(member.getEmail());

			ImageLoader.getInstance().loadImage(member.getImagesmall(),
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String s, View view) {

						}

						@Override
						public void onLoadingFailed(String s, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String s, View view,
								Bitmap bitmap) {
							avatar.setImageBitmap(bitmap);
						}

						@Override
						public void onLoadingCancelled(String s, View view) {

						}
					});

			// ImageLoader.getInstance().displayImage(member.getImagesmall(),
			// avatar);

			return convertView;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}
}