package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;

public class CreationActivity extends AppCompatActivity {

    private TextView vwDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        vwDate = findViewById(R.id.vwDate);
    }

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment(vwDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public View getVwDate() {
        return vwDate;
    }
}
