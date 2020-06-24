package at.htlkaindorf.twodoprojectmaxi.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/**
 * Class that is called before any other activity or class and is used for general configuration purposes which has to be done at the beginning of the application start
 */
public class App extends Application {

    public static final String CHANNEL_ID = "reminder_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        Proxy.setContext(this);
    }


    /**
     * Method which creates the NotificationChannel over which the reminder notifications can be sent
     */
    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel reminder_channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder for Activity",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            reminder_channel.setDescription(getString(R.string.channel_descritption));

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(reminder_channel);
            Log.d("NOTIFICATION_TODO", "createNotificationChannels: erstellt");
        }
    }

}
