package at.htlkaindorf.twodoprojectmaxi.activities;

import android.os.Bundle;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

public class ManipulationActivity extends CreationActivity
{
    private Entry editEntry;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editEntry = (Entry) intent.getSerializableExtra("oldEntry");
        position = intent.getIntExtra("entryPos", 0);
        intent.putExtra("position", position);

        tvHeader.setText("Edit Entry");
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
    }
}
