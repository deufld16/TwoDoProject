package at.htlkaindorf.twodoprojectmaxi.activities;

import android.content.Context;
import android.os.Bundle;

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

public class CategoriesManagementActivity extends AppCompatActivity {

    private RecyclerView rvCategroiesAdapter;
    private CategroiesAdapter catAdapter = new CategroiesAdapter(this,  getSupportFragmentManager());
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_management);

        //Navbar Start
        Proxy.getvNavBottom().getMenu().findItem(R.id.navigation_categories).setChecked(true);
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
