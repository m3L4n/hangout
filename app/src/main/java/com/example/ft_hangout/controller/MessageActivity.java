package com.example.ft_hangout.controller;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangout.CustomSmsAdapter;
import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.MydataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MessageActivity extends AppCompatActivity {
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private static final int PERMISSION_REQUEST_CODE = 200 ;
    EditText mgetMsg;
    ImageButton msendMsg;
    MydataBaseHelper mydb;
    CustomSmsAdapter customSmsAdapter;
    private  String enteredMsg;
    String currentTime;
    private ScrollView scrollView;
    Intent intent;
    ArrayList<String> msg_id, msg_num, msg_isSender, msg_content, msg_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.COLOR_ID));
        recyclerView = findViewById(R.id.recycleViewChat);
        mgetMsg = findViewById(R.id.getMessage);
        msendMsg = findViewById(R.id.Msendsms);
        mydb = new MydataBaseHelper(MessageActivity.this);
        msg_id = new ArrayList<>();
        msg_num = new ArrayList<>();
        msg_isSender = new ArrayList<>();
        msg_time = new ArrayList<>();
        msg_content = new ArrayList<>();
        storeDataInArray(Updateactivity._number);
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        customSmsAdapter = new CustomSmsAdapter(MessageActivity.this, this, msg_id, msg_content, msg_isSender, msg_time);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        recyclerView.setAdapter(customSmsAdapter);

// Faites d??filer la RecyclerView jusqu'?? la fin
        int lastPosition = customSmsAdapter.getItemCount() - 1;
        recyclerView.scrollToPosition(lastPosition);
    intent = getIntent();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        msendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !checkPermission())
                {
                    requestPermission();
                }
                else {
                    sendSms();
                }
            }
        });
    }
public void sendSms(){
        String number = Updateactivity._number;
        String msg = mgetMsg.getText().toString().trim();
        Date date = new Date();
        String result_time = simpleDateFormat.format(date);
        if (!msg.equals("") && !number.equals("")){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number,null,msg, null, null );
        }

        mydb.add_msg(number,result_time,msg);
        mgetMsg.setText("");
    Intent intent = new Intent(MessageActivity.this, MessageActivity.class);
    startActivity(intent);
    finish();
    Toast.makeText(getApplicationContext(), "SMS SEND", Toast.LENGTH_SHORT).show();
}
    void storeDataInArray(String numero){
        Cursor cursor = mydb.readAllMsg(numero);
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()) {
                msg_id.add(cursor.getString(0));
                msg_num.add(cursor.getString(1));
                msg_isSender.add(cursor.getString(2));
                msg_content.add(cursor.getString(3));
                msg_time.add(cursor.getString(4));
            }
            }
        }
    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECEIVE_SMS);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
    }
    public void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE, SEND_SMS, RECEIVE_SMS, READ_SMS}, PERMISSION_REQUEST_CODE);
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
                Toast.makeText(this, "L'application ??tait en arri??re-plan pendant " + MainActivity.mTimeInBackground / 1000 + " secondes", Toast.LENGTH_SHORT).show();
            }
        }
        MainActivity.isInBackground = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsBroadcastReceiver);
    }
}
