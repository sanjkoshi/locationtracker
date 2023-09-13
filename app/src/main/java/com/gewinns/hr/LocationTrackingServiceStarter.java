package com.gewinns.hr;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationTrackingServiceStarter extends BroadcastReceiver {
    private static final int PERMISSION_REQUEST_CODE = 124;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the LocationTrackingService is already running
        Log.i("LocationService","broadcast service received");
        if (!isLocationTrackingServiceRunning(context, LocationTrackingService.class)) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(context, LocationTrackingService.class);
                context.startService(serviceIntent);
           } //else {
//                String[] permissions={android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION};
//                //ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//                Intent serviceIntent = new Intent(context, LocationTrackingService.class);
//                context.startService(serviceIntent);
//            }
            // LocationTrackingService is not running, start it

        }
    }

    public static void setAlarm(Context context) {
        // Set the alarm to trigger after 15 minutes (900,000 milliseconds)
        long triggerTimeMillis = SystemClock.elapsedRealtime() + 15 * 60 * 1000;

        // Create an Intent for this BroadcastReceiver
        Intent intent = new Intent(context, LocationTrackingServiceStarter.class);

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Get the AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to trigger once, starting from the specified time
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTimeMillis, pendingIntent);
    }

    // Helper method to check if a service is running
    private static boolean isLocationTrackingServiceRunning(Context context, Class<?> serviceClass) {
        Intent serviceIntent = new Intent(context, serviceClass);
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                ? context.startForegroundService(serviceIntent) != null
                : context.startService(serviceIntent) != null;
    }
}

