package at.htlkaindorf.twodoprojectmaxi.enums;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/**
 * Enum for the Reminder Spinner with the text that should be displayed and a number which is used for distinguishing the reminder dates
 */
public enum ReminderEnum {
    NO_REMINDER(Proxy.getContext().getString(R.string.add_entry_page_no_reminder), 0),
    DAILY(Proxy.getContext().getString(R.string.add_entry_page_daily_reminder), 1),
    WEEKLY(Proxy.getContext().getString(R.string.add_entry_page_weekly_reminder), 2),
    MONTHLY(Proxy.getContext().getString(R.string.add_entry_page_monthly_reminder), 3),
    YEARLY(Proxy.getContext().getString(R.string.add_entry_page_yearly_reminder), 4),
    CUSTOM_REMINDER_PERIOD(Proxy.getContext().getString(R.string.add_entry_page_specific_interval), 5),
    CUSTOM_REMINDER_DATE(Proxy.getContext().getString(R.string.add_entry_page_specific_reminder), 6);

    private int reminder_id;
    private String reminder_identifierString;

    ReminderEnum(String reminder_identifierString, int reminder_id) {
        this.reminder_id = reminder_id;
        this.reminder_identifierString = reminder_identifierString;
    }

    public int getReminder_id() {
        return reminder_id;
    }

    public void setReminder_id(int reminder_id) {
        this.reminder_id = reminder_id;
    }

    public String getReminder_identifierString() {
        return reminder_identifierString;
    }

    public void setReminder_identifierString(String reminder_identifierString) {
        this.reminder_identifierString = reminder_identifierString;
    }
}
