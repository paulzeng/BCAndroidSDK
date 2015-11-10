package com.beintoo.social;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.plus.PlusShare;

public class GPlusManager {
    public static void shareStatus(Activity activity, String text, String url){

        Intent shareIntent = new PlusShare.Builder(activity)
                .setType("text/plain")
                .setText(text + " " + Uri.parse(url)  )
                .getIntent();

        activity.startActivityForResult(shareIntent, 0);
    }
}