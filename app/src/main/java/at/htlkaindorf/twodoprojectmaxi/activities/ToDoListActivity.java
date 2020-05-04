package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.bl.ToDoAdapter;
import at.htlkaindorf.twodoprojectmaxi.enums.SortingType;

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView rvToDo;
    private EditText etSearchbar;
    private Spinner spSortingMain;
    private Spinner spCategoryMain;
    private ToDoAdapter toDoAdapter = new ToDoAdapter(this);
    private RecyclerView.LayoutManager lm;
    private CategoryListModel clm = new CategoryListModel();
    private BottomNavigationView vNavBottom;


    private final int RC_CREATION_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        //Navbar Start
        vNavBottom = findViewById(R.id.bottom_navigation);
        vNavBottom.getMenu().findItem(R.id.navigation_to_do).setChecked(true);
        Proxy.setvNavBottom(vNavBottom);
        Proxy.addNavigationBarListener(toDoAdapter);
        Proxy.setMainNavActivity(this);
        Proxy.setActiveNavActivity(this);
        //Navbar End

        Proxy.setClm(clm);

        try {
            boolean entriesFileExists = false;
            boolean categoriesFileExists = false;
            for (File file:
                 getFilesDir().listFiles()) {
                Log.d("ERROR", "onCreate: " + file.getName()) ;
                if(file.getName().equalsIgnoreCase("entries.ser")){
                    entriesFileExists = true;
                }
                if(file.getName().equalsIgnoreCase("categories.ser")){
                    categoriesFileExists = true;
                }
            }
            if(entriesFileExists){
                toDoAdapter.loadEntries();
            }
            if(categoriesFileExists){
                clm.loadCategories(this);
            }else{
                clm.addCategory(new Category("ADD CATEGORY"));
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
                toDoAdapter.filter();
            }
        });

        List<Category> allCategories = new LinkedList<>(Proxy.getClm().getAllCategories());
        List<String> allFilterCategories = new LinkedList<>();
        for (Entry filterCategory:
             toDoAdapter.getEntries()) {
            if(!allFilterCategories.contains(filterCategory.getCategory().getCategory_name())){
                allFilterCategories.add(filterCategory.getCategory().getCategory_name());
            }
        }
        allFilterCategories.add(0, "All Categories");
        allCategories.removeIf(category -> category.getCategory_name().equalsIgnoreCase("ADD CATEGORY"));
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
               R.layout.spinner_item, allFilterCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoryMain = findViewById(R.id.spCategoryMain);
        spCategoryMain.setAdapter(categoryAdapter);
        spCategoryMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toDoAdapter.setFilter(etSearchbar.getText().toString());
                toDoAdapter.setFilterCategory(spCategoryMain.getSelectedItem().toString());
                toDoAdapter.filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<SortingType> allSortingTypes = Arrays.asList(SortingType.values());
        List<String> allSortingTypesDisplayName = new LinkedList<>();
        for (SortingType sortingType:
             allSortingTypes) {
            allSortingTypesDisplayName.add(sortingType.getDisplay_text());
        }

        ArrayAdapter<String> sortingTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, allSortingTypesDisplayName);
        sortingTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortingMain = findViewById(R.id.spSortingMain);
        spSortingMain.setAdapter(sortingTypeAdapter);
        spSortingMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toDoAdapter.setFilter(etSearchbar.getText().toString());
                toDoAdapter.setFilterEnum(spSortingMain.getSelectedItem().toString());
                toDoAdapter.filter();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
