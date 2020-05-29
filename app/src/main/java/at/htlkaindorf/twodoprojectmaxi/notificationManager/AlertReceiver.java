package at.htlkaindorf.twodoprojectmaxi.notificationManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import at.htlkaindorf.twodoprojectmaxi.MainActivity;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.App;
import at.htlkaindorf.twodoprojectmaxi.activities.ToDoListActivity;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class AlertReceiver extends BroadcastReceiver {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("NOTIFICATION_TESTING", "onReceive: Started message sending process");

        String displayText = "This is a reminder that this activity has to be done until ";
        String parts[] = intent.getStringExtra("doneUntil").split("\\.");
        LocalDateTime help = LocalDateTime.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]),Integer.parseInt(parts[0]), 0, 0);
        if(help.minusDays(1).isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += "tomorrow";
        }else if(help.isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += "today";
        }
        else if(help.minusDays(2).isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += "the day after tomorrow";
        }else{
            displayText += intent.getStringExtra("doneUntil");
        }

        Intent notificationIntent = new Intent(Proxy.getContext(), ToDoListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(Proxy.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Proxy.getContext());

        Notification notification = new NotificationCompat.Builder(Proxy.getContext(), App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Reminder for " + intent.getStringExtra("title"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(displayText))
                .setContentText(displayText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(intent.getIntExtra("id", 1), notification);
        if(!intent.getStringExtra("nextDueDate").equalsIgnoreCase("none")){
            NotificationHelper.startAlarm(intent.getIntExtra("id", 1));
        }
        //Log.d("NOTIFICATION_TESTING", "onReceive: Terminated message sending process");
    }
}
