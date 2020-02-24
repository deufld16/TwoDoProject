package at.htlkaindorf.twodoprojectmaxi.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Entry implements Serializable{

    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private List<LocalDateTime> reminderDates;

    private String title;
    private String entryNote;
    private int priorityValue;

    public Entry(int remindingPeriod, LocalDateTime dueDate, String title, String entryNote, int priorityValue){
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.reminderDates = getReminderDatesInInit(remindingPeriod);

        this.title = title;
        this.entryNote = entryNote;
        this.priorityValue = priorityValue;
    }

    public List<LocalDateTime> getReminderDatesInInit(int remindingPeriod){
        while(creationDate.plusDays(remindingPeriod).isBefore(dueDate)){
            creationDate = creationDate.plusDays(remindingPeriod);
            reminderDates.add(creationDate);
        }
        return reminderDates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<LocalDateTime> getReminderDates() {
        return reminderDates;
    }

    public void setReminderDates(List<LocalDateTime> reminderDates) {
        this.reminderDates = reminderDates;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEntryNote() {
        return entryNote;
    }

    public void setEntryNote(String entryNote) {
        this.entryNote = entryNote;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    public void setPriorityValue(int priorityValue) {
        this.priorityValue = priorityValue;
    }
}

