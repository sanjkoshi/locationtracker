package com.gewinns.hr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import com.gewinns.hr.LocationTrackingService;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.gewinns.hr.Employee;
import com.google.gson.JsonParser;

public class SubmitMobileForm extends AppCompatActivity {
//    private static final int PERMISSION_REQUEST_CODE = 123;
    private EditText mobileNumberEditText;
    private Button submitButton;
    private TextView instructionText;
    private EditText otpEditText;
    private Button otpVerify;
//    private Button startButton;
//    private Button endButton;
//    private TextView time;
//    private TextView latitude;
//    private TextView longitude;
    private static RequestQueue requestQueue;
   // private LocationTrackingService loc;
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationService();
//            }
//        }
//    }
//    private void requestLocationPermissionsAndStartService() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            startLocationService();
//        } else {
//            String[] permissions={android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION};
//            //ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//            startActivity(intent);
//        }
//    }

//    private void startLocationService() {
//        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
//        ContextCompat.startForegroundService(this, serviceIntent);
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_mobile_form);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        submitButton = findViewById(R.id.submitButton);
        instructionText = findViewById(R.id.otpInstruction);
        otpEditText = findViewById(R.id.otpEditText);
        otpVerify = findViewById(R.id.submitOTP);
//        time=findViewById(R.id.timeEditText);
//        latitude=findViewById(R.id.latitudeEditText);
//        longitude=findViewById(R.id.longitudeEditText);
//        startButton=findViewById(R.id.startButton);
        //loc=new LocationTrackingService();
        requestQueue = Volley.newRequestQueue(this);
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       // Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        Intent next=new Intent(SubmitMobileForm.this,LocationTrackingActivity.class );


//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                requestLocationPermissionsAndStartService();
//            }
//        });

        otpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = mobileNumberEditText.getText().toString();
                String otp=otpEditText.getText().toString();
                String subdomain = sharedPreferences.getString("subdomain", "");
                String url= String.format("https://hr.gewinns.com/api/method/gewin_payroll.api.verify_otp_new?phone=%s&otp=%s&subdomain=%s",mobileNumber,otp,subdomain);
                StringRequest stringRequest = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d("ApiResponse", response);
                                if (response.equals("verified")){
                                    editor.putString("phone",mobileNumber);
                                    editor.apply();
                                   // requestLocationPermissionsAndStartService();
                                    startActivity(next);

                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ApiError", error.toString());
                            }
                        });

                requestQueue.add(stringRequest);

                // Once you receive a response, show the OTP EditText
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String mobileNumber = mobileNumberEditText.getText().toString();

                String url = String.format("https://hr.gewinns.com/api/method/gewin_payroll.api.install_app?phone=%s&id=1234",mobileNumber);

                StringRequest stringRequest = new StringRequest(url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                Employee emp=gson.fromJson(response,Employee.class);
                                editor.putString("employee", emp.getEmployee_id());
                                editor.putString("subdomain",emp.getSubdomain());
                                editor.apply();
                                Log.d("ApiResponse", emp.getEmployee_id());
                                Log.d("ApiResponse", emp.getSubdomain());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ApiError", error.toString());
                            }
                        });

                requestQueue.add(stringRequest);

                instructionText.setVisibility(View.VISIBLE);
                otpEditText.setVisibility(View.VISIBLE);
                otpVerify.setVisibility(View.VISIBLE);
            }
        });
    }
}

