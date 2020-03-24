package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.ToDoAdapter;

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView rvToDo;
    private EditText etSearchbar;
    private Spinner spCategory;
    private ToDoAdapter toDoAdapter = new ToDoAdapter(this);
    private RecyclerView.LayoutManager lm;
    private CategoryListModel clm = new CategoryListModel();


    private final int RC_CREATION_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        try {
            boolean fileExists = false;
            for (File file:
                 getFilesDir().listFiles()) {
                Log.d("ERROR", "onCreate: " + file.getName()) ;
                if(file.getName().equalsIgnoreCase("entries.ser")){
                    fileExists = true;
                }
            }
            if(fileExists){
                toDoAdapter.loadEntries();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry();
            }
        });
        etSearchbar = findViewById(R.id.et_searchBar);
        etSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toDoAdapter.setFilter(etSearchbar.getText().toString());
            }
        });
        //List<Category> allCategories = clm.getAllCategories();
        //ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<Category>(this,
        //        R.layout.spinner_item, allCategories);
        //categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spCategory = findViewById(R.id.spCategoryMain);
        //spCategory.setAdapter(categoryAdapter);
/*       rvToDo.setHasFixedSize(true);

        lm = new LinearLayoutManager(this);
        rvToDo.setLayoutManager(lm);

        toDoAdapter = new ToDoAdapter(entries);
        rvToDo.setAdapter(toDoAdapter);*/
        rvToDo = findViewById(R.id.rvDisplay);
        rvToDo.setAdapter(toDoAdapter);
        rvToDo.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     *  Method to add a new entry to the ToDoList
     */
    public void addEntry()
    {
        Intent creationIntent = new Intent(this, CreationActivity.class);
        startActivityForResult(creationIntent, RC_CREATION_ACTIVITY);
        //startActivity(creationIntent);
    }

    /**
     * Handler for finished Activity-Intents
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_CREATION_ACTIVITY)
        {
            //finished Entry-Creation-Process
            if(resultCode == Activity.RESULT_OK)
            {
                //User pressed OK
                Entry entry = (Entry) data.getSerializableExtra("newEntry");
                Toast.makeText(this, Html.fromHtml("New Entry <i>"+entry+"</i> created")
                        ,Toast.LENGTH_SHORT).show();
                toDoAdapter.addEntry(entry);
                toDoAdapter.notifyDataSetChanged();
            }
        }
    }
}
