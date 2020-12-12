package com.paulsoft.pelican.ranking.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.paulsoft.pelican.ranking.model.RankElementWrapper;
import com.paulsoft.pelican.ranking.service.FetchingMode;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;
import com.paulsoft.service.R;

import java.io.Serializable;
import java.util.List;

public class PelicanTableRankWidget extends AppWidgetProvider {

    public static final String TAG = "PelicanTableRankWidget";

    public void updateAppWidget(Context context, List<RankElementWrapper> rank) {

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
        if(PelicanRankDataFetcherService.EVENT_RANK_RESULT_WRAPPED_FETCHED.equals(intent.getAction())) {
            Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE);
            List<RankElementWrapper> rankWrapper = (List<RankElementWrapper>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);
            updateAppWidget(context, rankWrapper);
        }

        super.onReceive(context, intent);
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