package at.htlkaindorf.twodoprojectmaxi.notificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class RestartAppReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "NOTIFICATION_TODO";
    private static final String FILE_NAME = "entries.ser";

    public RestartAppReceiver() {
    }

    /**
     * Method which reschedules the alarm after a device power off (removes all currently set Alarms)
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case Intent.ACTION_BOOT_COMPLETED:
                    Log.i(LOG_TAG, "Start resetting alarms after reboot");

                    boolean entriesFileExists = false;

                    for (File file:
                            Proxy.getContext().getFilesDir().listFiles()) {
                        Log.d(LOG_TAG, "onCreate: " + file.getName()) ;
                        if(file.getName().equalsIgnoreCase("entries.ser")){
                            entriesFileExists = true;
                        }
                    }
                    if(entriesFileExists){
                        List<Entry> allEntries = helpLoadEntries();
                        for (Entry entry:
                             allEntries) {
                            Log.d(LOG_TAG, entry.getDueDate() + " " + entry.getTitle());
                            if(entry.needsReminder()){
                                NotificationHelper.startAlarm(entry);
                            }
                        }
                    }else{
                        return;
                    }
                    Log.i(LOG_TAG, "Finish resetting alarms after reboot");
                    break;
                default:
                    break;
            }
        }
    }

    private List<Entry> helpLoadEntries(){
        try{
            List<Entry> allEntries = new LinkedList<>();
            FileInputStream fis = Proxy.getContext().openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            allEntries = (List<Entry>)ois.readObject();
            ois.close();
            return allEntries;
        }catch (Exception ex){
            Log.d("NOTIFICATION_TODO", "Fehler beim Einlesen");
            return null;
        }
    }
}
