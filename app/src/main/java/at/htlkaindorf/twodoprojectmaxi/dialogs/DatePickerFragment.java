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

/**
 * creates/inflates a Calender-Dialog which is used to select and return a date
 *
 * @author Maximilian Strohmaier
 * @author Florian Deutschmann
 */
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

    /***
     * inflates the calendar
     *
     * @param savedInstanceState
     * @return calendar-dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /***
     * This method is called when the date is selected.
     * It enables the spinner so that a reminderInterval can be set.
     *
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        vwDate.setText(day+"."+(month+1)+"."+year);
        spReminder.setEnabled(true);
        spReminder.setAdapter(reminderAdapter);
    }
}
