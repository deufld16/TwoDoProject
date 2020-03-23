package at.htlkaindorf.twodoprojectmaxi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.RecyclerViewAdapter;
import at.htlkaindorf.twodoprojectmaxi.bl.ToDoAdapter;

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView rvToDo;
    private ToDoAdapter toDoAdapter = new ToDoAdapter(this);
    private RecyclerView.LayoutManager lm;


    private final int RC_CREATION_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEntry();
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
        /*List<String> allNames = Arrays.asList("Test", "Florian", "Wixxer");
        RecyclerViewAdapter rv = new RecyclerViewAdapter(allNames, null, this);
        rvToDo.setAdapter(rv);
        rvToDo.setLayoutManager(new LinearLayoutManager(this));*/
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
