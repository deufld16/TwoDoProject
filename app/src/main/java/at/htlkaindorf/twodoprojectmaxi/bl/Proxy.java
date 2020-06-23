package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.CategoriesManagementActivity;
import at.htlkaindorf.twodoprojectmaxi.activities.ToDoListActivity;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.SoundRecorder;

/**
 * Proxy-class for a central access of specific data
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class Proxy {
    private static CategoryListModel clm;
    private static BottomNavigationView vNavBottom;
    private static AppCompatActivity activeNavActivity;
    private static ToDoAdapter toDoAdapter;
    private static Context context;
    private static Context languageContext;
    private static SoundRecorder soundRecorder = new SoundRecorder();
    private static VoiceRecordAdapter vra;
    private static final Map<Character, Double> CHARACTER_WIDTH;

    static
    {
        CHARACTER_WIDTH = new HashMap<>();
        CHARACTER_WIDTH.put('a', 2.5);
        CHARACTER_WIDTH.put('b', 2.5);
        CHARACTER_WIDTH.put('c', 2.);
        CHARACTER_WIDTH.put('d', 2.5);
        CHARACTER_WIDTH.put('e', 2.5);
        CHARACTER_WIDTH.put('f', 1.5);
        CHARACTER_WIDTH.put('g', 2.);
        CHARACTER_WIDTH.put('h', 2.5);
        CHARACTER_WIDTH.put('i', 1.);
        CHARACTER_WIDTH.put('j', 1.);
        CHARACTER_WIDTH.put('k', 2.);
        CHARACTER_WIDTH.put('l', 1.);
        CHARACTER_WIDTH.put('m', 3.5);
        CHARACTER_WIDTH.put('n', 2.5);
        CHARACTER_WIDTH.put('o', 2.5);
        CHARACTER_WIDTH.put('p', 2.5);
        CHARACTER_WIDTH.put('q', 2.5);
        CHARACTER_WIDTH.put('r', 1.5);
        CHARACTER_WIDTH.put('s', 1.5);
        CHARACTER_WIDTH.put('t', 1.5);
        CHARACTER_WIDTH.put('u', 2.5);
        CHARACTER_WIDTH.put('v', 2.);
        CHARACTER_WIDTH.put('w', 3.);
        CHARACTER_WIDTH.put('x', 2.);
        CHARACTER_WIDTH.put('y', 2.);
        CHARACTER_WIDTH.put('z', 1.5);
        CHARACTER_WIDTH.put('A', 2.5);
        CHARACTER_WIDTH.put('B', 2.5);
        CHARACTER_WIDTH.put('C', 2.5);
        CHARACTER_WIDTH.put('D', 2.5);
        CHARACTER_WIDTH.put('E', 2.);
        CHARACTER_WIDTH.put('F', 2.);
        CHARACTER_WIDTH.put('G', 3.);
        CHARACTER_WIDTH.put('H', 2.5);
        CHARACTER_WIDTH.put('I', 1.);
        CHARACTER_WIDTH.put('J', 1.5);
        CHARACTER_WIDTH.put('K', 2.5);
        CHARACTER_WIDTH.put('L', 2.);
        CHARACTER_WIDTH.put('M', 4.);
        CHARACTER_WIDTH.put('N', 3.);
        CHARACTER_WIDTH.put('O', 3.);
        CHARACTER_WIDTH.put('P', 2.5);
        CHARACTER_WIDTH.put('Q', 3.);
        CHARACTER_WIDTH.put('R', 2.5);
        CHARACTER_WIDTH.put('S', 2.);
        CHARACTER_WIDTH.put('T', 2.);
        CHARACTER_WIDTH.put('U', 3.);
        CHARACTER_WIDTH.put('V', 2.5);
        CHARACTER_WIDTH.put('W', 4.);
        CHARACTER_WIDTH.put('X', 2.5);
        CHARACTER_WIDTH.put('Y', 2.);
        CHARACTER_WIDTH.put('Z', 2.);
        CHARACTER_WIDTH.put('0', 2.5);
        CHARACTER_WIDTH.put('1', 2.);
        CHARACTER_WIDTH.put('2', 2.5);
        CHARACTER_WIDTH.put('3', 2.5);
        CHARACTER_WIDTH.put('4', 2.5);
        CHARACTER_WIDTH.put('5', 2.5);
        CHARACTER_WIDTH.put('6', 2.5);
        CHARACTER_WIDTH.put('7', 2.5);
        CHARACTER_WIDTH.put('8', 2.5);
        CHARACTER_WIDTH.put('9', 2.5);
    }

    public static Map<Character, Double> getCharacterWidth() {
        return CHARACTER_WIDTH;
    }

    public static SoundRecorder getSoundRecorder() {
        return soundRecorder;
    }

    public static void setSoundRecorder(SoundRecorder soundRecorder) {
        Proxy.soundRecorder = soundRecorder;
    }

    public static Context getLanguageContext() {
        return languageContext;
    }

    public static void setLanguageContext(Context languageContext) {
        Proxy.languageContext = languageContext;
    }

    public static VoiceRecordAdapter getVra() {
        return vra;
    }

    public static void setVra(VoiceRecordAdapter vra) {
        Proxy.vra = vra;
    }

    public static Context getContext(){
        return context;
    }

    public static void setContext(Context context){
        Proxy.context = context;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storageDir.exists())
        {
            try {
                storageDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     * Method to set the actions that must be taken after clicking
     * on specific items of the bottom navigation bar
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
    }

    public static boolean widthOk(String msg, double targetWidth)
    {
        double actualWidth = 0.;
        for (int i = 0; i < msg.length(); i++)
        {
            char character = msg.charAt(i);
            double characterW = 2.;
            if(CHARACTER_WIDTH.containsKey(character))
            {
                characterW = CHARACTER_WIDTH.get(character);
            }
            actualWidth += characterW;
        }

        if(actualWidth <= targetWidth)
        {
            return true;
        }

        return false;
    }
}
