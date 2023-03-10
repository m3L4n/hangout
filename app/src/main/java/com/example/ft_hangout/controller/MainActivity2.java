package com.example.ft_hangout.controller;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentCallbacks2;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.MydataBaseHelper;

public class MainActivity2 extends AppCompatActivity implements ComponentCallbacks2 {
    private SmsBroadcastReceiver smsBroadcastReceiver;
    EditText name, lastName, email, number, birthday;
    Button add_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.COLOR_ID));
        name = findViewById(R.id.Create_name);
        lastName = findViewById(R.id.Create_lastName);
        email = findViewById(R.id.Create_email);
        number = findViewById(R.id.Create_Number);
        birthday = findViewById(R.id.Create_Birthday);
        add_button = findViewById(R.id.create_button);
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MydataBaseHelper myDb = new MydataBaseHelper(MainActivity2.this);
                myDb.add_Contact(name.getText().toString().trim(),
                        lastName.getText().toString().trim(), email.getText().toString().trim(),
                        number.getText().toString().trim(), birthday.getText().toString().trim(), null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadcastReceiver, intentFilter);
        if (MainActivity.isInBackground)
        {
            if (MainActivity.mStartTime != 0) {
                MainActivity.mTimeInBackground = (System.currentTimeMillis() - MainActivity.mStartTime);
                MainActivity.mStartTime = 0;
                Toast.makeText(this, "L'application était en arrière-plan pendant " + MainActivity.mTimeInBackground / 1000 + " secondes", Toast.LENGTH_SHORT).show();
            }
        }
        MainActivity.isInBackground = false;
        // Cacher le Toast lorsque l'application reprend le focus
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsBroadcastReceiver);
    }
}