package com.example.myfirstapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.MyApplication;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "TAG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);
        notifBuilder.setContentTitle("FCM NOTIFICATION");
        notifBuilder.setContentText(remoteMessage.getNotification().getBody());
        notifBuilder.setAutoCancel(true);
        notifBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifBuilder.setContentIntent(pendingIntent);
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(0,notifBuilder.build());
    }
}
