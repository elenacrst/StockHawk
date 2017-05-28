package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.details.DetailsActivity;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

public class StockWidgetProvider extends AppWidgetProvider{


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_initial);
            remoteViews.setOnClickPendingIntent(R.id.container, pIntent);

            Intent widgetIntent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);//todo 64 comment t3 lines
            widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            context.startService(widgetIntent);

            remoteViews.setRemoteAdapter( R.id.list, widgetIntent);

            Intent mainIntent = new Intent(context, MainActivity.class);
             PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.title_widget, pendingIntent);
            Intent detailIntent = new Intent(context, DetailsActivity.class);
             PendingIntent pendingIntentDetail = PendingIntent.getActivity(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
          remoteViews.setPendingIntentTemplate(R.id.list, pendingIntentDetail);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.list);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(QuoteSyncJob.ACTION_DATA_UPDATED)) {
            AppWidgetManager am = AppWidgetManager.getInstance(context);
            int [] appWidgetIds = am.getAppWidgetIds(new ComponentName(context, StockWidgetProvider.class));
            am.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list);
        }
        super.onReceive(context, intent);
    }
}
