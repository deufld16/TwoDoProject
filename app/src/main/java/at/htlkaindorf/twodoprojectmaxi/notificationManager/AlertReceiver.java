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

    /**
     * Method, which recieves the alarm when it goes off. This method creates and displays the reminder notification using an self created notification channel.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATION_FIX", "onReceive: Started message sending process");

        String displayText = Proxy.getLanguageContext().getString(R.string.notifications_this_is_a_reminder);
        String parts[] = intent.getStringExtra("doneUntil").split("\\.");
        LocalDateTime help = LocalDateTime.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]),Integer.parseInt(parts[0]), 0, 0);
        if(help.minusDays(1).isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += Proxy.getLanguageContext().getString(R.string.tomorrow);
        }else if(help.isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += Proxy.getLanguageContext().getString(R.string.today);
        }
        else if(help.minusDays(2).isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0))){
            displayText += Proxy.getLanguageContext().getString(R.string.the_day_after_tomorrow);
        }else{
            if(!Proxy.getLanguageContext().getString(R.string.notifications_stupid_language_3).equalsIgnoreCase("-")){
                displayText += Proxy.getLanguageContext().getString(R.string.notifications_stupid_language_3) + " ";
            }
            displayText += intent.getStringExtra("doneUntil");
        }
        if(!Proxy.getLanguageContext().getString(R.string.language_is_stupid).equalsIgnoreCase("-")){
            displayText += Proxy.getLanguageContext().getString(R.string.language_is_stupid);
        }
        Intent notificationIntent = new Intent(Proxy.getContext(), ToDoListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(Proxy.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Proxy.getContext());

        Notification notification = new NotificationCompat.Builder(Proxy.getContext(), App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(Proxy.getLanguageContext().getString(R.string.notifications_reminder_for) + intent.getStringExtra("title"))
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
        Log.d("NOTIFICATION_FIX", "onReceive: Terminated message sending process");
    }
}
