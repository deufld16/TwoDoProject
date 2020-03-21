package at.htlkaindorf.twodoprojectmaxi.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.enums.ReminderEnum;

public class Entry implements Serializable{

    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private List<LocalDateTime> reminderDates;

    private String title;
    private String entryNote;
    private int priorityValue;
    private Category category;

    public Entry(int reminderID, LocalDateTime dueDate, String title, String entryNote, int priorityValue, Category category){
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.reminderDates = getReminderDatesInInit(reminderID);

        this.title = title;
        this.entryNote = entryNote;
        this.priorityValue = priorityValue;
        this.category = category;
    }

    public List<LocalDateTime> getReminderDatesInInit(int reminderID){
        if(reminderID != 0 && reminderID != 5 && reminderID != 6){
            int plusNumber = 0;
            int type = 0;
            switch (reminderID){
                case 1: plusNumber = 1;
                    break;
                case 2: plusNumber = 7;
                    break;
                case 3: plusNumber = 1;
                        type = 1;
                    break;
                case 4: plusNumber = 1;
                        type = 2;
                    break;
            }
            if(type == 0){
                while(creationDate.plusDays(plusNumber).isBefore(dueDate)){
                    creationDate = creationDate.plusDays(plusNumber);
                    reminderDates.add(creationDate);
                }
            }else if(type == 1){
                while(creationDate.plusMonths(plusNumber).isBefore(dueDate)){
                    creationDate = creationDate.plusMonths(plusNumber);
                    reminderDates.add(creationDate);
                }
            }else if(type == 2){
                while(creationDate.plusYears(plusNumber).isBefore(dueDate)){
                    creationDate = creationDate.plusYears(plusNumber);
                    reminderDates.add(creationDate);
                }
            }
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

