package com.beintoo.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.activities.CardsAddCardActivity;
import com.beintoo.adapters.ProfileCardsAdapter;
import com.beintoo.asynctasks.ProfileCardsTask;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.DebugUtility;
import com.beintoo.wrappers.CardsInfoResponse;
import com.beintoo.wrappers.PaginatedList;

public class ProfileCardsFragment extends Fragment {

    public static final int REQUEST_MODIFY_CARD = 121;
    public static final int RESULT_MODIFIED_CARD = 122;

    private ListView mListView;
    private ProgressBar mProgressBar;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.profile_fragment, container, false);

        ((TextView)mView.findViewById(R.id.tv_list_header_text)).setText("TBD: For connecting your card...");
        mView.findViewById(R.id.ll_bottom_divider).setVisibility(View.GONE);

        mListView = (ListView) mView.findViewById(R.id.listView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progressBar);

        mListView.setDivider(null);
        mListView.setDividerHeight(0);

        /*
        [13 October 2014] - DISABLED ADD CARD BUTTON.
         */
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(mListView.getHeaderViewsCount() == 0) {
//                    CardsInfoResponse card = (CardsInfoResponse) adapterView.getItemAtPosition(i);
//                    Intent intent = new Intent(getActivity(), CardsTransactionActivity.class);
//                    intent.putExtra("card", new Gson().toJson(card));
//                    getActivity().startActivityForResult(intent, REQUEST_MODIFY_CARD);
//                }
//            }
//        });

        new ProfileCardsTask(getActivity(), new ProfileCardsTask.CardsListListener() {
            @Override
            public void onCardsListLoaded(PaginatedList<CardsInfoResponse> cardsPerUsersWrapper) {
                mProgressBar.setVisibility(View.GONE);
                List<CardsInfoResponse> cardsContainer = cardsPerUsersWrapper.getList();
                if (cardsContainer != null && getParentFragment() != null && getParentFragment().getView() != null) {
                    if (cardsContainer.size() == 0) {
                        setupNoCardsView();
                    } else {
                        if (mListView.getFooterViewsCount() == 0) {
                            View footer = View.inflate(getActivity(), R.layout.action_button, null);
                            RelativeLayout layout = (RelativeLayout) footer.findViewById(R.id.action_button_content);

                            TextView tvActionText = (TextView) footer.findViewById(R.id.action_button_text);
                            tvActionText.setText("Add a card");

                            int a = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("maxCardsPerUser", 4);
                            DebugUtility.showLog("####: " + a);

                            if (cardsContainer.size() >= PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("maxCardsPerUser", 4)) {
                                changeActionButtonState(footer, false);
                            }
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getActivity().startActivityForResult(new Intent(getActivity(), CardsAddCardActivity.class), ProfileCardsFragment.REQUEST_MODIFY_CARD);
                                }
                            });
                            /*
                            [13 October 2014] - DISABLED ADD CARD BUTTON.
                             */
//                            mListView.addFooterView(footer, null, true);
                        }
                        mListView.setAdapter(new ProfileCardsAdapter(getActivity(), cardsContainer));
                    }
                } else {
                    setupNoCardsView();
                }
            }

            @Override
            public void onError() {
                setupNoCardsView();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return mView;
    }

    private void setupNoCardsView() {
        mProgressBar.setVisibility(View.GONE);
        if(getActivity() == null) {
            return;
        }
        View header = View.inflate(getActivity(), R.layout.header_no_cards_connected, null);
        TextView tvActionText = (TextView) header.findViewById(R.id.action_button_text);
        tvActionText.setText(getString(R.string.cards_connect_a_card));

        RelativeLayout rlActionContent = (RelativeLayout) header.findViewById(R.id.action_button_content);
        rlActionContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivityForResult(new Intent(getActivity(), CardsAddCardActivity.class), ProfileCardsFragment.REQUEST_MODIFY_CARD);
            }
        });

        mListView.addHeaderView(header);
        mListView.setAdapter(new ProfileCardsAdapter(getActivity(), new ArrayList<CardsInfoResponse>()));
    }

    private void changeActionButtonState(View view, boolean enabled) {
        try {
            if (!enabled) {
                ((RelativeLayout) view.findViewById(R.id.action_button_content)).getClass().getMethod(android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground" : "setBackgroundDrawable",
                        Drawable.class).invoke(((RelativeLayout) view.findViewById(R.id.action_button_content)), getResources().getDrawable(R.drawable.disabled_button_selector));
                ((TextView) view.findViewById(R.id.action_button_text)).setTextColor(Color.WHITE);
            } else {
                ((RelativeLayout) view.findViewById(R.id.action_button_content)).getClass().getMethod(android.os.Build.VERSION.SDK_INT >= 16 ? "setBackground" : "setBackgroundDrawable",
                        Drawable.class).invoke(((RelativeLayout) view.findViewById(R.id.action_button_content)), getResources().getDrawable(R.drawable.green_button));
                ((TextView) view.findViewById(R.id.action_button_text)).setTextColor(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
    }
}
