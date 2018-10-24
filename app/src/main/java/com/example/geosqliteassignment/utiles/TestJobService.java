package com.example.geosqliteassignment.utiles;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.geosqliteassignment.activities.MainActivity;

public class TestJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent =new Intent("get_Data_from_db");
        localBroadcastManager.sendBroadcast(intent);
        NetworkSchedule.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
