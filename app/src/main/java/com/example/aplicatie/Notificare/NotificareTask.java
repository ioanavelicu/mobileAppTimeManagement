package com.example.aplicatie.Notificare;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.aplicatie.AddTaskActivity;
import com.example.aplicatie.DateConverter.DateConverter;
import com.example.aplicatie.MainActivity;
import com.example.aplicatie.R;
import com.example.aplicatie.Reminder.Reminder;

public class NotificareTask extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Reminder reminder = new Reminder();
        reminder.setMesaj(intent.getStringExtra(AddTaskActivity.MESAJ_REMINDER));
        reminder.setDataReminder(DateConverter.fromString(AddTaskActivity.DATA_REMINDER));

        Uri sunetAlarma = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(i);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifcareBuilder = new NotificationCompat.Builder(context);
        NotificationChannel canal = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canal = new NotificationChannel("AplicatieLicenta", "HELLO", NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = notifcareBuilder
                .setSmallIcon(R.drawable.reminder)
                .setContentTitle("Reminder")
                .setContentText(intent.getStringExtra(AddTaskActivity.MESAJ_REMINDER))
                .setAutoCancel(true)
                .setSound(sunetAlarma)
                .setChannelId("AplicatieLicenta")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(canal);
        }
        notificationManager.notify(1, notification);
    }
}
