package at.htlkaindorf.twodoprojectmaxi.notificationManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.App;
import at.htlkaindorf.twodoprojectmaxi.activities.ToDoListActivity;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;


public class NotificationHelper {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.YYYY");
    private static final String FILE_NAME =  "entries.ser";

    public static void startAlarm(Entry entry){
        AlarmManager alarmManager = (AlarmManager) Proxy.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Proxy.getContext(), AlertReceiver.class);
        //Log.d("NOTIFICATION_TODO", "startAlarm: " + entry.getTitle() + entry.getRequest_id());
        intent.setData(Uri.parse("timer: " + entry.getReminderDates().get(0)));

        intent.putExtra("title", entry.getTitle());
        intent.putExtra("doneUntil", dtf.format(entry.getDueDate()));
        intent.putExtra("id", entry.getRequest_id());

        if(entry.getReminderDates().size() > 1) {
            String nextDueDate = entry.getReminderDates().get(1) + "";
            //Log.d("NOTIFICATION_TODO", nextDueDate);
            intent.putExtra("nextDueDate", nextDueDate);
        }else{
            intent.putExtra("nextDueDate", "none");
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Proxy.getContext(), 0, intent, 0);
        Calendar c = Calendar.getInstance();
        Log.d("NOTIFICATION_TESTING", "method: startAlarm - the next alarm for the activity " + entry.getTitle() + " should occur at " + entry.getReminderDates().get(0));
        c.setTime(Date.from(entry.getReminderDates().get(0).atZone(ZoneId.systemDefault()).toInstant()));
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 8, 0);
        //Log.d("NOTIFICATION_TODO", c.getTime() + " ----- " + Calendar.getInstance().getTime());
        alarmManager.setExact( AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        entry.getReminderDates().remove(0);
    }

    public static void startAlarm(int id){
        Entry specificEntry = null;
        try{
            List<Entry> allEntries = Proxy.getToDoAdapter().getEntries();
            for (Entry entry:
                 allEntries) {
                if(entry.getRequest_id() == id){
                    specificEntry = entry;
                    break;
                }
            }

            startAlarm(specificEntry);
        }catch (Exception ex){
            Log.d("NOTIFICATION_TESTING", "Error while Loading Entries");
        }
    }

    public static void cancelAlarm(int request_id){
        AlarmManager alarmManager = (AlarmManager) Proxy.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Proxy.getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Proxy.getContext(), request_id, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
