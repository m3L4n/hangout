package com.example.ft_hangout.controller;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangout.BuildConfig;
import com.example.ft_hangout.CustomerAdapter;
import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.Contact;
import com.example.ft_hangout.model.MydataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ComponentCallbacks2 {

    RecyclerView recyclerView;
     ImageView empty_ImageView;
     private  SmsBroadcastReceiver smsBroadcastReceiver;
     TextView no_data;
     FloatingActionButton add_button;
     MydataBaseHelper mydb;
     ArrayList<String> contact_id, contact_name, contact_lastName, contact_email, contact_number,
        contact_birthday;
    CustomerAdapter customerAdapter;
    public static int COLOR_ID = 0xFFCCCCCC;
    public static boolean isInBackground = true;
    public static long mStartTime = 0;
    public  static long mTimeInBackground = 0;
    Contact mContact;
        int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerComponentCallbacks(this);
        recyclerView = findViewById(R.id.recycleView);
        add_button = findViewById(R.id.add_button);
        empty_ImageView = findViewById(R.id.imageNoData);
        no_data = findViewById(R.id.noData);
        if (!checkPermission())
        {
            requestPermission();
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.COLOR_ID));
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
        mydb = new MydataBaseHelper(MainActivity.this);
        contact_id = new ArrayList<>();
        contact_name = new ArrayList<>();
        contact_lastName = new ArrayList<>();
        contact_email = new ArrayList<>();
        contact_number = new ArrayList<>();
        contact_birthday = new ArrayList<>();
        storeDataInArray();
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        customerAdapter = new CustomerAdapter(MainActivity.this,this, contact_id,contact_name,contact_lastName,contact_email,contact_number,contact_birthday);
        recyclerView.setAdapter(customerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            recreate();
        }
    }

    void storeDataInArray(){
        Cursor cursor = mydb.readAllData();
        if (cursor.getCount() == 0){
            empty_ImageView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }
        else {
            while (cursor.moveToNext()){
                contact_id.add(cursor.getString(0));
                contact_name.add(cursor.getString(1));
                contact_lastName.add(cursor.getString(2));
                contact_email.add(cursor.getString(3));
                contact_number.add(cursor.getString(4));
                contact_birthday.add(cursor.getString(5));

            }
            empty_ImageView.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            // L'application passe en arrière-plan
            mStartTime = System.currentTimeMillis();
            isInBackground = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(smsBroadcastReceiver, intentFilter);
        if (isInBackground)
        {
            if (mStartTime != 0) {
                mTimeInBackground = (System.currentTimeMillis() - mStartTime);
                mStartTime = 0;
                Toast.makeText(this, "L'application était en arrière-plan pendant " + mTimeInBackground / 1000 + " secondes", Toast.LENGTH_SHORT).show();
            }
        }
        isInBackground = false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.color_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color_red:{

                COLOR_ID = Color.RED;
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.red)));
                return true;
            }
            case R.id.color_blue:{

                COLOR_ID = Color.BLUE;
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                return true;
            }
            case R.id.color_green:{
                COLOR_ID = Color.GREEN;
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GREEN));
                return true;
            }
            case R.id.delete_all:{
                confirmDialog();
                return  true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("@string/deleteAll_title");
        builder.setMessage("@string/deleteAll_msg");
        builder.setPositiveButton("@string/positive", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mydb = new MydataBaseHelper(MainActivity.this);
                mydb.deleteAllData();
                recreate();
              //  Intent intent = new Intent(MainActivity.this, MainActivity.class);
           //     startActivity(intent);
           //     finish();
            }
        });
        builder.setNegativeButton("@string/negative", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&  result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }
    public void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE, SEND_SMS, RECEIVE_SMS, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsBroadcastReceiver);
    }
}


