package com.beintoo.broadcasters;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.IntentCompat;
import android.widget.RemoteViews;

import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;
import com.beintoo.utils.MemberAuthStore;

public class WidgetBedollars extends AppWidgetProvider {

    public static void updateWidgetBalance(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetBedollars.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        intent.putExtra("force_update", true);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getBooleanExtra("force_update", false)) {
            int ids[] = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if(ids != null) {
                onUpdate(context, AppWidgetManager.getInstance(context), ids);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_bedollars);

            int bedollars = 0;
            try {
                bedollars = MemberAuthStore.getMember(context).getBedollars().intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }

            views.setTextViewText(R.id.tv_qnt_bedollars, "" + bedollars);

            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("eventtype", "URL_SCHEME_LAUNCH");
            intent.putExtra("path", "/bestore");

            PendingIntent pi = PendingIntent.getActivity(context, 1234, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.rl_widget_bedollars_root, pi);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }
}
