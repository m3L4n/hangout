package com.example.ft_hangout.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.MydataBaseHelper;

import java.io.IOException;

public class Updateactivity extends AppCompatActivity {
    EditText name, lastName, email, number, birthday;
    private static final int GALLERY_REQ_CODE = 1000;
    ImageView imageProfile;
    Button choose_image;
    public static String _id, _name, _lastName, _email, _number, _birthday;
    ImageButton call_button, sms_button;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    Button update_button, delete_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateactivity);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(MainActivity.COLOR_ID));
        name = findViewById(R.id.update_name);
        lastName = findViewById(R.id.update_lastName);
        email = findViewById(R.id.update_email);
        number = findViewById(R.id.update_Number);
        birthday = findViewById(R.id.update_Birthday);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        call_button = findViewById(R.id.Call_button);
        imageProfile = findViewById(R.id.imageProfile);
        choose_image = findViewById(R.id.choose_image);
        sms_button = findViewById(R.id.go_to_SMS);
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        getAndSetIntentData();
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle(_name);
        sms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sms_activity = new Intent(Updateactivity.this, MessageActivity.class);
                startActivity(sms_activity);
            }
        });
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = number.getText().toString();

                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.fromParts("tel", phone_number, null));
                phone_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // start Intent
                startActivity(phone_intent);
            }
        });
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        MydataBaseHelper myDb = new MydataBaseHelper(Updateactivity.this);
        _name = name.getText().toString().trim();
        _lastName = lastName.getText().toString().trim();
        _email = email.getText().toString().trim();
        _number = number.getText().toString().trim();
        _birthday = birthday.getText().toString().trim();
        myDb.updateData(_id, _name, _lastName, _email, _number, _birthday);
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();

            }
        });
    }


    public void getAndSetIntentData(){
        if (getIntent().hasExtra("id") && getIntent().hasExtra("name")&&
                getIntent().hasExtra("lastName") && getIntent().hasExtra("email")
                && getIntent().hasExtra("Number") && getIntent().hasExtra("birthday")){

            _id = getIntent().getStringExtra("id");
            _name = getIntent().getStringExtra("name");
            _lastName = getIntent().getStringExtra("lastName");
            _email = getIntent().getStringExtra("email");
            _number = getIntent().getStringExtra("Number");
            _birthday = getIntent().getStringExtra("birthday");
            name.setText(_name);
            lastName.setText(_lastName);
            email.setText(_email);
            number.setText(_number);
            birthday.setText(_birthday);
        }
        else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("@string/delete_title" + _name);
        builder.setMessage("@string/delete_msg" + _name + "?");
        builder.setPositiveButton("@string/positive", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MydataBaseHelper myDb = new MydataBaseHelper(Updateactivity.this);
                myDb.deleteOneRow(_id);
                finish();
            }
        });
        builder.setNegativeButton("@string/negative", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        imageProfile.setImageBitmap(
                                selectedImageBitmap);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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