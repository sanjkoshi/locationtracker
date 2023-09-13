package com.gewinns.hr;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
//import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Build;
//import android.os.Bundle;
import android.os.IBinder;
//import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

//import org.json.JSONException;
//import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocationTrackingService extends Service {

    private static final String TAG = "LocationService";
    private MyReceiver receiver;
    private static final int NOTIFICATION_ID = 551;
    private static final String ACTION_STOP_SERVICE = "com.gewinns.hr.ACTION_STOP_SERVICE";
    public static final String ACTION_START_SERVICE = "com.gewinns.hr.START_SERVICE";
    public static final String ACTION_START_LOCATION_SERVICE="com.gewinns.hr.ACTION_START_LOCATION_SERVICE";
    //public static final String TAG="com.gewinns.hr";
    private static final float MOVEMENT_THRESHOLD_METERS = 12;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "LocationChannel";
    private static RequestQueue requestQueue;
    private Location previousLocation;
    private Integer count = 0;
    private Intent stopIntent;
    private PendingIntent stopPendingIntent;
    private Intent startIntent;
    private PendingIntent startPendingIntent;
    private Long frequency;




    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("com.gewinns.hr","starting service");
        count = 0;
        //frequency= Long.valueOf(20000);
        stopIntent = new Intent(this, LocationTrackingService.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE);
        startIntent = new Intent(this, LocationTrackingService.class);
        startIntent.setAction(LocationTrackingService.ACTION_START_SERVICE);
        startPendingIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_MUTABLE);
        //Intent alarmIntent = new Intent("YOUR_ALARM_ACTION");
        Intent bcastIntent=new Intent(this,LocationTrackingServiceStarter.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,100,bcastIntent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE) ;
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.add(Calendar.SECOND, 60);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time.getTimeInMillis(),pi);
        Log.d(TAG,"Alarm scheduled");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("on create test","at line 88");
        createLocationRequest();
        createNotificationChannel();
        buildNotification("creating notification");
        previousLocation = null;
        requestQueue = Volley.newRequestQueue(this);
//        receiver = new MyReceiver();
//        IntentFilter filter = new IntentFilter("com.gewinns.hr.PROCESS_TERMINATED");
//        registerReceiver(receiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(LocationTrackingService.ACTION_START_SERVICE)) {
                    count = 0;
                    // Handle starting the service, if needed
                } else if (action.equals(LocationTrackingService.ACTION_STOP_SERVICE)) {
                    stopForeground(true);
                    stopSelf();
                    Log.d(TAG,"stopping location service sanjay");
                    return START_STICKY;
                }
            }
        }
        // ... other code ...
        startForeground(1, buildNotification("starting notification"));

        requestLocationUpdates();
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("com.gewinns.hr","service destroyed sanjay");
        stopForeground(true);
        unregisterReceiver(receiver);
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    public void onPause() {
        Log.i("com.gewinns.hr","service on pause sanjay");

    }

    public void onStop(){

    }
