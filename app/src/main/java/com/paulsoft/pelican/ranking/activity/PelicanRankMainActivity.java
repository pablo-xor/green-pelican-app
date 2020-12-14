/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paulsoft.pelican.ranking.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.paulsoft.pelican.ranking.commons.NetworkManager;
import com.paulsoft.pelican.ranking.commons.RankJobScheduleInfo;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.model.UserDto;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;
import com.paulsoft.pelican.ranking.service.FetchingMode;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;
import com.paulsoft.service.R;

import java.util.List;
import java.util.Optional;

public class PelicanRankMainActivity extends AppCompatActivity {

    private static final String TAG = "PelicanMainActivity";
    public static final String EMPTY_TEXT = "";
    public static final String EVENT_ANDROID_NET_STATE_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
    private PreferencesRepository preferencesRepository;
    private Spinner userSelector;
    private UserSpinnerAdapter userSpinnerAdapter;
    private Optional<Long> currentUserId;

    private final BroadcastReceiver rankResultFetchedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_BUNDLE);
            List<RankElement> rank = (List<RankElement>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);

            Log.d(TAG, "Rank received:" + rank);

            userSpinnerAdapter.updateData(rank);

            if (currentUserId.isPresent()) {
                userSelector.setSelection(userSpinnerAdapter.getPosition(currentUserId.get()));
            }

        }
    };

    private final BroadcastReceiver networkStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadRankDataIfNetworkEnabled();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(rankResultFetchedReceiver, new IntentFilter(
                PelicanRankDataFetcherService.EVENT_RANK_RESULT_FETCHED));
        registerReceiver(networkStateChangedReceiver, new IntentFilter(EVENT_ANDROID_NET_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(rankResultFetchedReceiver);
        unregisterReceiver(networkStateChangedReceiver);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        preferencesRepository = new PreferencesRepository(this);
        currentUserId = preferencesRepository.load(Preference.USER_ID, Long.class);

        userSelector = findViewById(R.id.user_selector);

        userSpinnerAdapter = new UserSpinnerAdapter(getApplicationContext());
        userSelector.setAdapter(userSpinnerAdapter);

        loadRankDataIfNetworkEnabled();
    }

    private void loadRankDataIfNetworkEnabled() {
        if (NetworkManager.isNetworkEnabled(getApplicationContext())) {
            Intent serviceIntent = new Intent(this, PelicanRankDataFetcherService.class);
            serviceIntent.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.SINGLE_CALL);
            startService(serviceIntent);
        }
    }

    public void startApp(View view) {
        UserDto selectedItem = (UserDto) userSelector.getSelectedItem();
        preferencesRepository.save(Preference.USER_ID, Long.class, selectedItem.getUserId());

        boolean userChanged = currentUserId.isPresent() && currentUserId.get() != selectedItem.getUserId();

        Context context = getApplicationContext();
        JobInfo jobInfo = RankJobScheduleInfo.create(context);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(jobInfo);

        Intent serviceIntent = new Intent(this, PelicanRankDataFetcherService.class);
        serviceIntent.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.JOB);
        serviceIntent.putExtra(PelicanRankDataFetcherService.PARAM_USER_CHANGED, userChanged);

        startService(serviceIntent);

        finish();
    }

}
