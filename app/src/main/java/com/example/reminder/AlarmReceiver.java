package com.example.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "CHANNEL_SAMPLE";

    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            // Set the alarm here.
//        }

        //Get Id
        int notificationId = intent.getIntExtra("notificationId", 0);

        //Call MainActivity when notification is tapped
        Intent mainIntent=new Intent(context,MainActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(context,
                0,mainIntent,0);
        //NotificationManager
        NotificationManager notificationManager=(NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);

//        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Prepare Notification
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_none_24)
                .setContentTitle("MoodScore Time!")
                .setContentText("How's your mood today?")
                .setContentIntent(contentIntent)
//                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        //Notify
        notificationManager.notify(notificationId,builder.build());
    }
}