//    @Override
//    public void () {
//        super.checkUriPermission();
//        fusedLocationClient.removeLocationUpdates(locationCallback);
//    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void updateLocationRequest(Long interval) {
        Log.d("com.gewinns.com", "updating location");
        //fusedLocationClient.removeLocationUpdates(locationCallback);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(interval);
        //locationRequest.setSmallestDisplacement(7);
        // Set other LocationRequest properties as needed
    }

    // Method to start requesting location updates with the updated LocationRequest
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.i("Permission Check1", Manifest.permission.ACCESS_FINE_LOCATION.toString());
            Log.i("Permission Check2", Manifest.permission.ACCESS_COARSE_LOCATION.toString());
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private Notification buildNotification(String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Service")
                .setContentText(text)
                .setSound(null)
                .setPriority(2)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
            builder.setPriority(2);
        }
        Log.d(TAG,"inside build notification");
        return builder.build();
    }

    private void updateNotification(String notificationText) {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Concatenate the time and location information for the notification content
        String combinedText = "Time: " + currentTime + "\n" + notificationText;
        // Create a PendingIntent for the stop button
//        Intent stopIntent = new Intent(this, LocationTrackingService.class);
//        stopIntent.setAction(ACTION_STOP_SERVICE);
//        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE);
//        Intent startIntent = new Intent(this, LocationTrackingService.class);
//        startIntent.setAction(LocationTrackingService.ACTION_START_SERVICE);
//        PendingIntent startPendingIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_MUTABLE);
//        // Build the notification with stop button action
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Location Tracking Service")
                .setContentText(combinedText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(combinedText))
                .setSound(null);
                //.addAction(R.drawable.ic_notifications_black_24dp, "Start Service", startPendingIntent)
                //.addAction(R.drawable.ic_notifications_black_24dp, "Stop Service", stopPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(7);
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Log.d(TAG, "Location received: " + locationResult.getLastLocation());
                    Location location = locationResult.getLastLocation();

                    count=count+1;
                    if (count==3){}
                    //boolean isCurrentlyMoving = isMoving(location, previousLocation);
                    //if (isCurrentlyMoving) {
                        String speed= String.valueOf(location.getSpeed());
                        String locstr= location.toString();
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        String employee = sharedPreferences.getString("employee", "");
                        String subdomain = sharedPreferences.getString("subdomain", "");
                        String phone = sharedPreferences.getString("phone", "");
                        
                        String currentTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        //final Uri url = Uri.parse('https://hr.gewinns.com/api/method/gewin_payroll.api.addLocation?employee=$employee&subdomain=$subdomain&location=${location.toString()}&phone=$phone&timestamp=$timestampString');
                        //String url = String.format("https://hr.gewinns.com/api/method/gewin_payroll.api.addLocation?employee=%s&subdomain=%s&latitude=%s&longitude=%s&phone=%s&timestamp=%s&locstr=%s", employee, subdomain, latitude, longitude, phone, currentTime,locstr);
                        String url = String.format("https://hr.gewinns.com/api/method/gewin_payroll.api.addLocation_test?employee=%s&subdomain=%s&latitude=%s&longitude=%s&phone=%s&timestamp=%s&locstr=%s", employee, subdomain, latitude, longitude, phone, currentTime,locstr);

                    Log.d(TAG, url);
                        StringRequest stringRequest = new StringRequest(url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.d(TAG, response);
                                        try {
                                            String currentTime1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                            frequency=Long.valueOf(response);
                                            Log.d(TAG, String.valueOf(frequency));
                                            updateLocationRequest(frequency);
                                            startLocationUpdates();
                                            Log.d(TAG, String.valueOf(locationRequest));
                                            String notificationText = "Lt: " + String.valueOf(location.getLatitude()) + ",Lg: " + String.valueOf(location.getLongitude()) + ",f:"+ String.valueOf(frequency);
                                            String combinedText="T: " + currentTime1 + "\n" + notificationText;
                                            Log.d(TAG,combinedText);
                                            //updateNotification(notificationText);
//                                            stopForeground(true);
                                          // startForeground(1, buildNotification(combinedText));
//                                            Log.d(TAG,"foreground started");

                                        } catch (
                                                NumberFormatException e) {
                                            frequency=60000L;
                                            throw new RuntimeException(e);
                                        }


                                                                           }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(TAG, error.toString());
                                    }
                                });
//                        String notificationText = "Lat: " + String.valueOf(location.getLatitude()) + ", Lng: " + String.valueOf(location.getLongitude()) + ",frq:"+ String.valueOf(frequency);
//                        String combinedText="Time: " + currentTime + "\n" +notificationText;
//                        Log.d(TAG,combinedText);
//                    //updateNotification(notificationText);
//                    startForeground(1, buildNotification(combinedText));
//                    Log.d(TAG,"foreground started");
                        requestQueue.add(stringRequest);
                    Log.d(TAG,"url added to queue");
                }
            };
//            if (frequency != null){
//            locationRequest.setInterval(frequency);}
            //fusedlocationclient.
            Log.d(TAG,"before location request update");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d(TAG,"after location request update");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(serviceChannel);
        }
    }
}
