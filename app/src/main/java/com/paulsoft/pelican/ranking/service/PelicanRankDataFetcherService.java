package com.paulsoft.pelican.ranking.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.paulsoft.pelican.ranking.backend.RankingFetchResult;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.provider.RankingRemoteProvider;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;

import java.util.List;
import java.util.Optional;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PelicanRankDataFetcherService extends Service {

    private PreferencesRepository preferencesRepository;
    private RankingRemoteProvider rankingRemoteProvider;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Optional<String> loginResult = preferencesRepository.load(Preference.LOGIN, String.class);
        loginResult.ifPresent(login -> rankingRemoteProvider.fetchRanking((rank) -> processRanking(rank, login)));
        return super.onStartCommand(intent, flags, startId);
    }

    private void processRanking(List<RankElement> rankElements, String currentLogin) {

        Optional<RankElement> userRanking = rankElements.stream()
                .filter(el -> currentLogin.equals(el.getLogin())).findAny();

        //TODO

    }

    @Override
    public void onCreate() {
        preferencesRepository = new PreferencesRepository(getApplicationContext());
        rankingRemoteProvider = new RankingRemoteProvider();
        super.onCreate();
    }

}