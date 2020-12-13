package com.paulsoft.pelican.ranking.provider;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paulsoft.pelican.ranking.backend.RankingClient;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.service.BuildConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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

    @SuppressLint("CheckResult")
    public void fetchRanking(FetchResult<List<RankElement>> rankingFetchResult) {
        rankingClient.getRank()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rankingFetchResult::afterFetched, this::onError);
    }

    public void loadUserImage(String iconUrl, FetchResult<InputStream> iconLoadedResult) {
        rankingClient.getUserIcon(iconUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(el -> el.byteStream())
                .subscribe(iconLoadedResult::afterFetched, this::onError);
    }

    public void loadUserImages(List<RankElement> iconUrls, FetchResult<List<Pair<Long, InputStream>>> iconsFetchResult) {
        List<Observable<ResponseBody>> calls = new ArrayList<>();

        iconUrls.forEach(el -> {
            calls.add(rankingClient.getUserIcon(el.getIconUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
        });

        Observable.zip(calls, res -> convertToIconStreams(iconUrls, res))
                .subscribe(iconsFetchResult::afterFetched, this::onError);
    }

    private List<Pair<Long, InputStream>> convertToIconStreams(List<RankElement> urls, Object[] objects) {
        List<Pair<Long, InputStream>> icons = new ArrayList<>(objects.length);

        for (int i = 0; i < urls.size(); i++) {
            icons.add(Pair.create(urls.get(i).getAthleteId(), ((ResponseBody)objects[i]).byteStream()));
        }

        return icons;
    }

    private void buildRankingClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONN_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor((chain) -> {
                    Request.Builder ongoing = chain.request().newBuilder();
                    ongoing.addHeader(AUTH_HEADER, API_SECRET);
                    return chain.proceed(ongoing.build());
                });

        if(BuildConfig.DEBUG) {
            builder.addInterceptor(logging);
        }

        OkHttpClient httpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BACKEND_API_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        rankingClient = retrofit.create(RankingClient.class);
    }

    private void onError(Throwable e) {
        Log.e(getClass().getSimpleName(), "Err: " + e.getClass().getSimpleName() + ":" + e.getMessage(), e);
    }
}
