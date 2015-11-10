package com.beintoo.utils;

import android.widget.AbsListView;

public class EndlessScrollListener implements AbsListView.OnScrollListener {


    public interface EndlessCallback{
        public void OnLoadNeeded();
    }

    private int visibleThreshold = 5;
    private int previousTotal = 0;
    public boolean loading = true;
    private EndlessCallback mCallback;

    public EndlessScrollListener() {

    }
    public EndlessScrollListener(int visibleThreshold, EndlessCallback callback) {
        this.visibleThreshold = visibleThreshold;
        this.mCallback = callback;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
            loading = true;
            mCallback.OnLoadNeeded();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public void resetListener(){
        previousTotal = 0;
        loading = true;
    }
}
