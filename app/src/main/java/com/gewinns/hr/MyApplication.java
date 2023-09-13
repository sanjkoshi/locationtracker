package com.gewinns.hr;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your application here.
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.w("LocationService","terminating application sanjay");
        // Broadcast that the app's process is terminating.
        Intent intent = new Intent("com.gewinns.hr.PROCESS_TERMINATED");
        sendBroadcast(intent);
    }
}
