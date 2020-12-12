package com.paulsoft.pelican.ranking.widget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViewsService;

import com.paulsoft.pelican.ranking.model.RankElementWrapper;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;

import java.util.List;

public class PelicanRankWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_EXTENDED_BUNDLE);
        List<RankElementWrapper> wrappedRank = (List<RankElementWrapper>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);
        return new PelicanRankWidgetViewsFactory(this.getApplicationContext(), wrappedRank);
    }
}
