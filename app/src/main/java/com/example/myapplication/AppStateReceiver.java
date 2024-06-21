package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppStateReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "user_notifications_channel";
    private static final String CHANNEL_NAME = "User Notifications";

    @SuppressLint("NotificationPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // 創建通知渠道
        createNotificationChannel(context);

        // 構建通知
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Notification Title")
                .setContentText("This is a test notification.")
                .setSmallIcon(R.drawable.images)
                .build();

        // 顯示通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    // 創建通知渠道（僅適用於 Android 8.0 及更高版本）
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
