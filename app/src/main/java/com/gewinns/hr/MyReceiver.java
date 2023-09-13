package com.gewinns.hr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.gewinns.hr.PROCESS_TERMINATED".equals(intent.getAction())) {
            // Handle the process termination event here.
            Log.d("LocationService", "App's process has terminated.");
        }
    }
}