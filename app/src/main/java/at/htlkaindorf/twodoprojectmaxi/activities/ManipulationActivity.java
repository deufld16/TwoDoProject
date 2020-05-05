package at.htlkaindorf.twodoprojectmaxi.activities;

import android.os.Bundle;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;

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
        //hier bitte den aktuellen wert für priority auf spinner setzen
        vwDate.setText(editEntry.getDueDate().format(dtf));
        //hier bitte den aktuellen wert für reminder auf spinner setzen
    }
}
