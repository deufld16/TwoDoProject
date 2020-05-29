package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class App extends Application {

    public static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        Proxy.setContext(this);
    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel reminder_channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder for Activity",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            reminder_channel.setDescription("This channel is used to transmit the reminders for the activities");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(reminder_channel);
            Log.d("NOTIFICATION_TODO", "createNotificationChannels: erstellt");
        }
    }
}
