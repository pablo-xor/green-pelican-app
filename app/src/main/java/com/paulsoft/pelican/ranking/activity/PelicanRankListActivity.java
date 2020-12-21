package com.paulsoft.pelican.ranking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.paulsoft.pelican.ranking.adapter.RankElementListAdapter;
import com.paulsoft.pelican.ranking.model.RankElement;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;
import com.paulsoft.pelican.ranking.service.FetchingMode;
import com.paulsoft.pelican.ranking.service.PelicanRankDataFetcherService;
import com.paulsoft.service.R;

import java.util.List;

public class PelicanRankListActivity extends AppCompatActivity {

    private ListView rankListView;
    private SpinKitView loader;
    private PreferencesRepository preferencesRepository;
    private long currentAthleteId;

    private final BroadcastReceiver rankResultFetchedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundleExtra = intent.getBundleExtra(PelicanRankDataFetcherService.EXTRA_RANK_LIST_BUNDLE);
            List<RankElement> rank = (List<RankElement>) bundleExtra.getSerializable(PelicanRankDataFetcherService.EXTRA_RANK_LIST);

            loader.setVisibility(View.INVISIBLE);
            rankListView.setVisibility(View.VISIBLE);
            rankListView.setAdapter(new RankElementListAdapter(getApplicationContext(), rank, currentAthleteId));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesRepository = new PreferencesRepository(getApplicationContext());
        currentAthleteId = preferencesRepository.load(Preference.USER_ID, Long.class, -1L);

        setContentView(R.layout.activity_pelican_rank_list);
        setSupportActionBar(findViewById(R.id.toolbar));

        rankListView = findViewById(R.id.ranking);
        loader = findViewById(R.id.loader);

        Intent serviceIntent = new Intent(this, PelicanRankDataFetcherService.class);
        serviceIntent.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.FOR_LIST);

        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_back) {
            startActivity(new Intent(this, PelicanRankMainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(rankResultFetchedReceiver, new IntentFilter(
                PelicanRankDataFetcherService.EVENT_RANK_RESULT_FETCHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(rankResultFetchedReceiver);
    }
}