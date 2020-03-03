package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import at.htlkaindorf.twodoprojectmaxi.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private TextView vwDate;

    public DatePickerFragment(TextView vwDate)
    {
        this.vwDate = vwDate;
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
    }
}
