package com.paulsoft.pelican.ranking.widget;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.model.RankElementWrapper;
import com.paulsoft.service.R;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PelicanRankWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    private final Context context;
    private final List<RankElementWrapper> rankList;

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

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

        RankElementWrapper rankItem = rankList.get(position);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.table_rank_row);

        if(rankItem.hasUserAvatar()) {
            rv.setBitmap(R.id.userAvatar, null, rankItem.getIcon());
        }

        RankElement rankElement = rankItem.getRankElement();
        rv.setTextViewText(R.id.place, rankElement.getPlace().toString());
        rv.setTextViewText(R.id.login, rankElement.getName());
        rv.setTextViewText(R.id.points, rankElement.getTotal() + " pts");
        rv.setTextViewText(R.id.ridding, rankElement.getRide() + " km");
        rv.setTextViewText(R.id.running, rankElement.getRun() + " km");

        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return rankList.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
