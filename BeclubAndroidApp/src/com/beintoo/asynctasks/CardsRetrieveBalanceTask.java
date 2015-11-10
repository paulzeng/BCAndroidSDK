package com.beintoo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.adapters.ProfileCardsAdapter;
import com.beintoo.api.SpreedlyResource;

public class CardsRetrieveBalanceTask extends AsyncTask<Void, Void, String> {

    public interface BalanceListener {
        public void onBalanceLoaded(String balance);
    }

    private Context mContext;
    private String mCardToken;
    private int mPosition;
    private ProfileCardsAdapter.CardHolder mHolder;
    private BalanceListener mListener;

    public CardsRetrieveBalanceTask(Context context, String cardToken, int position, ProfileCardsAdapter.CardHolder holder, BalanceListener listener) {
        this.mContext = context;
        this.mCardToken = cardToken;
        this.mPosition = position;
        this.mHolder = holder;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            SpreedlyResource csr = new SpreedlyResource();
            String balance = csr.getBalanceForCard(mContext, mCardToken);
            if(balance != null) {
                return balance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(this.mHolder != null && this.mHolder.position == mPosition) {
            mHolder.tvTotalEarned.setText("" + Double.valueOf(s).intValue());
        }
        if(mListener != null) {
            mListener.onBalanceLoaded("" + Double.valueOf(s).intValue());
        }
    }
}
