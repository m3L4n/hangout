package com.example.ft_hangout.controller;
import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangout.CustomerAdapter;
import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.MydataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private static final String COLUMN_PHOTO = "_photo";
    RecyclerView recyclerView;
     ImageView empty_ImageView;
     private  SmsBroadcastReceiver smsBroadcastReceiver;
     TextView no_data;
     FloatingActionButton add_button;
     MydataBaseHelper mydb;
     ArrayList<String> contact_id, contact_name, contact_lastName, contact_email, contact_number,
        contact_birthday;
     ArrayList<Bitmap> contact_photo;
    CustomerAdapter customerAdapter;
    public static int COLOR_ID = R.color.clementine;
    public static boolean isInBackground = true;
    public static long mStartTime = 0;
    public  static long mTimeInBackground = 0;
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
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(MainActivity.COLOR_ID)));
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });
        mydb = new MydataBaseHelper(MainActivity.this);
        contact_id = new ArrayList<>();
        contact_name = new ArrayList<>();
        contact_lastName = new ArrayList<>();
        contact_email = new ArrayList<>();
        contact_number = new ArrayList<>();
        contact_birthday = new ArrayList<>();
        contact_photo = new ArrayList<>();
        storeDataInArray();
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        customerAdapter = new CustomerAdapter(MainActivity.this,this, contact_id,contact_name,contact_lastName,contact_email,contact_number,contact_birthday, contact_photo);
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
                contact_birthday.add(cursor.getString(6));
                @SuppressLint("Range") byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PHOTO));
                if (imageBytes != null && imageBytes.length > 0) {
                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    contact_photo.add(image);
                } else {
                    Bitmap defaultImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.baseline_question_mark_24);
                    contact_photo.add(defaultImage);
                }
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
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color_clementine:{

                COLOR_ID = R.color.clementine;
                Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.clementine)));
                return true;
            }
            case R.id.color_brownOrange:{

                COLOR_ID = R.color.brownOrange;
                Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.brownOrange)));
                return true;
            }
            case R.id.color_brown:{
                COLOR_ID = R.color.brown;
                Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.brown)));
                return true;
            }
            case R.id.color_orange:{
                COLOR_ID = R.color.orange;
                Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.orange)));
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
        builder.setPositiveButton("@string/positive", (dialogInterface, i) -> {
            mydb = new MydataBaseHelper(MainActivity.this);
            mydb.deleteAllData();
            recreate();
        });
        builder.setNegativeButton("@string/negative", (dialogInterface, i) -> {

        });
        builder.create().show();
    }
    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&  result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED;
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


