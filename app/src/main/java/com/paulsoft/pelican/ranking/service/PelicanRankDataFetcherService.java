package com.paulsoft.pelican.ranking.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.provider.FetchResult;
import com.paulsoft.pelican.ranking.provider.RankingRemoteProvider;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;
import com.paulsoft.service.R;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
public class PelicanRankDataFetcherService extends Service {

    public static final String PARAM_FETCHING_MODE = "fetchingMode";
    public static final String EVENT_RANK_RESULT_FETCHED = "com.paulsoft.pelican.ranking.service.RankResultFetched";
    public static final String EXTRA_RANK_LIST = "RANK_LIST";
    public static final String EXTRA_RANK_LIST_BUNDLE = "EXTRA_RANK_LIST_BUNDLE";
    public static final String CHANNEL_ID = "com.paulsoft.pelican.ranking.service.RankNotifyChannel";
    public static final int NOTIFY_ID = 1;


    private PreferencesRepository preferencesRepository;
    private RankingRemoteProvider rankingRemoteProvider;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int fetchingMode = intent.getIntExtra(PARAM_FETCHING_MODE, FetchingMode.SINGLE_CALL);

        switch (fetchingMode) {
            case FetchingMode.JOB:
                runJobAction();
                break;
            case FetchingMode.SINGLE_CALL:
                runSingleCallAction();
                break;
            default:
                throw new UnsupportedOperationException();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void runSingleCallAction() {
        rankingRemoteProvider.fetchRanking(this::publishRankFetchResult);
    }

    private void publishRankFetchResult(List<RankElement> rank) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_RANK_LIST, (Serializable) rank);

        Intent intent = new Intent(EVENT_RANK_RESULT_FETCHED);
        intent.putExtra(EXTRA_RANK_LIST_BUNDLE, bundle);
        sendBroadcast(intent);
    }

    private void runJobAction() {
        Optional<Long> loginResult = preferencesRepository.load(Preference.USER_ID, Long.class);
        loginResult.ifPresent(id -> rankingRemoteProvider.fetchRanking((rank) -> processRanking(rank, id)));
    }

    private void processRanking(List<RankElement> rankElements, Long userId) {
        Optional<RankElement> userRanking = rankElements.stream()
                .filter(el -> userId.equals(el.getAthleteId())).findAny();

        userRanking.ifPresent(this::showNotify);
    }

    @SneakyThrows
    private void showNotify(RankElement rankElement) {

        rankingRemoteProvider.loadUserImage(rankElement.getIconUrl(), result -> {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setLargeIcon(BitmapFactory.decodeByteArray(result, 0, result.length))
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentTitle("Zielony pelikan - ranking - " + rankElement.getName())
                            .setContentText(rankElement.toString())
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

            notificationManager.notify(NOTIFY_ID, builder.build());
        });
    }


    @Override
    public void onCreate() {
        preferencesRepository = new PreferencesRepository(getApplicationContext());
        rankingRemoteProvider = new RankingRemoteProvider();
        super.onCreate();
    }

}