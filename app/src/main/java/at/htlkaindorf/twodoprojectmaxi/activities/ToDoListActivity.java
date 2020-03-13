package at.htlkaindorf.twodoprojectmaxi.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.ToDoAdapter;

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView rvToDo;
    private RecyclerView.Adapter toDoAdapter;
    private RecyclerView.LayoutManager lm;

    private List<Entry> entries = new LinkedList<>();

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
    }

    public void addEntry()
    {
        Intent creationIntent = new Intent(this, CreationActivity.class);
        startActivity(creationIntent);
    }

}
