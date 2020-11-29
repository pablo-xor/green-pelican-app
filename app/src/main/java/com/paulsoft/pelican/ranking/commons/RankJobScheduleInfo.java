package com.paulsoft.pelican.ranking.commons;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.paulsoft.pelican.ranking.service.PelicanRankJobService;

public class RankJobScheduleInfo {

    public static final int SECOND = 1000;

    public static JobInfo create(Context context) {
        ComponentName serviceComponent = new ComponentName(context, PelicanRankJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(true); //Android 9 :)
        }

        builder.setMinimumLatency(5 * 60 * SECOND);
        builder.setOverrideDeadline(10 * 60 * SECOND);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        return builder.build();
    }

}
