package com.example.ft_hangout;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.ft_hangout.controller.MainActivity;
import com.example.ft_hangout.controller.MessageActivity;
import com.example.ft_hangout.model.MydataBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        MydataBaseHelper mydb = new MydataBaseHelper(context);
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        Date date = new Date();
                        String result_time = simpleDateFormat.format(date);
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = smsMessage.getMessageBody();
                        String sender = smsMessage.getOriginatingAddress();
                        mydb.isNeedToEnregister(sender, result_time, messageBody);
                        Toast.makeText(context, "Nouveau message reçu de " + sender + ": " + messageBody, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            boolean messageActivityRunning = false;
            boolean mainActivityRunning = false;

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo runningTask : runningTasks) {
                ComponentName componentName = runningTask.topActivity;
                if (componentName.getClassName().equals(MessageActivity.class.getName())) {
                    messageActivityRunning = true;
                    break;
                }
                else if (componentName.getClassName().equals(MainActivity.class.getName())) {
                    mainActivityRunning = true;
                }
            }
            if (messageActivityRunning){

                  Intent i = new Intent(context, MessageActivity.class);
                  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  context.startActivity(i);
            }
            else if( mainActivityRunning){
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        }
         //  Intent i = new Intent(context, MessageActivity.class);
          //  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          //  context.startActivity(i);
    }
}

