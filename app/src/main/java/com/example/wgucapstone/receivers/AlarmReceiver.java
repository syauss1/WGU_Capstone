package com.example.wgucapstone.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.wgucapstone.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vacation_planner_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Vacation Planner Alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_flight_24)
                        .setContentTitle("Vacation Planner")
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}