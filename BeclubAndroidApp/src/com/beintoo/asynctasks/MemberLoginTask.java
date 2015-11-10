package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.AuthResource;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.AuthBean;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.MeBean;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.UnknownHostException;

import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * 用户登陆Task
 * 
 * @author thomas
 * 
 */
public class MemberLoginTask extends AsyncTask<Void, Void, Boolean> {

	public interface OnLoginListener {
		public void onLoginSucceed();

		public void onLoginFailed(AsyncTasks.Result statusResult);
	}

	private Context mContext;
	private AsyncTasks.Result statusResult;
	private OnLoginListener mListener;
	private GeoAndDeviceWrapper mWrapper;
	private ProgressDialog mProgressDialog;

	public MemberLoginTask(Context context, GeoAndDeviceWrapper wrapper,
			OnLoginListener listener) {
		this.mContext = context;
		this.mWrapper = wrapper;
		this.mListener = listener;
		this.mProgressDialog = ProgressDialog.show(context, "Login",
				"Please wait...", true, false);
	}

	@Override
	protected Boolean doInBackground(Void... data) {
		try {
			RestAdapter restAdapter = BeRestAdapter.getAuthKeyRestAdapter();
			AuthResource.IAuthResource service = restAdapter
					.create(AuthResource.IAuthResource.class);

			AuthBean auth = service.loginMember(mWrapper);

			if (auth.getToken() != null) {
				MemberAuthStore.setAuth(mContext, auth);

				restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
				service = restAdapter.create(AuthResource.IAuthResource.class);
				Response response = service.me(auth.getToken());

				MeBean meBean = new Gson().fromJson(new InputStreamReader(
						response.getBody().in()), MeBean.class);
				if (meBean.getMember() != null) {
					MemberAuthStore.setMember(mContext, meBean.getMember());
					MemberAuthStore.setApp(mContext, meBean.getApp());
					statusResult = AsyncTasks.Result.OK;
					return true;
				} else {
					statusResult = AsyncTasks.Result.FAILED;
				}
			} else {
				statusResult = AsyncTasks.Result.FAILED;
			}
		} catch (ApiException apiEx) {
			apiEx.printStackTrace();
			if (apiEx.getId() == -4) {
				statusResult = AsyncTasks.Result.NOT_AVAILABLE;
			} else if (apiEx.getId() == ApiConfiguration.BANNED_DEVICE_CODE) {
				statusResult = AsyncTasks.Result.BANNED;
			} else if (apiEx.getId() == ApiConfiguration.EMAIL_NOT_VALID_CODE) {
				statusResult = AsyncTasks.Result.OTHER;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() instanceof UnknownHostException) {
				statusResult = AsyncTasks.Result.NETWORK_ERROR;
			} else {
				statusResult = AsyncTasks.Result.FAILED;
			}
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean loginSucceed) {
		super.onPostExecute(loginSucceed);

		if (statusResult == AsyncTasks.Result.OK) {
			if (mListener != null) {
				mListener.onLoginSucceed();
			}
		} else {
			if (mListener != null) {
				mListener.onLoginFailed(statusResult);
			}
		}

		try {
			mProgressDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
