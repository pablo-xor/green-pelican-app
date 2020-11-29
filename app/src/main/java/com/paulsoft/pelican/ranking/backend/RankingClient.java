package com.paulsoft.pelican.ranking.backend;

import com.paulsoft.pelican.ranking.model.RankElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RankingClient {

    @GET("stats/rankData")
    Call<List<RankElement>> getRank();

}
