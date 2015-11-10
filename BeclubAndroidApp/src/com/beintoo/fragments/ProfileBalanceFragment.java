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
import android.widget.Toast;

import com.beintoo.adapters.ProfileBalanceAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.wrappers.MembersBedollarsWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;


@SuppressLint("ResourceAsColor") public class ProfileBalanceFragment extends Fragment {

    private String mLastKey = null;
    private ProfileBalanceAdapter mAdapter;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DebugUtility.showLog("ProfileBalanceFragment onCreateView");
        View view = inflater.inflate(R.layout.profile_balance_fragment, container, false);

        final SuperListview list = (SuperListview) view.findViewById(R.id.list);
//        list.getList().addHeaderView(View.inflate(getActivity(), R.layout.profile_balance_header, null));
        list.setRefreshingColor(android.R.color.holo_green_light, android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_green_dark);

        getMemberHistory(mLastKey, 10, new Callback<PaginatedList<MembersBedollarsWrapper>>() {
            @Override
            public void success(PaginatedList<MembersBedollarsWrapper> membersBedollarsWrapperPaginatedList, Response response) {
                if (getActivity() != null) {
                    if (membersBedollarsWrapperPaginatedList != null) {
                        mLastKey = membersBedollarsWrapperPaginatedList.getLastKey();
                        mAdapter = new ProfileBalanceAdapter(getActivity(), R.id.textView, membersBedollarsWrapperPaginatedList.getList());
                    } else {
                        mAdapter = new ProfileBalanceAdapter(getActivity(), R.id.textView, new ArrayList<MembersBedollarsWrapper>());
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

                getMemberHistory(mLastKey, 10, new Callback<PaginatedList<MembersBedollarsWrapper>>() {
                    @Override
                    public void success(PaginatedList<MembersBedollarsWrapper> membersBedollarsWrapperPaginatedList, Response response) {
                        if (getActivity() != null) {
                            if (membersBedollarsWrapperPaginatedList != null) {
                                mAdapter.clear();
                                mLastKey = membersBedollarsWrapperPaginatedList.getLastKey();
                                mAdapter = new ProfileBalanceAdapter(getActivity(), R.id.textView, membersBedollarsWrapperPaginatedList.getList());
                            } else {
                                mAdapter = new ProfileBalanceAdapter(getActivity(), R.id.textView, new ArrayList<MembersBedollarsWrapper>());
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
                    getMemberHistory(mLastKey, 10, new Callback<PaginatedList<MembersBedollarsWrapper>>() {
                        @Override
                        public void success(PaginatedList<MembersBedollarsWrapper> membersBedollarsWrapperPaginatedList, Response response) {
                            if (membersBedollarsWrapperPaginatedList != null) {
                                mLastKey = membersBedollarsWrapperPaginatedList.getLastKey();
                                mAdapter.addAll(membersBedollarsWrapperPaginatedList.getList());
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

        return view;
    }

    private void handleError(RetrofitError error) {
        if(error.isNetworkError()) {
            Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
        }
    }

    private void getMemberHistory(String lastKey, Integer rows, Callback<PaginatedList<MembersBedollarsWrapper>> callback) {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IMemberResource service = restAdapter.create(IMemberResource.class);

        service.getMemberHistory(MemberAuthStore.getAuth(getActivity()).getToken(), MemberAuthStore.getMember(getActivity()).getId(), lastKey, rows, "rewards,missions", callback);
    }
}
