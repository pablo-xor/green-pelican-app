package com.paulsoft.pelican.ranking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.service.FetchingMode;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;
import com.paulsoft.service.R;

import java.io.Serializable;
import java.util.List;

public class PelicanTableRankWidget extends AppWidgetProvider {

    public static final String EVENT_RANK_DATA_UPDATED = "com.paulsoft.pelican.ranking.service.RankDataUpdated";

    public void updateAppWidget(Context context, List<RankElement> rank) {

        Intent intent = new Intent(context, PelicanRankWidgetRemoteViewsService.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST, (Serializable) rank);
        intent.putExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE, bundle);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pelican_table_rank_widget);
        views.setRemoteAdapter(R.id.widgetListView, intent);

        ComponentName componentName = new ComponentName(context, PelicanTableRankWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PelicanRankDataFetcherService.EVENT_RANK_RESULT_WRAPPED_FETCHED.equals(intent.getAction())) {
            Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE);
            List<RankElement> rank = (List<RankElement>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);

            int fetchingMode = intent.getIntExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.FOR_WIDGET);

            switch (fetchingMode) {
                case FetchingMode.FOR_WIDGET:
                    updateAppWidget(context, rank);
                    break;
                case FetchingMode.JOB:
                    updateWidgetData(context, rank);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        super.onReceive(context, intent);
    }

    private void updateWidgetData(Context context, List<RankElement> rank) {

        Intent broadcastIntent = new Intent(EVENT_RANK_DATA_UPDATED);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST, (Serializable) rank);
        broadcastIntent.putExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE, bundle);
        context.sendBroadcast(broadcastIntent);

        ComponentName componentName = new ComponentName(context, PelicanTableRankWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
    }

    @Override
    public void onEnabled(Context context) {
        Intent serviceIntent = new Intent(context, PelicanRankDataFetcherService.class);
        serviceIntent.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.FOR_WIDGET);
        context.startService(serviceIntent);
    }

    @Override
    public void onDisabled(Context context) {
    }
}