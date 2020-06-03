package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;
import at.htlkaindorf.twodoprojectmaxi.dialogs.TextInputFragment;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

/**
 * This class is used for several purposes:
 *      -to add new entries when pressing on the plus symbol on the Main Page
 *      -to edit entries when clicking on them (detailed Information display)
 * Therefore the class contains all Components, that are needed to create/edit an entry
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class CreationActivity extends AppCompatActivity{

    protected TextView tvHeader;
    protected EditText etTitle;
    protected EditText etDescription;
    protected TextView vwDate;
    protected Button btOk;
    protected Button btCancel;
    protected Spinner spCategories;
    protected Spinner spPriorities;
    protected Spinner spReminder;

    protected ArrayAdapter<Category> categoryAdapter;
    protected ArrayAdapter<String> priorityAdapter;
    protected ArrayAdapter<String> reminderAdapter;
    protected Intent intent;
    protected DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private List<String> priorities = Arrays.asList("Low Priority", "Medium Priority", "High Priority");
    private List<String> remindingIntervalls = Arrays.asList("No Reminder", "Daily", "Weekly", "Monthly", "Yearly", "Specific Date", "Specific Interval");
    private Entry entry;
    private Context helpContext = this;

    /**
     * Method that inflates/creates the GUI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        init();
    }

    /**
     * Method in which all the component are fetched and then initialized in an for the component appropriate way
     *      -with ArrayAdapters
     *      -with OnClickEvents (for the detailed view/editing of the entry)
     */
    private void init(){
        //fetching the components and disabling the ReminderInterval until a date is selected
        tvHeader = findViewById(R.id.tv_creation_manipulation_title);
        etTitle = findViewById(R.id.etEntryTitle);
        etDescription = findViewById(R.id.etEntryDescription);
        vwDate = findViewById(R.id.vwDate);
        spCategories = findViewById(R.id.spCategories);
        spPriorities = findViewById(R.id.spPriority);
        spReminder = findViewById(R.id.spReminder);
        spReminder.setEnabled(false);

        intent = getIntent();

        //creating the adapters for the spinners so that they display the wanted content
        List<Category> allCategories = Proxy.getClm().getAllCategories();
        categoryAdapter = new ArrayAdapter<Category>(this,
                R.layout.spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        priorityAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, remindingIntervalls);
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategories.setAdapter(categoryAdapter);
        spPriorities.setAdapter(priorityAdapter);

        //Creating the methods for the Spinners (OnSelectEvents)
        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if((adapterView.getSelectedItem()+"").equals("ADD CATEGORY"))
                {
                    DialogFragment textInputDlg = new TextInputFragment("Add Category",
                            "Please enter the name for the new category:", Proxy.getClm(),
                            adapterView.getSelectedItemPosition(), spCategories, helpContext);
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
    //OnClick event for the OK Button which creates the entry with the input data
    public void addOkListener()
    {
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createEntry(view)){
                    intent.putExtra("newEntry", entry);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.from_top_partial, 0);
                }
            }
        });
    }
    //OnClick event for the Cancel Button in order to cancel the creation of the entry
    public void addCancelListener()
    {
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.from_top_partial, 0);
            }
        });
    }

    //Custom fragement which is used for displaying the calender like selecting of the date
    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment(vwDate, spReminder, reminderAdapter);
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

    //Methode to read the due date
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
    //Method to read the Priority as an Enum
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

    //Method to read the Reminder as an Enum
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

    /**
     * Method that creates a new Entry and returns a boolean that distinguishes whether or not the entry could be created
     *      -checks if all required input parameters have been set in an adequat way
     *      -true -> Entry was created and added to the list
     *      -false -> Entry could not be created
     * @param view
     * @return
     */
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

        entry = new Entry(reminder_id, dueDate, titleStr, descriptionStr, priorityNumber, cat, getRequestID());

        return true;
    }

    public int getRequestID(){
        int request_id = 0;
        int help = 0;
        do{
            help = 0;
            request_id ++;
            for (Entry entry:
                    Proxy.getToDoAdapter().getEntries()) {
                if(entry.getRequest_id() != request_id){
                    help ++;
                }
            }
        }while(help != Proxy.getToDoAdapter().getEntries().size());

        Log.d("ERROR", request_id + "");
        return request_id;
    }

}
