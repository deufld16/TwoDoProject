package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.bl.ToDoAdapter;
import at.htlkaindorf.twodoprojectmaxi.enums.SortingType;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * This is the starting activity. It contains a RecyclerView to display the entries and a plus button to add new Entries.
 * There is also a navbar to navigate to the other activities.
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView rvToDo;
    private EditText etSearchbar;
    private Spinner spSortingMain;
    private Spinner spCategoryMain;
    private ToDoAdapter toDoAdapter = new ToDoAdapter(this);
    private RecyclerView.LayoutManager lm;
    private CategoryListModel clm;
    private BottomNavigationView vNavBottom;
    private static String [] supported_languages;
    private static final String [] SUPPORTED_LANGUAGES_PREFIX = {"de", "en", "fr"};
    private String currentLanguage = "de";

    private final int RC_CREATION_ACTIVITY = 2;


    /**
     * This is used to enable swiping for setting the status of an entry to done/deleted.
     */
    ItemTouchHelper.SimpleCallback ithSimpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * This is used to set the state of the entry depending on the swipe direction and its current state
         *      -STATE DOING
         *          -swipe right: sets state to done
         *          -swipe left: sets state to deleted
         *      -STATE DELETED
         *          -swipe right: sets state to doing/restores the entry
         *          -swipe left: ultimately deletes an entry
         *      -STATE DONE
         *          -swipe right: ultimately removes the entry because its done done
         *          -swipe left: sets state to doing
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Entry swipedEntry = toDoAdapter.getFilteredEntries().get(position);
            int absPosition = toDoAdapter.getEntries().indexOf(swipedEntry);

            if(direction == ItemTouchHelper.LEFT)
            {
                //von rechts nach links
                switch (swipedEntry.getStatus())
                {
                    case Working:
                        toDoAdapter.deleteEntry(absPosition, swipedEntry);
                        break;
                    case Deleted:
                        toDoAdapter.releaseEntry(absPosition, swipedEntry);
                        break;
                    case Done:
                        toDoAdapter.restoreEntry(absPosition, swipedEntry);
                        break;
                }
            }
            else if(direction == ItemTouchHelper.RIGHT)
            {
                //von links nach rechts
                switch (swipedEntry.getStatus())
                {
                    case Working:
                        toDoAdapter.doEntry(absPosition, swipedEntry);
                        break;
                    case Deleted:
                        toDoAdapter.restoreEntry(absPosition, swipedEntry);
                        break;
                    case Done:
                        toDoAdapter.releaseEntry(absPosition, swipedEntry);
                        break;
                }
            }
            toDoAdapter.filter();
            try {
                toDoAdapter.saveEntries();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Method to display a specific icon while swiping an entry in order to
         * enhance the user experience and receive a visual feedback
         *
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         * @param dY
         * @param actionState
         * @param isCurrentlyActive
         */
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState,
                                boolean isCurrentlyActive) {
            Status activeStatusView = toDoAdapter.getDisplayStatus();
            int iconLeftSwipe = 0;
            int iconRightSwipe = 0;
            switch (activeStatusView)
            {
                case Working:
                    iconLeftSwipe = R.drawable.ic_delete_white_60dp;
                    iconRightSwipe = R.drawable.ic_done_white_60dp;
                    break;
                case Deleted:
                    iconLeftSwipe = R.drawable.ic_delete_forever_white_60dp;
                    iconRightSwipe = R.drawable.ic_restore_white_60dp;
                    break;
                case Done:
                    iconLeftSwipe = R.drawable.ic_restore_white_60dp;
                    iconRightSwipe = R.drawable.ic_done_all_wihte_60dp;
                    break;
            }
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(iconLeftSwipe)
                    .addSwipeRightActionIcon(iconRightSwipe)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    /**
     * Fetches the required components via the findViewById method
     *      -sets the navbar to navigate between the activities
     *      -sets the CategoryListModel in the Proxy class to make it globally available to all classes
     *      -checks if there is a Category/Entry serilized file which can be read to restore the entries/categories
     *          -if found it is loaded
     *          -if not the default state of the programm is loaded
     *      -Button to switch to the CreationActivity to add an Entry
     *      -TextChangeListener on the SearchBar for filtering purposes
     *      -set the Adapters for the spinners so that they can display the values as intended
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        supported_languages = new String[3];
        supported_languages[0] = getString(R.string.lang_german);
        supported_languages[1] = getString(R.string.lang_english);
        supported_languages[2] = getString(R.string.lang_french);
        ImageView ivPopoupmenu = findViewById(R.id.ivPopupMenu);
        ivPopoupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        //Navbar Start
        vNavBottom = findViewById(R.id.bottom_navigation);
        vNavBottom.getMenu().findItem(R.id.navigation_to_do).setChecked(true);

        Intent theIntent = getIntent();
        Status displayStatus = (Status) theIntent.getSerializableExtra("displayStatus");
        if(displayStatus != null)
        {
            toDoAdapter.switchView(displayStatus);
            switch (displayStatus)
            {
                case Deleted:
                    vNavBottom.getMenu().findItem(R.id.navigation_deleted).setChecked(true);
                    break;
                case Done:
                    vNavBottom.getMenu().findItem(R.id.navigation_done).setChecked(true);
                    break;
            }
        }
        String currentLanguage = (String) theIntent.getStringExtra("currentLang");
        if(currentLanguage != null){
            this.currentLanguage = currentLanguage;
        }
        Proxy.setvNavBottom(vNavBottom);
        Proxy.setToDoAdapter(toDoAdapter);
        Proxy.addNavigationBarListener();
        Proxy.setActiveNavActivity(this);
        //Navbar End
        Proxy.setLanguageContext(this);
        clm = new CategoryListModel();
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
                clm.addCategory(new Category(getString(R.string.add_entry_page_add_category)));
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

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

        List<String> allFilterCategories = getFilterCategories();
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

        List<String> allSortingTypesDisplayName = Arrays.asList(getString(R.string.sorting_1), getString(R.string.sorting_2),
                getString(R.string.sorting_3), getString(R.string.sorting_4));
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

        rvToDo = findViewById(R.id.rvDisplay);
        rvToDo.setAdapter(toDoAdapter);
        rvToDo.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper ith = new ItemTouchHelper(ithSimpleCallback);
        ith.attachToRecyclerView(rvToDo);

        //loadLocale();
        TextView test = findViewById(R.id.tvMainListTitle);
        test.setText(R.string.app_name);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(getResources().getString(R.string.app_name));

    }

    private List<String> getFilterCategories(){
        List<Category> allCategories = new LinkedList<>(Proxy.getClm().getAllCategories());
        List<String> allFilterCategories = new LinkedList<>();
        for (Entry filterCategory:
                toDoAdapter.getEntries()) {
            Log.d("MLANGUAGE", "onCreate: " + filterCategory.getCategory().getCategory_name());
            if(!allFilterCategories.contains(filterCategory.getCategory().getCategory_name())){
                allFilterCategories.add(filterCategory.getCategory().getCategory_name());
            }
        }
        allFilterCategories.add(0, getString(R.string.all_categories));
        allCategories.removeIf(category -> category.getCategory_name().equalsIgnoreCase(getString(R.string.add_entry_page_add_category)));

        return allFilterCategories;
    }

    /**
     *  Method to display the menu when clicking on the menu item
     */
    private void showPopupMenu(View view) {
        PopupMenu pm = new PopupMenu(this, view);
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return handleMenuSelection(menuItem);
            }
        });
        pm.inflate(R.menu.popup_menu);
        pm.show();
    }

    /**
     * Method to handle the selection of an item in the popup-menu
     * @param menuItem
     * @return success
     */
    private boolean handleMenuSelection(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.mi_transfer:
                //Transfer via Bluetooth
                Intent transferIntent = new Intent(this, TransferActivity.class);
                startActivity(transferIntent);
                overridePendingTransition(0, R.anim.from_left);
                return true;
            case R.id.mi_change_language:
                showChangeLanguageDialog();
                return true;
        }
        return false;
    }

    /**
     *  Method to add a new entry to the ToDoList
     */
    public void addEntry()
    {
        Intent creationIntent = new Intent(this, CreationActivity.class);
        startActivityForResult(creationIntent, RC_CREATION_ACTIVITY);
        overridePendingTransition(0, R.anim.from_left);
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
        Entry entry = null;
        if(data != null) {
            entry = (Entry) data.getSerializableExtra("newEntry");
        }
        if(requestCode == RC_CREATION_ACTIVITY)
        {
            //finished Entry-Creation-Process
            if(resultCode == Activity.RESULT_OK)
            {
                //User pressed OK
                Toast.makeText(this, Html.fromHtml("New Entry <i>"+entry+"</i> created")
                        ,Toast.LENGTH_SHORT).show();
                toDoAdapter.addEntry(entry);
                List<String> allFilterCategories = getFilterCategories();
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                        R.layout.spinner_item, allFilterCategories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategoryMain = findViewById(R.id.spCategoryMain);
                spCategoryMain.setAdapter(categoryAdapter);
            }
        }
        if(requestCode == toDoAdapter.getRC_MANIPULATION_ACTIVITY())
        {
            if(resultCode == Activity.RESULT_OK) {
                Log.d("ERRORFIXING", "onActivityResult: " + "Eintrag wurde bearbeitet");
                List<Entry> currentEntries = new LinkedList<>(toDoAdapter.getEntries());
                int position = data.getIntExtra("position", 0);
                currentEntries.set(position, entry);
                toDoAdapter.setEntries(currentEntries);
            }
        }

        toDoAdapter.notifyDataSetChanged();
    }


    private void showChangeLanguageDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ToDoListActivity.this);
        mBuilder.setTitle(getString(R.string.choose_language_title));
        int checkedItem = -1;
        for (int i = 0; i < SUPPORTED_LANGUAGES_PREFIX.length; i++) {
            if(currentLanguage.equals(SUPPORTED_LANGUAGES_PREFIX[i])){
                checkedItem = i;
            }
        }
        mBuilder.setSingleChoiceItems(supported_languages, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        //German
                        setLocale("de");
                        break;
                    case 1:
                        //English
                        setLocale("en");
                        break;
                    case 2:
                        //French
                        setLocale("fr");
                        break;
                }
                dialog.dismiss();
            }
        });
        mBuilder.show();
    }

    private void setLocale(String languageIdentifier){
        if(!currentLanguage.equals(languageIdentifier)){
            Category add_category = null;
            Category default_category = null;

            for (Category cat:
                    Proxy.getClm().getAllCategories()) {
                if(cat.getCategory_name().equalsIgnoreCase(getString(R.string.add_entry_page_add_category))){
                    add_category = cat;
                }else if(cat.getCategory_name().equalsIgnoreCase(getString(R.string.add_entry_page_default))){
                    default_category = cat;
                }
            }

            Log.d("MLANGUAGE", "setLocale: " + add_category + " - " + default_category + " - " + languageIdentifier);

            Locale locale = new Locale(languageIdentifier);
            Locale.setDefault(locale);
            Resources res = getResources();
            Configuration config = res.getConfiguration();
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            Proxy.getClm().languageChanged(add_category, default_category);
            finish();
            Intent intent = getIntent();
            Intent intent1 = new Intent(this, CreationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("currentLang", languageIdentifier);
            finish();
            startActivity(getIntent());
            //toDoAdapter.switchView(Status.Working);
        }
    }
}
