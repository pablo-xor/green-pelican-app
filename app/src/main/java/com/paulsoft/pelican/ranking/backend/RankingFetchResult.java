package com.paulsoft.pelican.ranking.backend;

import com.paulsoft.pelican.ranking.model.RankElement;

import java.util.List;

public interface RankingFetchResult {

    void afterRankingLoaded(List<RankElement> ranking);

}
