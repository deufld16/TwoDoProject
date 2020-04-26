package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.CategoriesManagementActivity;

public class Proxy {
    private static CategoryListModel clm;
    private static BottomNavigationView vNavBottom;
    private static AppCompatActivity activeNavActivity;
    private static AppCompatActivity mainNavActivity;

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

    public static AppCompatActivity getMainNavActivity() {
        return mainNavActivity;
    }

    public static void setMainNavActivity(AppCompatActivity mainNavActivity) {
        Proxy.mainNavActivity = mainNavActivity;
    }

    public static BottomNavigationView getvNavBottom() {
        return vNavBottom;
    }

    public static void setvNavBottom(BottomNavigationView vNavBottom) {
        Proxy.vNavBottom = vNavBottom;
    }

    public static void addNavigationBarListener()
    {
        vNavBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(activeNavActivity, activeNavActivity+" - "+mainNavActivity
                        ,Toast.LENGTH_LONG).show();
                if(!activeNavActivity.equals(mainNavActivity)) {
                    activeNavActivity.finish();
                }
                switch (item.getItemId())
                {
                    case R.id.navigation_categories:
                        Intent categoriesIntent = new Intent(mainNavActivity, CategoriesManagementActivity.class);
                        mainNavActivity.startActivity(categoriesIntent);
                        return true;
                    case R.id.navigation_deleted:
                        return true;
                    case R.id.navigation_to_do:
                        return true;
                    case R.id.navigation_done:
                        return true;
                }
                return false;
            }
        });
        // for more information see https://androidwave.com/bottom-navigation-bar-android-example/
    }
}
