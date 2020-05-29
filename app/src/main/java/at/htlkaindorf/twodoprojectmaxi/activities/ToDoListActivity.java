package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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
    private CategoryListModel clm = new CategoryListModel();
    private BottomNavigationView vNavBottom;

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

        Proxy.setvNavBottom(vNavBottom);
        Proxy.setToDoAdapter(toDoAdapter);
        Proxy.addNavigationBarListener();
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

        rvToDo = findViewById(R.id.rvDisplay);
        rvToDo.setAdapter(toDoAdapter);
        rvToDo.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper ith = new ItemTouchHelper(ithSimpleCallback);
        ith.attachToRecyclerView(rvToDo);
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
        overridePendingTransition(R.anim.from_bottom_partial, 0);
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
            }
        }
        if(requestCode == toDoAdapter.getRC_MANIPULATION_ACTIVITY())
        {
            if(resultCode == Activity.RESULT_OK) {
                List<Entry> currentEntries = toDoAdapter.getEntries();
                int position = data.getIntExtra("position", 0);
                currentEntries.set(position, entry);
                toDoAdapter.setEntries(currentEntries);
            }
        }
        toDoAdapter.notifyDataSetChanged();
    }
}
