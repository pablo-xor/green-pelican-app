package com.paulsoft.pelican.ranking.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.provider.RankingRemoteProvider;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;
import com.paulsoft.service.R;

import java.io.Serializable;
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
    public static final int SUMMARY_NOTIFY_ID = 1;
    public static final String GREEN_PELICAN_LABEL = "Zielony pelikan - ranking";
    public static final String PLACE_UP_TEXT = "Gratulacje! Jesteś aktualnie na %d miejscu";
    public static final String PLACE_DOWN_TEXT = "Spadłeś na miejsce %d :(";
    public static final int PLACE_CHANGED_NOTIFY_ID = 2;


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

        userRanking.ifPresent((el) -> {
            showSummaryNotify(el);
            showNotifyIfPlaceChanged(el);
        });
    }

    private void showNotifyIfPlaceChanged(RankElement el) {
        Optional<Integer> lastPlace = preferencesRepository.load(Preference.LAST_RANK, Integer.class);

        if (lastPlace.isPresent()) {

            if (!el.getPlace().equals(lastPlace.get())) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(PLACE_CHANGED_NOTIFY_ID, buildPlaceNotification(el.getPlace(), lastPlace.get()));
            }

        }

        preferencesRepository.save(Preference.LAST_RANK, Integer.class, el.getPlace());

    }

    private Notification buildPlaceNotification(Integer actualPlace, Integer lastPlace) {

        boolean isHigherPlace = actualPlace < lastPlace;

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(GREEN_PELICAN_LABEL)
                .setColorized(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(String.format(isHigherPlace ? PLACE_UP_TEXT : PLACE_DOWN_TEXT, actualPlace))
                .setColor(getColor(isHigherPlace ? R.color.colorGreen : R.color.colorRed))
                .setPriority(NotificationCompat.PRIORITY_HIGH).build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PelicanNotifyChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SneakyThrows
    private void showSummaryNotify(RankElement rankElement) {
        rankingRemoteProvider.loadUserImage(rankElement.getIconUrl(), result -> {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(SUMMARY_NOTIFY_ID, buildSummaryNotification(rankElement, result));
        });
    }

    private Notification buildSummaryNotification(RankElement rankElement, byte[] result) {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeByteArray(result, 0, result.length))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Miejsce: " + rankElement.getPlace())
                .setColorized(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setColor(getColor(R.color.colorOrange))
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Jazda rowerem: " + rankElement.getRide() + "km")
                        .addLine("Bieganie: " + rankElement.getRun() + "km")
                        .addLine("Suma: " + rankElement.getTotal() + "km"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
    }


    @Override
    public void onCreate() {
        preferencesRepository = new PreferencesRepository(getApplicationContext());
        rankingRemoteProvider = new RankingRemoteProvider();
        createNotificationChannel();
        super.onCreate();
    }

}