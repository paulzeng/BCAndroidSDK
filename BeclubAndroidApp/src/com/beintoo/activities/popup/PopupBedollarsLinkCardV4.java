package com.beintoo.activities.popup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.asynctasks.ProfileCardsTask;
import com.beintoo.beclubapp.R;
import com.beintoo.fragments.CardItemFragment;
import com.beintoo.utils.ui.CirclePageIndicator;
import com.beintoo.utils.ui.SmartViewPager;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.PaginatedList;

import java.util.List;

public class PopupBedollarsLinkCardV4 extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_bedollars_link_card_v4);

        TextView tvActionText = (TextView) findViewById(R.id.action_button_text);
        tvActionText.setText("Tap here to link");


        final SmartViewPager mViewPager = (SmartViewPager) findViewById(R.id.vp_cards);

        final int widthPx = getResources().getDisplayMetrics().widthPixels - 20;
        final int heightPx = (widthPx * 450) / 730;
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(widthPx, heightPx));

        final CirclePageIndicator mPageIndicator = (CirclePageIndicator) findViewById(R.id.vp_cards_indicator);

        new ProfileCardsTask(this, new ProfileCardsTask.CardsListListener() {
            @Override
            public void onCardsListLoaded(PaginatedList<CardsInfoResponse> wrapper) {
                if (wrapper != null && wrapper.getList() != null) {
                    if (wrapper.getList().size() > 0) {
                        mViewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(), wrapper.getList()));
                        mPageIndicator.setViewPager(mViewPager);
                    } else {

                    }
                }
            }

            @Override
            public void onError() {

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class CustomAdapter extends FragmentPagerAdapter {

        private List<CardsInfoResponse> cardsContainer;

        public CustomAdapter(FragmentManager fm, List<CardsInfoResponse> cards) {
            super(fm);
            this.cardsContainer = cards;
        }

        @Override
        public Fragment getItem(int position) {
            return new CardItemFragment(cardsContainer.get(position));
        }

        @Override
        public int getCount() {
            return cardsContainer.size();
        }
    }
}
