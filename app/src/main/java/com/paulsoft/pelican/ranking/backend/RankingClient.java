package com.paulsoft.pelican.ranking.backend;

import com.paulsoft.pelican.ranking.model.RankElement;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RankingClient {

    @GET("stats/rankData")
    Call<List<RankElement>> getRank();

    @GET
    Call<ResponseBody> getUserIcon(@Url String iconIrl);

}
