package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.beintoo.api.SpreedlyResource;
import com.beintoo.beclubapp.R;

public class CardsDeleteCardTask extends AsyncTask<Void, Void, Boolean> {

    public interface DeleteCardsListener {
        public void onCardDeleted(String cardToken);
    }

    private Context mContext;
    private ProgressDialog mProgress;
    private String mCardToken;
    private DeleteCardsListener mListener;

    public CardsDeleteCardTask(Context context, String cardToken, DeleteCardsListener listener) {
        this.mContext = context;
        this.mCardToken = cardToken;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mProgress = ProgressDialog.show(mContext, null, mContext.getString(R.string.cards_deleting_card), true, false);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SpreedlyResource csr = new SpreedlyResource();
            return csr.deleteCard(mContext, mCardToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean deleted) {
        super.onPostExecute(deleted);
        this.mProgress.dismiss();

        if (deleted) {
            Toast.makeText(mContext, mContext.getString(R.string.cards_deleted_successfully), Toast.LENGTH_LONG).show();
            if (mListener != null) {
                mListener.onCardDeleted(mCardToken);
            }
        }
    }
}
