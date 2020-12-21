package com.paulsoft.pelican.ranking.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.paulsoft.pelican.ranking.commons.ImageCache;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;
import com.paulsoft.service.R;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PelicanRankWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final List<RankElement> rankList;
    private long currentAthleteId;

    private final BroadcastReceiver dataChangedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE);
            List<RankElement> rankElements = (List<RankElement>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);
            rankList.clear();
            rankList.addAll(rankElements);
        }
    };

    @Override
    public void onCreate() {
        currentAthleteId = new PreferencesRepository(context).load(Preference.USER_ID, Long.class, -1L);

        IntentFilter filter = new IntentFilter();
        filter.addAction(PelicanTableRankWidget.EVENT_RANK_DATA_UPDATED);
        context.registerReceiver(dataChangedBroadcast, filter);
    }

    @Override
    public void onDataSetChanged() {


    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(dataChangedBroadcast);
    }

    @Override
    public int getCount() {
        return rankList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RankElement rankElement = rankList.get(position);
        Log.d("PelicanRankWidgetViewsFactory", "Rendering row for: " + rankElement);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.table_rank_row);

        Bitmap avatar = ImageCache.get(rankElement.getAthleteId());

        if(Objects.nonNull(avatar)) {
            rv.setImageViewBitmap(R.id.userAvatar, avatar);
        }

        if(rankElement.getAthleteId().equals(currentAthleteId)) {
            rv.setTextColor(R.id.login, context.getResources().getColor(R.color.colorGreen));
        }

        rv.setTextViewText(R.id.place, rankElement.getPlace().toString());
        rv.setTextViewText(R.id.login, rankElement.getName());
        rv.setTextViewText(R.id.points, rankElement.getTotal() + " pts");
        rv.setTextViewText(R.id.ridding, rankElement.getRide() + " km");
        rv.setTextViewText(R.id.running, rankElement.getRun() + " km");

        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.table_rank_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return rankList.get(position).getAthleteId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
