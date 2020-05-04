package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import at.htlkaindorf.twodoprojectmaxi.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private TextView vwDate;
    private Spinner spReminder;
    private ArrayAdapter<String> reminderAdapter;

    public DatePickerFragment(TextView vwDate, Spinner spReminder, ArrayAdapter<String> reminderAdapter)
    {
        this.vwDate = vwDate;
        this.spReminder = spReminder;
        this.reminderAdapter = reminderAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        vwDate.setText(day+"."+(month+1)+"."+year);
        spReminder.setEnabled(true);
        spReminder.setAdapter(reminderAdapter);
        //Maxi hier background auf normal
    }
}
