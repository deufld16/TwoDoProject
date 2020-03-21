package at.htlkaindorf.twodoprojectmaxi.enums;

public enum ReminderEnum {
    NO_REMINDER("No Reminder", 0),
    DAILY("Daily", 1),
    WEEKLY("Weekly", 2),
    MONTHLY("Monthly", 3),
    YEARLY("Yearly", 4),
    CUSTOM_REMINDER_PERIOD("Specific Interval", 5),
    CUSTOM_REMINDER_DATE("Specific Date", 6);

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
