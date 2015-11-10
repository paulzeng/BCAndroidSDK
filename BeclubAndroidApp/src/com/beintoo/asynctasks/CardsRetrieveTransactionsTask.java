package com.beintoo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.SpreedlyResource;
import com.beintoo.wrappers.CardTransactionBean;
import com.beintoo.wrappers.PaginatedList;

public class CardsRetrieveTransactionsTask extends AsyncTask<Void, Void, PaginatedList<CardTransactionBean>> {

    public interface CardTransactionsListener {
        public void onTransactionsLoaded(PaginatedList<CardTransactionBean> transactions);
        public void onError();
    }

    private static final Integer ROWS = 10;

    private Context mContext;
    private String mCardToken;
    private CardTransactionsListener mListener;
    private String mLastKey;

    public CardsRetrieveTransactionsTask(Context context, String cardToken, String lastKey, CardTransactionsListener listener) {
        this.mContext = context;
        this.mCardToken = cardToken;
        this.mListener = listener;
        this.mLastKey = lastKey;
    }

    @Override
    protected PaginatedList<CardTransactionBean> doInBackground(Void... voids) {
        try {
            SpreedlyResource csr = new SpreedlyResource();
            PaginatedList<CardTransactionBean> transactions = csr.getTransactionsForCard(mContext, mCardToken, mLastKey, ROWS);

//            transactions = new PaginatedList<CardTransactionBean>();
//            transactions.setLastKey("123456789");
//
//            List<CardTransactionBean> list = new ArrayList<CardTransactionBean>();
//            for(int i=0; i<30; i++) {
//                CardTransactionBean ct = new CardTransactionBean();
//                ct.setBedollars(20.0);
//                ct.setBrandName("Best Buy");
//                ct.setBrandAddress("Corso di Porta Romana 68");
//                ct.setStatus(i % 2 == 0 ? "Accepted" : i % 3 == 0 ? "Pending" : "Returned");
//
//                list.add(ct);
//            }
//
//            transactions.setList(list);


            return transactions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(PaginatedList<CardTransactionBean> transactions) {
        super.onPostExecute(transactions);
        if(mListener != null) {
            if(transactions != null) {
                mListener.onTransactionsLoaded(transactions);
            }
        }
    }
}
