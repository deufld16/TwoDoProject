package at.htlkaindorf.twodoprojectmaxi.activities;

import android.os.Bundle;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

/**
 * Class that inherits from the creation Activity. This acitvity is used for displaying detailed information of the entry
 * and to edit the entries.
 */

public class ManipulationActivity extends CreationActivity
{
    private Entry editEntry;
    private int position;

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
