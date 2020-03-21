package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;

public class CreationActivity extends AppCompatActivity {

    private TextView vwDate;
    private Button btOk;
    private Button btCancel;
    private Spinner spCategories;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.M.yyyy");
    private CategoryListModel clm = new CategoryListModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        init();
    }

    private void init(){
        vwDate = findViewById(R.id.vwDate);
        spCategories = findViewById(R.id.spCategories);

        //Das auflisten aller Verfügbaren Kategorien
        List<Category> allCategories = clm.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_item, allCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(categoryAdapter);

        btOk = findViewById(R.id.btCreationOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createEntry(view)){
                    Snackbar.make(view, "Entry saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        btCancel = findViewById(R.id.btCreationCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment(vwDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public View getVwDate() {
        return vwDate;
    }

    public boolean createEntry(View view){
        EditText edTitle = findViewById(R.id.edTitle);
        EditText edDescription = findViewById(R.id.edDescription);
        if(edTitle.getText().toString().isEmpty()){
            Snackbar.make(view, "Please enter a Title for your Entry", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }
        LocalDate dueDate = LocalDate.parse(vwDate.getText().toString(), dtf);

        //Entry entry = new Entry(edTitle.getText().toString(), edDescription.getText().toString(),
        //        )

        return true;
    }
}
