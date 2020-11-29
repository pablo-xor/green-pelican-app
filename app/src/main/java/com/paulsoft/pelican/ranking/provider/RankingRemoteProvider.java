package com.paulsoft.pelican.ranking.provider;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulsoft.pelican.ranking.backend.RankingClient;
import com.paulsoft.pelican.ranking.backend.RankingFetchResult;
import com.paulsoft.pelican.ranking.model.RankElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankingRemoteProvider {

    public static final String BACKEND_API_URL = "https://zielonypelikan-ranking.nt4.pl/api/";
    public static final String API_SECRET = "androidwfKZYYehNZJBklIBFqrr";
    public static final String AUTH_HEADER = "auth";
    public static final int CONN_TIMEOUT = 5;
    public static final int READ_TIMEOUT = 5;
    private RankingClient rankingClient;

    public RankingRemoteProvider() {
        buildRankingClient();
    }

    public void fetchRanking(RankingFetchResult rankingFetchResult) {
        Call<List<RankElement>> rankCall = rankingClient.getRank();
        rankCall.enqueue(new RankingCallbackResultWrapper(rankingFetchResult));
    }

    private void buildRankingClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor((chain) -> {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader(AUTH_HEADER, API_SECRET);
                    return chain.proceed(ongoing.build());
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BACKEND_API_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        rankingClient = retrofit.create(RankingClient.class);
    }

    @AllArgsConstructor
    private static class RankingCallbackResultWrapper implements Callback<List<RankElement>>{

        private RankingFetchResult rankingFetchResult;

        @Override
        public void onResponse(Call<List<RankElement>> call, Response<List<RankElement>> response) {
            List<RankElement> ranking = response.body();
            Log.d(getClass().getSimpleName(), "Result: " + ranking);
            rankingFetchResult.afterRankingLoaded(ranking);
        }

        @Override
        public void onFailure(Call<List<RankElement>> call, Throwable t) {
            Log.e(getClass().getSimpleName(), "Err: " + t.getClass().getSimpleName() + ":" + t.getMessage());
        }
    }
}
