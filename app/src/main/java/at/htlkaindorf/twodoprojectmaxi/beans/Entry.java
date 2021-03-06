package at.htlkaindorf.twodoprojectmaxi.beans;

import android.app.PendingIntent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import at.htlkaindorf.twodoprojectmaxi.notificationManager.NotificationHelper;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;

/**
 * Beans class for the entries
 *
 * @author Florian Deutschmann
 */

public class Entry implements Serializable{

    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private List<LocalDateTime> reminderDates = new LinkedList<>();

    private String title;
    private String entryNote;
    private int priorityValue;
    private Category category;
    private Status status;
    private int reminderID;

    private List<String> allAudioFileLocations = new LinkedList<>();
    private List<String> allPhotoLocations = new LinkedList<>();

    private int request_id;

    public Entry(int reminderID, LocalDateTime dueDate, String title, String entryNote, int priorityValue, Category category, int request_id){
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.reminderDates = getReminderDatesInInit(reminderID);

        this.title = title;
        this.entryNote = entryNote;
        this.priorityValue = priorityValue;
        this.category = category;
        this.status = Status.Working;
        this.reminderID = reminderID;

        this.request_id = request_id;
        NotificationHelper.startAlarm(this);
        Log.d("NOTIFICATION_FIX", "setParameters: 2" + getReminderDates());
    }

    public void setParameters(int reminderID, LocalDateTime dueDate, String title, String entryNote, int priorityValue, Category category, int request_id){
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.reminderDates = getReminderDatesInInit(reminderID);

        this.title = title;
        this.entryNote = entryNote;
        this.priorityValue = priorityValue;
        this.category = category;
        this.status = Status.Working;
        this.reminderID = reminderID;

        this.request_id = request_id;
        NotificationHelper.startAlarm(this);

        Log.d("NOTIFICATION_FIX", "setParameters: " + getReminderDates());
    }

    public Entry(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return request_id == entry.request_id;
    }

    /**
     * Method that calculates all the dates on which a reminding message should be received
     * @param reminderID
     * @return
     */



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
                if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                        .isBefore(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0).plusDays(1)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate.minusDays(1));
                    }
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate);
                    }
                }
            }else if(type == 1){
                while(creationDate.plusMonths(plusNumber).isBefore(dueDate)){
                    creationDate = creationDate.plusMonths(plusNumber);
                    reminderDates.add(creationDate);
                }
                if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                        .isBefore(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0).plusDays(1)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate.minusDays(1));
                    }
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate);
                    }
                }
            }else if(type == 2){
                while(creationDate.plusYears(plusNumber).isBefore(dueDate)){
                    creationDate = creationDate.plusYears(plusNumber);
                    reminderDates.add(creationDate);
                }
                if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                        .isBefore(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0).plusDays(1)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate.minusDays(1));
                    }
                    if(!LocalDateTime.of(dueDate.getYear(), dueDate.getMonth().getValue(), dueDate.getDayOfMonth(),0,0)
                            .isEqual(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth().getValue(), LocalDateTime.now().getDayOfMonth(), 0,0))){
                        reminderDates.add(dueDate);
                    }
                }
            }
        }else if(reminderID == 0){
            reminderDates.add(dueDate);
        }
        return reminderDates;
    }

    public boolean needsReminder(){
        Log.d("RestartAppReceiver", title + ": " + dueDate + "\n" + LocalDateTime.now());
        for (LocalDateTime reminder:
             new LinkedList<>(reminderDates)) {
            if(reminder.isBefore(LocalDateTime.now().minusDays(1))){
                reminderDates.remove(reminder);
            }
        }
        return dueDate.isAfter(LocalDateTime.now());
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }


    public int getReminderID() {
        return reminderID;
    }

    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDueDate() {
        if(dueDate == null){
            return LocalDateTime.of(1900, Month.JANUARY, 1, 0,0);
        }
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getAllAudioFileLocations() {
        return allAudioFileLocations;
    }

    public void setAllAudioFileLocations(List<String> allAudioFileLocations) {
        this.allAudioFileLocations = allAudioFileLocations;
    }

    public List<String> getAllPhotoLocations() {
        return allPhotoLocations;
    }

    public void setAllPhotoLocations(List<String> allPhotoLocations) {
        this.allPhotoLocations = allPhotoLocations;
    }
}

