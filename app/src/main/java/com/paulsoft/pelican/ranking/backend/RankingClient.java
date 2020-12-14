package com.paulsoft.pelican.ranking.backend;

import com.paulsoft.pelican.ranking.model.RankElement;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RankingClient {

    @GET("stats/rankData")
    Observable<List<RankElement>> getRank();

    @GET
    Observable<ResponseBody> getUserIcon(@Url String iconIrl);

}
