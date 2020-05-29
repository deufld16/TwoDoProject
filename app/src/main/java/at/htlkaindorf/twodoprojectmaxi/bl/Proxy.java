package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.atomic.AtomicInteger;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.CategoriesManagementActivity;
import at.htlkaindorf.twodoprojectmaxi.activities.ToDoListActivity;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;

public class Proxy {
    private static CategoryListModel clm;
    private static BottomNavigationView vNavBottom;
    private static AppCompatActivity activeNavActivity;
    private static ToDoAdapter toDoAdapter;
    private static Context context;

    public static Context getContext(){
        return context;
    }

    public static void setContext(Context context){
        Proxy.context = context;
    }

    public static CategoryListModel getClm() {
        return clm;
    }
    
    public static void setClm(CategoryListModel clm) {
        Proxy.clm = clm;
    }

    public static Object getActiveNavActivity() {
        return activeNavActivity;
    }

    public static void setActiveNavActivity(AppCompatActivity activeNavActivity) {
        Proxy.activeNavActivity = activeNavActivity;
    }

    public static BottomNavigationView getvNavBottom() {
        return vNavBottom;
    }

    public static void setvNavBottom(BottomNavigationView vNavBottom) {
        Proxy.vNavBottom = vNavBottom;
    }

    public static ToDoAdapter getToDoAdapter() {
        return toDoAdapter;
    }

    public static void setToDoAdapter(ToDoAdapter toDoAdapter) {
        Proxy.toDoAdapter = toDoAdapter;
    }

    /**
     *
     */
    public static void addNavigationBarListener()
    {
        vNavBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.navigation_categories
                    && !(activeNavActivity instanceof CategoriesManagementActivity))
                {
                    Intent categoriesIntent = new Intent(activeNavActivity, CategoriesManagementActivity.class);
                    activeNavActivity.startActivity(categoriesIntent);
                    activeNavActivity.overridePendingTransition(0, R.anim.from_right);
                    return true;
                }

                if(itemId == R.id.navigation_deleted
                    || itemId == R.id.navigation_to_do
                    || itemId == R.id.navigation_done)
                {
                    Status status = null;
                    switch (itemId)
                    {
                        case R.id.navigation_deleted:
                            status = toDoAdapter.switchView(Status.Deleted);
                            break;
                        case R.id.navigation_to_do:
                            status = toDoAdapter.switchView(Status.Working);
                            break;
                        case R.id.navigation_done:
                            status = toDoAdapter.switchView(Status.Done);
                            break;
                    }

                    if(!(activeNavActivity instanceof ToDoListActivity)) {
                        Intent toDoIntent = new Intent(activeNavActivity, ToDoListActivity.class);
                        toDoIntent.putExtra("displayStatus", status);
                        activeNavActivity.startActivity(toDoIntent);
                        activeNavActivity.overridePendingTransition(0, R.anim.from_left);
                    }

                    return true;
                }

                return false;
            }
        });
        // for more information see https://androidwave.com/bottom-navigation-bar-android-example/
    }
}
