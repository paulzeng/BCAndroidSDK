package com.beintoo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.beintoo.api.SpreedlyResource;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.PaginatedList;

public class ProfileCardsTask extends AsyncTask<Void, Void, PaginatedList<CardsInfoResponse>> {

    public interface CardsListListener {
        public void onCardsListLoaded(PaginatedList<CardsInfoResponse> wrapper);
        public void onError();
    }

    private Context mContext;
    private CardsListListener mListener;

    public ProfileCardsTask(Context context, CardsListListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected PaginatedList<CardsInfoResponse> doInBackground(Void... voids) {
        try {
            MemberBean member = MemberAuthStore.getMember(mContext);
            if(member.getCardspringactivated() == null) {
                return null;
            }
            if(!member.getCardspringactivated()) {
                return null;
            }

            SpreedlyResource csr = new SpreedlyResource();
            return csr.getUserDetails(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(PaginatedList<CardsInfoResponse> cardsPerUsersWrapper) {
        super.onPostExecute(cardsPerUsersWrapper);
        if(mListener != null) {
            if(cardsPerUsersWrapper != null) {
                mListener.onCardsListLoaded(cardsPerUsersWrapper);
            } else {
                mListener.onError();
            }
        }
    }
}
