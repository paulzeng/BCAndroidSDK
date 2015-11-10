package com.beintoo.fragments;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.adapters.BestoreAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IBestoreResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.LocationUtils;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.BeSuperListView;
import com.beintoo.wrappers.MemberBean;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.RewardWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.quentindommerc.superlistview.OnMoreListener;

@SuppressLint("ResourceAsColor") public class BestoreFragmentV2 extends Fragment {

    public final static String screenName = "BESTORE";

    private String mLastKey;

    private BestoreAdapter mAdapter;
    private TextView tvBalance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bestore_fragment_v2, container, false);

        tvBalance = (TextView) view.findViewById(R.id.header_bedollars_amount);

        final BeSuperListView list = (BeSuperListView) view.findViewById(R.id.list);
        list.setRefreshingColor(android.R.color.holo_green_light, android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_green_dark);

        getBestore(mLastKey, 5, new Callback<PaginatedList<RewardWrapper>>() {
            @Override
            public void success(PaginatedList<RewardWrapper> result, Response response) {
                if (getActivity() != null) {
                    if (result != null) {
                        mLastKey = result.getLastKey();
                        mAdapter = new BestoreAdapter(getActivity(), R.id.textView, result.getList());
                    } else {
                        mAdapter = new BestoreAdapter(getActivity(), R.id.textView, new ArrayList<RewardWrapper>());
                    }
                    list.setAdapter(mAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                handleError(error);
            }
        });

        list.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLastKey = null;

                getBestore(mLastKey, 5, new Callback<PaginatedList<RewardWrapper>>() {
                    @Override
                    public void success(PaginatedList<RewardWrapper> result, Response response) {
                        if (getActivity() != null) {
                            if (result != null) {
                                mAdapter.clear();
                                mLastKey = result.getLastKey();
                                mAdapter = new BestoreAdapter(getActivity(), R.id.textView, result.getList());
                            } else {
                                mAdapter = new BestoreAdapter(getActivity(), R.id.textView, new ArrayList<RewardWrapper>());
                            }
                            list.setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleError(error);
                    }
                });
            }
        });

        list.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                if (mLastKey != null) {
                    getBestore(mLastKey, 5, new Callback<PaginatedList<RewardWrapper>>() {
                        @Override
                        public void success(PaginatedList<RewardWrapper> result, Response response) {
                            if (result != null) {
                                mLastKey = result.getLastKey();
                                mAdapter.addAll(result.getList());
                            }
                            list.hideMoreProgress();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            handleError(error);
                        }
                    });
                } else {
                    list.hideMoreProgress();
                }
            }
        }, 1);

        list.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

        return view;
    }

    private void getBestore(String lastKey, Integer rows, Callback<PaginatedList<RewardWrapper>> callback) {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IBestoreResource service = restAdapter.create(IBestoreResource.class);

        service.getBestore(MemberAuthStore.getAuth(getActivity()).getToken(), LocationUtils.getLocationParams(getActivity()), lastKey, rows, callback);
    }

    private void handleError(RetrofitError error) {
        if (error.isNetworkError()) {
            Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MemberBean mMe = MemberAuthStore.getMember(getActivity());
        tvBalance.setText("" + mMe.getBedollars().intValue());

         
    }
}
