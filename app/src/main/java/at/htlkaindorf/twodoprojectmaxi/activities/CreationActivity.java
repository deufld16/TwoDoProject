package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;

public class CreationActivity extends AppCompatActivity {

    private TextView vwDate;
    private Button btOk;
    private Button btCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        vwDate = findViewById(R.id.vwDate);

        btOk = findViewById(R.id.btCreationOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Entry saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btCancel = findViewById(R.id.btCreationCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
