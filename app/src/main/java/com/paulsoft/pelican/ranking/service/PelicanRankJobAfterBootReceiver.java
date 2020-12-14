package com.paulsoft.pelican.ranking.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paulsoft.pelican.ranking.commons.RankJobScheduleInfo;
import com.paulsoft.pelican.ranking.repository.Preference;
import com.paulsoft.pelican.ranking.repository.PreferencesRepository;

import java.util.Optional;

public class PelicanRankJobAfterBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Optional<Long> login = new PreferencesRepository(context).load(Preference.USER_ID, Long.class);

        if(login.isPresent()) {
            JobInfo jobInfo = RankJobScheduleInfo.create(context);
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(jobInfo);
        }

    }
}