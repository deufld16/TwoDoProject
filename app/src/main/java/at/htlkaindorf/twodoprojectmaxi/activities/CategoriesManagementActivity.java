package at.htlkaindorf.twodoprojectmaxi.activities;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bl.CategroiesAdapter;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.dialogs.AddCategoryFragment;
import at.htlkaindorf.twodoprojectmaxi.dialogs.TextInputFragment;

/**
 * This class is used for displaying the CategeoryManagement Page
 * This page is used for the following things:
 *      -creating Categories
 *      -having an overview of all available categories
 *      -deleting Categories
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class CategoriesManagementActivity extends AppCompatActivity {

    private RecyclerView rvCategroiesAdapter;
    private CategroiesAdapter catAdapter = new CategroiesAdapter(this,  getSupportFragmentManager());
    private Context context = this;

    /**
     * Inflating/creating the displayed GUI
     *      -The navbar is created in order to navigate between activities
     *      -OnClick event for creating new categories
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_management);

        //Navbar Start - to navigate between the Activities
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
        bnv.getMenu().findItem(R.id.navigation_categories).setChecked(true);
        Proxy.setvNavBottom(bnv);
        Proxy.addNavigationBarListener();
        Proxy.setActiveNavActivity(this);
        //Navbar End

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment textInputDlg = new AddCategoryFragment(context, catAdapter);
                textInputDlg.show(getSupportFragmentManager(), "addCategoryFragment");
            }
        });
        rvCategroiesAdapter = findViewById(R.id.rvMngmtCat);
        rvCategroiesAdapter.setAdapter(catAdapter);
        rvCategroiesAdapter.setLayoutManager(new LinearLayoutManager(this));
        catAdapter.setEntries(Proxy.getClm().getAllCategories());
    }

}
