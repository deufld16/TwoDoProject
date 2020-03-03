package at.htlkaindorf.twodoprojectmaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import at.htlkaindorf.twodoprojectmaxi.activities.EntryCreationActivity;
import at.htlkaindorf.twodoprojectmaxi.activities.WelcomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
        startActivity(welcomeIntent);

        Intent creationIntent = new Intent(this, EntryCreationActivity.class);
        startActivity(creationIntent);
    }
}
