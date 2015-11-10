package com.beintoo.fragments;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beintoo.adapters.ProfileWalletAdapter;
import com.beintoo.api.BeRestAdapter;
import com.beintoo.api.IMemberResource;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.DebugUtility;
import com.beintoo.utils.MemberAuthStore;
import com.beintoo.utils.ui.BeSuperListView;
import com.beintoo.wrappers.BuyWrapper;
import com.beintoo.wrappers.PaginatedList;
import com.beintoo.wrappers.RewardWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.quentindommerc.superlistview.OnMoreListener;

@SuppressLint("ResourceAsColor") public class ProfileWalletFragment extends Fragment {

    private BeSuperListView list;

    public static String mLastKey = null;
    private ProfileWalletAdapter mAdapter;

    public static RewardWrapper.RewardTypes currentWalletType = RewardWrapper.RewardTypes.REGULAR;

    public static int ARCHIVE_CODE = 5000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DebugUtility.showLog("ProfileWalletFragment onCreateView");
        View view = inflater.inflate(R.layout.profile_wallet_fragment, container, false);
        list = (BeSuperListView) view.findViewById(R.id.list);

        list.setRefreshingColor(android.R.color.holo_green_light, android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_green_dark);

        mLastKey = null;

        getMemberWallet(currentWalletType.toString(), mLastKey, 10, new Callback<PaginatedList<BuyWrapper>>() {
            @Override
            public void success(PaginatedList<BuyWrapper> buyWrapperPaginatedList, Response response) {
                if (getActivity() != null) {
                    if (buyWrapperPaginatedList != null && buyWrapperPaginatedList.getList() != null) {
                        mLastKey = buyWrapperPaginatedList.getLastKey();
                        mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, buyWrapperPaginatedList.getList(), ProfileWalletFragment.this);
                    } else {
                        mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, new ArrayList<BuyWrapper>(), ProfileWalletFragment.this);
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
                getMemberWallet(currentWalletType.toString(), mLastKey, 10, new Callback<PaginatedList<BuyWrapper>>() {
                    @Override
                    public void success(PaginatedList<BuyWrapper> buyWrapperPaginatedList, Response response) {
                        if (getActivity() != null) {
                            if (buyWrapperPaginatedList != null && buyWrapperPaginatedList.getList() != null) {
                                mLastKey = buyWrapperPaginatedList.getLastKey();
                                mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, buyWrapperPaginatedList.getList(), ProfileWalletFragment.this);
                            } else {
                                mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, new ArrayList<BuyWrapper>(), ProfileWalletFragment.this);
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
                    getMemberWallet(currentWalletType.toString(), mLastKey, 10, new Callback<PaginatedList<BuyWrapper>>() {
                        @Override
                        public void success(PaginatedList<BuyWrapper> buyWrapperPaginatedList, Response response) {
                            if (buyWrapperPaginatedList != null) {
                                mLastKey = buyWrapperPaginatedList.getLastKey();
                                mAdapter.addAll(buyWrapperPaginatedList.getList());
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

    private void handleError(RetrofitError error) {
        if(error.isNetworkError()) {
            Toast.makeText(getActivity(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
        }
    }

    private void getMemberWallet(String type, String lastKey, Integer rows, Callback<PaginatedList<BuyWrapper>> callback) {
        RestAdapter restAdapter = BeRestAdapter.getMemberTokenRestAdapter();
        IMemberResource service = restAdapter.create(IMemberResource.class);

        service.getMemberWallet(MemberAuthStore.getAuth(getActivity()).getToken(), MemberAuthStore.getMember(getActivity()).getId(), type, lastKey, rows, callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_active_rewards:
                if (currentWalletType == RewardWrapper.RewardTypes.REGULAR) {
                    return true;
                }
                mLastKey = null;
                currentWalletType = RewardWrapper.RewardTypes.REGULAR;
                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true, false);
                getMemberWallet(currentWalletType.toString(), mLastKey, 10, new Callback<PaginatedList<BuyWrapper>>() {
                    @Override
                    public void success(PaginatedList<BuyWrapper> buyWrapperPaginatedList, Response response) {
                        if (buyWrapperPaginatedList != null) {
                            mAdapter.clear();
                            mLastKey = buyWrapperPaginatedList.getLastKey();
                            mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, buyWrapperPaginatedList.getList(), ProfileWalletFragment.this);
                        } else {
                            mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, new ArrayList<BuyWrapper>(), ProfileWalletFragment.this);
                        }
                        list.setAdapter(mAdapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleError(error);
                        dialog.dismiss();
                    }
                });
                return true;
            case R.id.action_archived_rewards:
                if (currentWalletType == RewardWrapper.RewardTypes.ARCHIVED) {
                    return true;
                }
                mLastKey = null;
                currentWalletType = RewardWrapper.RewardTypes.ARCHIVED;
                final ProgressDialog dialog2 = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true, false);
                getMemberWallet(currentWalletType.toString(), mLastKey, 10, new Callback<PaginatedList<BuyWrapper>>() {
                    @Override
                    public void success(PaginatedList<BuyWrapper> buyWrapperPaginatedList, Response response) {
                        if (buyWrapperPaginatedList != null) {
                            mAdapter.clear();
                            mLastKey = buyWrapperPaginatedList.getLastKey();
                            mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, buyWrapperPaginatedList.getList(), ProfileWalletFragment.this);
                        } else {
                            mAdapter = new ProfileWalletAdapter(getActivity(), list, currentWalletType, R.id.textView, new ArrayList<BuyWrapper>(), ProfileWalletFragment.this);
                        }
                        list.setAdapter(mAdapter);
                        dialog2.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleError(error);
                        dialog2.dismiss();
                    }
                });
                return true;
        }
        return true;
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ARCHIVE_CODE && resultCode == Activity.RESULT_OK) {
            if (mAdapter != null) {
                if (data.getIntExtra("wallet_position", -1) >= 0) {
                    mAdapter.removeItemAtPosition(data.getIntExtra("wallet_position", 0));
                }
            }
        }
    }
}
