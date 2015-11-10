package com.beintoo.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.SpreedlyResource;
import com.beintoo.utils.DebugUtility;
import com.beintoo.wrappers.creditcard.CreditCard;
import com.beintoo.wrappers.creditcard.Data;
import com.beintoo.wrappers.creditcard.PaymentMethod;
import com.beintoo.wrappers.creditcard.SpreedlyRegisterCCWrapper;
import com.beintoo.wrappers.creditcard.SpreedlyResponse;

public class SpreedlyRegistrationTask extends AsyncTask<Void, Void, SpreedlyResponse> {

    public interface CardsRegistrationListener {
        public void onRegistrationCompleted(SpreedlyResponse cardsInfoResponse);
        public void onRegistrationError();
    }

    private Context mContext;
    private String mCardNumber;
    private String mMonth;
    private String mYear;
    private ProgressDialog mProgress;
    private CardsRegistrationListener mListener;

    public SpreedlyRegistrationTask(Context context, String cardNumber, String month, String year, CardsRegistrationListener listener) {
        this.mContext = context;
        this.mCardNumber = cardNumber;
        this.mMonth = month;
        this.mYear = year;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgress = ProgressDialog.show(mContext, null, "Registering card...", true, false);
    }

    @Override
    protected SpreedlyResponse doInBackground(Void... voids) {
        try {
            DebugUtility.showLog("Spreedly Register new card!");
            SpreedlyResource csr = new SpreedlyResource();

            SpreedlyRegisterCCWrapper.Builder builder = new SpreedlyRegisterCCWrapper.Builder();
            builder.setPaymentMethod(new PaymentMethod.Builder()
                    .setCreditCard(new CreditCard.Builder()
                            .setFirstName("-")
                            .setLastName("-")
                            .setEmail("-")
                            .setMonth(mMonth)
                            .setYear(mYear)
                            .setNumber(mCardNumber)
                            .build())
                    .setData(new Data.Builder()
                            .build())
                    .build());

            SpreedlyResponse spreedlyResponse = csr.registerCardOnSpreedly(mContext, builder.build());

            if(spreedlyResponse != null) {
                boolean success = csr.registerCardTokenOnBeintoo(mContext, spreedlyResponse);
                if(success) {
                    return spreedlyResponse;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(SpreedlyResponse cardsInfoResponse) {
        super.onPostExecute(cardsInfoResponse);
        mProgress.dismiss();
        if(mListener != null) {
            if(cardsInfoResponse != null) {
                mListener.onRegistrationCompleted(cardsInfoResponse);
            } else {
                mListener.onRegistrationError();
            }
        }
    }
}
