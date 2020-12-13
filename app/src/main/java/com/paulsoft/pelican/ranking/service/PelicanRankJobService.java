package com.paulsoft.pelican.ranking.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.paulsoft.pelican.ranking.commons.NetworkManager;
import com.paulsoft.pelican.ranking.commons.RankJobScheduleInfo;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;

public class PelicanRankJobService extends JobService {

    private PreferencesRepository preferencesRepository;

    private void recoverJobExecutionAfterNetIsEnabled(Context context) {
        Boolean isJobWaiting = preferencesRepository.load(Preference.JOB_WAITING, Boolean.class, Boolean.FALSE);

        if (NetworkManager.isNetworkEnabled(context) && isJobWaiting) {
            Log.d(this.getClass().getSimpleName(), "Enable job after network is switch on...");

            JobInfo jobInfo = RankJobScheduleInfo.create(context);
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(jobInfo);
            preferencesRepository.save(Preference.JOB_WAITING, Boolean.class, Boolean.FALSE);
        }
    }

    @Override
    public void onCreate() {
        Context applicationContext = getApplicationContext();
        preferencesRepository = new PreferencesRepository(applicationContext);
        NetworkManager.registerListener(applicationContext, () -> recoverJobExecutionAfterNetIsEnabled(applicationContext));
        super.onCreate();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = getApplicationContext();
        Intent service = new Intent(context, PelicanRankDataFetcherService.class);
        service.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.JOB);
        context.startService(service);

        if (NetworkManager.isNetworkEnabled(context)) {
            JobInfo jobInfo = RankJobScheduleInfo.create(context);
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(jobInfo);
            return true;
        } else {
            preferencesRepository.save(Preference.JOB_WAITING, Boolean.class, Boolean.TRUE);
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
