package at.htlkaindorf.twodoprojectmaxi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import at.htlkaindorf.twodoprojectmaxi.R;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.bl.VoiceRecordAdapter;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.ImageRecorder;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.SoundRecorder;

/**
 * Class that inherits from the creation Activity. This acitvity is used for displaying detailed information of the entry
 * and to edit the entries.
 *
 * @author Maximilian Strohmaier
 */

public class ManipulationActivity extends CreationActivity
{
    private Entry editEntry;
    private int position;
    private List<Uri> savepointUris = new LinkedList<>();


    /**
     * Overrides the onCreate method from the CreatinActivity so that the components are filled
     * with the values of the entry (for the detailed view and to edit them)
     *      -Enums are set
     *      -Title and description are set
     *      -due date is set
     *      -in other words all attribute from an entry, that can be entered when creating the entry, are set.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Proxy.getVra() == null){
            Proxy.setVra(new VoiceRecordAdapter(this, getSupportFragmentManager()));
        }
        super.onCreate(savedInstanceState);
        editEntry = (Entry) intent.getSerializableExtra("oldEntry");
        Proxy.getVra().setEntry(editEntry);
        Proxy.getVra().setSourceActivity(this);
        position = intent.getIntExtra("entryPos", 0);
        intent.putExtra("position", position);

        tvHeader.setText(getString(R.string.edit_entry_page_Title));
        etTitle.setText(editEntry.getTitle());
        etDescription.setText(editEntry.getEntryNote());
        spCategories.setSelection(categoryAdapter.getPosition(editEntry.getCategory()));
        PriorityEnum priority = null;
        for (PriorityEnum help:
             PriorityEnum.values()) {
            if(help.getPrioirty_value() == editEntry.getPriorityValue()){
                priority = help;
            }
        }

        spPriorities.setSelection(priorityAdapter.getPosition(priority.getPrioirty_text()));
        vwDate.setText(editEntry.getDueDate().format(dtf));

        ReminderEnum reminder = null;

        for (ReminderEnum reminderEnum:
             ReminderEnum.values()) {
            if(editEntry.getReminderID() == reminderEnum.getReminder_id()){
                reminder = reminderEnum;
            }
        }
        spReminder.setAdapter(reminderAdapter);
        spReminder.setEnabled(true);
        spReminder.setSelection(reminderAdapter.getPosition((reminder.getReminder_identifierString())));

        savepointUris = editEntry.getAllPhotoLocations().stream().map(s -> Uri.parse(s)).collect(Collectors.toList());
        for (Uri uri : savepointUris)
        {
            try {
                photoAdpt.addPhoto(uri, ImageRecorder.createScaledBitmap(this, uri, 600.));
            } catch (IOException e) {
                Toast.makeText(helpContext, getString(R.string.info_img_load_error), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
        //recordButtonOnClickListener

    }

    @Override
    protected void addRecordAudioHandler() {
        btRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Proxy.getSoundRecorder().checkAudioPremissionFromDevice()){
                    if(isRecording){
                        Proxy.getSoundRecorder().stopRecordAudio();
                        Proxy.getVra().renew();
                        isRecording = false;
                        btRecordAudio.setText(getString(R.string.add_entry_page_record_audio));

                    }else{
                        if(Proxy.getVra().getDisplayedAudios().size() < 3){
                            btRecordAudio.setText(getString(R.string.add_entry_page_stop_recording_audio));
                            isRecording = true;
                            Proxy.getSoundRecorder().recordAudio(editEntry);
                        }else{
                            Toast.makeText(Proxy.getContext(), getString(R.string.add_entry_page_information_max_recordings), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Proxy.getSoundRecorder().requestPermission(activity);
                }
            }
        });
    }
    @Override
    public void addCancelListener() {
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                List<Uri> urisToDelete = photoAdpt.getImageUris();
                urisToDelete.removeAll(savepointUris);
                for (Uri uri : urisToDelete)
                {
                    Log.d("PHOTO_STORAGE", "checking: "+uri.toString());
                    getContentResolver().delete(uri, null, null);
                    Log.d("PHOTO_STORAGE", uri.toString() + " deleted");
                }
                finish();
                overridePendingTransition(0, R.anim.from_right);
            }
        });
    }
}
