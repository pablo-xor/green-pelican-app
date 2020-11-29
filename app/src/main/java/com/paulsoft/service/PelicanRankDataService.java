package com.paulsoft.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PelicanRankDataService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        Log.i("TEST", "On create...");

        super.onCreate();
    }
}