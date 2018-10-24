package com.example.geosqliteassignment.utiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class MyStartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (GpsUtils.isConnectingToInternet(context)) {
            NetworkSchedule.scheduleJob(context);
        } else {
            NetworkSchedule.finishScheduleJob(context);
        }


    }
}
