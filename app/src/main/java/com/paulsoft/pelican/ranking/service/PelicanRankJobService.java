package com.paulsoft.pelican.ranking.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import com.paulsoft.pelican.ranking.commons.RankJobScheduleInfo;

public class PelicanRankJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = getApplicationContext();
        Intent service = new Intent(context, PelicanRankDataFetcherService.class);
        service.putExtra(PelicanRankDataFetcherService.PARAM_FETCHING_MODE, FetchingMode.JOB);
        context.startService(service);

        JobInfo jobInfo = RankJobScheduleInfo.create(context);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(jobInfo);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
