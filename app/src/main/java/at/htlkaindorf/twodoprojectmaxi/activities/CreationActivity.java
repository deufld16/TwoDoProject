package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.Operations;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;
import at.htlkaindorf.twodoprojectmaxi.dialogs.TextInputFragment;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

public class CreationActivity extends AppCompatActivity{

    private TextView vwDate;
    private Button btOk;
    private Button btCancel;
    private Spinner spCategories;
    private Spinner spPriorities;
    private Spinner spReminder;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.M.yyyy");
    private CategoryListModel clm = new CategoryListModel();
    private List<String> priorities = Arrays.asList("Low Priority", "Medium Priority", "High Priority");
    private List<String> remindingIntervalls = Arrays.asList("No Reminder", "Daily", "Weekly", "Monthly", "Yearly", "Specific Date", "Specific Interval");
    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        init();
    }

    private void init(){
        //finden der einzelnen Komponenten
        vwDate = findViewById(R.id.vwDate);
        spCategories = findViewById(R.id.spCategories);
        spPriorities = findViewById(R.id.spPriority);
        spReminder = findViewById(R.id.spReminder);
        spReminder.setEnabled(false);
        //Maxi hier background auf hintergraut setzten

        //erstellen der Adapter für die Spinner
        List<Category> allCategories = clm.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(this,
                R.layout.spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, remindingIntervalls);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoryAdapter);
        spPriorities.setAdapter(priorityAdapter);
        spReminder.setAdapter(reminderAdapter);

        //Erstellen der Methoden für die Spinner
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if((adapterView.getSelectedItem()+"").equals("Add category"))
                {
                    DialogFragment textInputDlg = new TextInputFragment("Add Category",
                            "Please enter the name for the new category:");
                    textInputDlg.show(getSupportFragmentManager(), "textInput");
//to be continued (https://developer.android.com/guide/topics/ui/dialogs#java)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spReminder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(remindingIntervalls.get(i).equalsIgnoreCase("Specific Date")){
                    //Anzeigen des Kalenders
                }else if(remindingIntervalls.get(i).equalsIgnoreCase("Specific Interval")){
                    //Anzeigen einer Textarea wo die Eingabe eines Zahlenwerts (Intervall gefragt wird)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btOk = findViewById(R.id.btCreationOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createEntry(view)){
                    Intent intent = getIntent();
                    intent.putExtra("newEntry", entry);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        btCancel = findViewById(R.id.btCreationCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment(vwDate, spReminder);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public View getVwDate() {
        return vwDate;
    }

    public boolean createEntry(View view){
        EditText edTitle = findViewById(R.id.etTitle);
        EditText edDescription = findViewById(R.id.etDescription);
        if(edTitle.getText().toString().trim().isEmpty()){
            Snackbar.make(view, "Please enter a Title for your Entry", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }
        LocalDateTime dueDate = null;
        try {
            String parts [] = vwDate.getText().toString().split("\\.");
            if(parts.length == 3){
                dueDate = LocalDateTime.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), 0,0);
            }
        }catch (DateTimeParseException ex){
                dueDate = null;
        }
        if(dueDate == null)
        {
            Snackbar.make(view, "Please enter a Date for your Entry", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }
        Category cat = (Category)spCategories.getSelectedItem();
        int priorityNumber = 0;
        for (PriorityEnum prio:
             PriorityEnum.values()) {
            if(prio.getPrioirty_text().equalsIgnoreCase(spPriorities.getSelectedItem().toString())){
                priorityNumber = prio.getPrioirty_value();
            }
        }
        int reminder_id = 0;
        for (ReminderEnum reminder:
                ReminderEnum.values()) {
            if(reminder.getReminder_identifierString().equalsIgnoreCase(spReminder.getSelectedItem().toString())){
                reminder_id = reminder.getReminder_id();
            }
        }

        entry = new Entry(reminder_id, dueDate, edTitle.getText().toString(), edDescription.getText().toString(), priorityNumber, cat);
        return true;
    }

}
