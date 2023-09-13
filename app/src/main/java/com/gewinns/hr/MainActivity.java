package com.gewinns.hr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.gewinns.hr.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private CheckBox consentCheckbox;
    private Button continueButton;
    private String consent;
    private String phone;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        consentCheckbox = findViewById(R.id.consentCheckbox);
        continueButton = findViewById(R.id.continueButton);
        Intent next=new Intent(MainActivity.this,SubmitMobileForm.class );
        Intent next1=new Intent(MainActivity.this,LocationTrackingActivity.class );
        consent = sharedPreferences.getString("consent", "");
        phone=sharedPreferences.getString("phone", "");
        Log.i("App Message",consent);
        if (consent.equals("YES")){
            Log.i("phone", String.valueOf(phone.length()));
            if (phone.length()>0){
                startActivity(next1);
            } else { startActivity(next);}
        }

        //String username = sharedPreferences.getString("username", "");
        consentCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle checkbox click
                if (consentCheckbox.isChecked()) {
                    // Checkbox is checked, perform actions
                    continueButton.setVisibility(v.VISIBLE);
                    consent="YES";
                } else {
                    // Checkbox is unchecked, handle accordingly
                    continueButton.setVisibility(v.GONE);
                    consent="NO";
                }
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consentCheckbox.isChecked()) {
                    // User has given consent, perform necessary actions
                    Toast.makeText(MainActivity.this, "Consent granted. Proceeding...", Toast.LENGTH_SHORT).show();
                    editor.putString("consent", consent);
                    editor.apply();
                    startActivity(next);
                    // Add your logic to navigate to the next activity or perform other actions here
                } else {
                    Toast.makeText(MainActivity.this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}




