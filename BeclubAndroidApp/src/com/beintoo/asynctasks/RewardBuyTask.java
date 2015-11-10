package com.beintoo.asynctasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.ApiConfiguration;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IBestoreResource;
import com.beintoo.dialogs.RewardBuyDialog;
import com.beintoo.utils.ApiException;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.GeoAndDeviceWrapper;
import com.beintoo.wrappers.RewardWrapper;

import java.net.UnknownHostException;

import retrofit.RestAdapter;

public class RewardBuyTask extends AsyncTask<Void, Integer, BuyWrapper> {

    private Context mContext;
    private RewardWrapper mReward;
    private AsyncTasks.Result statusResult;
    private Dialog mProgress;
    private RewardBuyDialog.RewardBuyCallback mCallback;

    public RewardBuyTask(Context contet, RewardWrapper reward, RewardBuyDialog.RewardBuyCallback callback) {
        mContext = contet;
        mReward = reward;
        mCallback = callback;
        mProgress = ProgressDialog.show(contet, "Buy", "Please wait...", true, false);
    }

    @Override
    protected BuyWrapper doInBackground(Void... data) {
        try {
            RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
            IBestoreResource bestoreService = restAdapter.create(IBestoreResource.class);

            BuyWrapper buy = bestoreService.buyReward(MemberAuthStore.getAuth(mContext).getToken(), mReward.getId(), MemberAuthStore.getMember(mContext).getId(), GeoAndDeviceWrapper.build(mContext, null));

            if (buy != null && buy.getCode() != null) {
                DebugUtility.showLog("TASK GOT RESP " + buy + " code " + buy.getCode());
                statusResult = AsyncTasks.Result.OK;
                if (buy.getMemberbalance() != null) {
                    MemberAuthStore.updateMemberBedollarsBalance(mContext, buy.getMemberbalance());
                }
            } else {
                statusResult = AsyncTasks.Result.FAILED;
            }

            return buy;

        } catch (ApiException apiEx) {
            apiEx.printStackTrace();
            if (apiEx.getId() == -25) {
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
        return null;
    }

    @Override
    protected void onPostExecute(BuyWrapper buy) {

        try {
            mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (statusResult == AsyncTasks.Result.OK) {
            mCallback.onBuy(buy);
        } else if (statusResult == AsyncTasks.Result.NOT_AVAILABLE) {
            mCallback.onCouponNotAvailable();
        } else {
            mCallback.onError();
        }
    }
}
