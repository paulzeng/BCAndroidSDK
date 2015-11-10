package com.beintoo.asynctasks;

import android.os.AsyncTask;
import android.widget.ListView;


public class CollectiveFacebookFriendLoaderTask extends AsyncTask<Void, Void, Void>{

    private ListView mListView;

    public CollectiveFacebookFriendLoaderTask(ListView listView) {
        mListView = listView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
