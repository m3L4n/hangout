package com.example.ft_hangout.controller;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ft_hangout.R;
import com.example.ft_hangout.SmsBroadcastReceiver;
import com.example.ft_hangout.model.MydataBaseHelper;
import com.example.ft_hangout.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Updateactivity extends AppCompatActivity {
    EditText name, lastName, email, number, birthday;
    ImageView imageProfile;
    Button choose_image;
    int PERMISSION_REQUEST_CODE = 200;
    public static String _id, _name, _lastName, _email, _number, _birthday;
    Uri SelectImage;
    Uri SelectImage_enr;

    public  static Bitmap _image;
    ImageButton call_button, sms_button;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    Button update_button, delete_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateactivity);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(MainActivity.COLOR_ID)));
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
                if ( checkPermission()){

                String phone_number = number.getText().toString();

                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.fromParts("tel", phone_number, null));
                phone_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // start Intent
                startActivity(phone_intent);
            }
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
        SelectImage_enr = getImageUri(imageProfile);

        try{
            byte[] input_data;
            if (SelectImage_enr == null || getContentResolver().openInputStream(SelectImage_enr) == null) {
                Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_question_mark_24);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                defaultImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                input_data = stream.toByteArray();
            } else {
                InputStream inputStream = getContentResolver().openInputStream(SelectImage_enr);
                input_data = utils.getbytes(inputStream);
            }

            myDb.updateData(_id, _name, _lastName, _email, _number, _birthday,input_data);
        }
        catch (Exception e){
            myDb.updateData(_id, _name, _lastName, _email, _number, _birthday,null );
        }
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
                if (checkPermission()){
                imageChooser();
                }

            }
        });
    }
    private Uri getImageUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
        return Uri.parse(path);
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
            String imagePath = getIntent().getStringExtra("image");
            File file = new File(imagePath);
            if (file.exists()) {
                _image = BitmapFactory.decodeFile(imagePath);
                imageProfile.setImageBitmap(_image);
            } else {
                // Fichier introuvable, mettre une image par défaut ou afficher un message d'erreur.
            Drawable defaultDrawable = getResources().getDrawable(R.drawable.baseline_question_mark_24);
            imageProfile.setImageDrawable(defaultDrawable);
            }
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
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                            SelectImage = selectedImageUri;
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