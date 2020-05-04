package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;
import at.htlkaindorf.twodoprojectmaxi.dialogs.TextInputFragment;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

public class CreationActivity extends AppCompatActivity{

    public TextView tvHeader;
    public EditText etTitle;
    public EditText etDescription;
    public TextView vwDate;
    public Button btOk;
    public Button btCancel;
    public Spinner spCategories;
    public Spinner spPriorities;
    public Spinner spReminder;

    public ArrayAdapter<Category> categoryAdapter;
    public DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public List<String> priorities = Arrays.asList("Low Priority", "Medium Priority", "High Priority");
    public List<String> remindingIntervalls = Arrays.asList("No Reminder", "Daily", "Weekly", "Monthly", "Yearly", "Specific Date", "Specific Interval");
    public Intent intent;
    public Entry entry;
    private Context help = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        init();
    }

    private void init(){
        //finden der einzelnen Komponenten
        tvHeader = findViewById(R.id.tv_creation_manipulation_title);
        etTitle = findViewById(R.id.etEntryTitle);
        etDescription = findViewById(R.id.etEntryDescription);
        vwDate = findViewById(R.id.vwDate);
        spCategories = findViewById(R.id.spCategories);
        spPriorities = findViewById(R.id.spPriority);
        spReminder = findViewById(R.id.spReminder);
        spReminder.setEnabled(false);
        //Maxi hier background auf hintergraut setzten

        intent = getIntent();

        //erstellen der Adapter für die Spinner
        List<Category> allCategories = Proxy.getClm().getAllCategories();
        categoryAdapter = new ArrayAdapter<Category>(this,
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
                if((adapterView.getSelectedItem()+"").equals("ADD CATEGORY"))
                {
                    DialogFragment textInputDlg = new TextInputFragment("Add Category",
                            "Please enter the name for the new category:", Proxy.getClm(),
                            adapterView.getSelectedItemPosition(), spCategories, help);
                    textInputDlg.show(getSupportFragmentManager(), "textInput");
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
        addOkListener();

        btCancel = findViewById(R.id.btCreationCancel);
        addCancelListener();
    }

    public void addOkListener()
    {
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createEntry(view)){
                    intent.putExtra("newEntry", entry);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public void addCancelListener()
    {
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

    public String readTitle()
    {
        String res = etTitle.getText().toString();
        if(res.toString().trim().isEmpty()){
            return null;
        }
        return res;
    }

    public String readDescription()
    {
        return etDescription.getText().toString();
    }

    public LocalDateTime readDueDate()
    {
        LocalDateTime dueDate = null;
        try {
            String parts [] = vwDate.getText().toString().split("\\.");
            if(parts.length == 3){
                dueDate = LocalDateTime.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), 0,0);
            }
        }catch (DateTimeParseException ex){
            dueDate = null;
        }
        return dueDate;
    }

    public Category readCategory()
    {
        return Proxy.getClm().getAllCategories().get(Proxy.getClm().getAllCategories()
                    .indexOf((Category)spCategories.getSelectedItem()));
    }

    public int readPriority()
    {
        int priorityNumber = 0;
        for (PriorityEnum prio:
                PriorityEnum.values()) {
            if(prio.getPrioirty_text().equalsIgnoreCase(spPriorities.getSelectedItem().toString())){
                priorityNumber = prio.getPrioirty_value();
            }
        }
        return priorityNumber;
    }

    public int readReminder()
    {
        int reminder_id = 0;
        for (ReminderEnum reminder:
                ReminderEnum.values()) {
            if(reminder.getReminder_identifierString().equalsIgnoreCase(spReminder.getSelectedItem().toString())){
                reminder_id = reminder.getReminder_id();
            }
        }
        return reminder_id;
    }

    public boolean createEntry(View view){
        //EditText edTitle = findViewById(R.id.etEntryTitle);
        String titleStr = readTitle();
        if(titleStr == null)
        {
            Snackbar.make(view, "Please enter a Title for your Entry", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        //EditText edDescription = findViewById(R.id.etEntryDescription);
        String descriptionStr = readDescription();

        LocalDateTime dueDate = readDueDate();
        if(dueDate == null)
        {
            Snackbar.make(view, "Please enter a Date for your Entry", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        Category cat = readCategory();

        int priorityNumber = readPriority();

        int reminder_id = readReminder();

        entry = new Entry(reminder_id, dueDate, titleStr, descriptionStr, priorityNumber, cat);
        return true;
    }

}
