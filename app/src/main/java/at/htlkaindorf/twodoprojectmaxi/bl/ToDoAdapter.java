package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.ManipulationActivity;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.SortingType;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;
import at.htlkaindorf.twodoprojectmaxi.notificationManager.NotificationHelper;


/**
 * Adapter-class for the To-Do-list-entry-recycler-view
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private static final String FILE_NAME = "entries.ser";

    private List<Entry> entries = new LinkedList<>();
    private List<Entry> filteredEntries = new LinkedList<>();
    private Context context;
    private String filter = "";
    private Enum filterEnum = null;
    private String filterCategory = "";
    private Status displayStatus = Status.Working;

    private final int RC_MANIPULATION_ACTIVITY = 3;

    public ToDoAdapter(Context context) {
        this.context = context;
    }

    public Status getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Status displayStatus) {
        this.displayStatus = displayStatus;
    }

    /***
     * Method to add a new entry to the To-Do-List
     *
     * @param entry
     * @return sucess of adding process
     */
    public boolean addEntry(Entry entry){
        if(!entries.contains(entry)){
            entries.add(entry);
            filter();
            try{
                saveEntries();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<Entry> getFilteredEntries() {
        return filteredEntries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
        filter();
        try{
            saveEntries();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /***
     * Inflates the layout for the To-Do-list-entries
     *
     * @param parent
     * @param viewType
     * @return current ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_do_list,
                parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    /***
     * Binds specific data to the representation of the corresponding To-Do-list-entry
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Entry entry = filteredEntries.get(position);
        holder.tvEntryTitle.setText(entry.getTitle());
        holder.tvEntryCategory.setText(entry.getCategory().getCategory_name());
        holder.tvEntryDueDate.setText(entry.getDueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        String priority = "";
        for (PriorityEnum prio :
                PriorityEnum.values()) {
            if (prio.getPrioirty_value() == entry.getPriorityValue()) {
                priority = prio.getPrioirty_text();
            }
        }
        holder.tvEntryPriority.setText(priority);
        holder.clEntryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
        });
        holder.ivEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEntry(entry);
            }
        });
    }

    /***
     * Method to initiate the process of editing an existing entry
     *
     * @param entry
     */
    private void editEntry(Entry entry)
    {
        AppCompatActivity srcActivity = (AppCompatActivity) context;
        Intent editIntent = new Intent(srcActivity, ManipulationActivity.class);
        editIntent.putExtra("oldEntry", entry);
        int indexOfEntry = filteredEntries.indexOf(entry);
        editIntent.putExtra("entryPos", indexOfEntry);
        srcActivity.startActivityForResult(editIntent, RC_MANIPULATION_ACTIVITY);
        srcActivity.overridePendingTransition(0, R.anim.from_left);
    }

    public int getRC_MANIPULATION_ACTIVITY() {
        return RC_MANIPULATION_ACTIVITY;
    }

    @Override
    public int getItemCount() {
        return filteredEntries.size();
    }

    /***
     * Contains all necessary components for displaying a To-Do-list-entry
     *
     * @author Florian Deutschmann
     * @author Maximilian Strohmaier
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout clEntryLayout;
        private TextView tvEntryTitle;
        private TextView tvEntryCategory;
        private TextView tvEntryDueDate;
        private TextView tvEntryPriority;
        private ImageView ivEditItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clEntryLayout = itemView.findViewById(R.id.cl_entry_layout);
            tvEntryTitle = itemView.findViewById(R.id.tv_entry_title);
            tvEntryCategory = itemView.findViewById(R.id.tv_entry_category);
            tvEntryDueDate = itemView.findViewById(R.id.tv_entry_due_date);
            tvEntryPriority = itemView.findViewById(R.id.tv_priority_of_entry);
            ivEditItem = itemView.findViewById(R.id.iv_edit_item);
        }
    }


    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setFilterEnum(String filterEnum) {
        for (SortingType sortingType:
             SortingType.values()) {
            if(sortingType.getDisplay_text().equalsIgnoreCase(filterEnum)){
                this.filterEnum = sortingType;
            }
        }
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    /***
     * updates the display-status (Working, Deleted, Done)
     *
     * @param status
     * @return current status
     */
    public Status switchView(Status status){
        this.displayStatus = status;
        filter();
        return status;
    }

    /***
     * Set status of forwarded entry to DONE
     *
     * @param position
     * @param entry
     */
    public void doEntry(int position, Entry entry)
    {
        NotificationHelper.cancelAlarm(entry.getRequest_id());
        entry.setStatus(Status.Done);
        entries.set(position, entry);
    }

    /***
     * Set status of forwarded entry to DELETED
     *
     * @param position
     * @param entry
     */
    public void deleteEntry(int position, Entry entry)
    {
        NotificationHelper.cancelAlarm(entry.getRequest_id());
        entry.setStatus(Status.Deleted);
        entries.set(position, entry);
    }

    /***
     * Set status of forwarded entry to WORKING
     *
     * @param position
     * @param entry
     */
    public void restoreEntry(int position, Entry entry)
    {
        entry.setStatus(Status.Working);
        entries.set(position, entry);
        updateReminders(entry);
        if(entry.getReminderDates().size() >= 1){
            NotificationHelper.startAlarm(entry);
        }
    }

    private void updateReminders(Entry entry){
        List<LocalDateTime> datesToRemove = new LinkedList<>();
        for (LocalDateTime reminder:
             entry.getReminderDates()) {
            if(reminder.isBefore(LocalDateTime.now())){
                datesToRemove.add(reminder);
            }
        }

        for (LocalDateTime remove:
             datesToRemove) {
            entry.getReminderDates().remove(remove);
        }
    }


    /***
     * Remove the forwarded entry permanently
     *
     * @param position
     * @param entry
     */
    public void releaseEntry(int position, Entry entry)
    {
        entries.remove(position);
        for (String photoLocStr : entry.getAllPhotoLocations())
        {
            Log.d("PHOTO_STORAGE", "checking: "+photoLocStr);
            Uri uri = Uri.parse(photoLocStr);
            context.getContentResolver().delete(uri, null, null);
            Log.d("PHOTO_STORAGE", photoLocStr + " deleted");
        }
    }

    /***
     * Filter the entries according to a specific filter
     */
    public void filter(){
        filteredEntries.clear();
        List<Entry> helpList = new LinkedList<>();
        if(filter.equalsIgnoreCase("")){
            helpList = new LinkedList<>(entries);
        }else{
            for (Entry entry:
                 entries) {
                if(entry.getTitle().toUpperCase().contains(filter.toUpperCase())){
                    helpList.add(entry);
                }
            }
        }

        for (Entry entry:
                helpList) {
            if((filterCategory.equalsIgnoreCase(Proxy.getLanguageContext().getString(R.string.all_categories)) ||
                    filterCategory.equalsIgnoreCase(entry.getCategory().getCategory_name())) && entry.getStatus() == displayStatus){
                filteredEntries.add(entry);
            }
        }
        sortCategories();
        notifyDataSetChanged();
    }


    /***
     * Sort the filtered entries
     */
    private void sortCategories(){
        if(filterEnum != null){
            if(filterEnum == SortingType.PRIORITY_DOWNWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue));
            }else if(filterEnum == SortingType.DUE_DATE_DOWNWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate));
            }else if(filterEnum == SortingType.PRIORITY_UPWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue).reversed());
            }else{
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate).reversed());
            }
        }
    }

    /***
     * Load Entries from a .ser file
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadEntries() throws IOException, ClassNotFoundException{
        List<Entry> allEntries = new LinkedList<>();
        FileInputStream fis = context.openFileInput(FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        entries = (List<Entry>)ois.readObject();
        ois.close();
        Toast.makeText(context, "Entries Successfully loaded from " + FILE_NAME, Toast.LENGTH_LONG).show();
        filter();
    }

    /***
     * Save Entries to a .ser file
     *
     * @throws IOException
     */
    public void saveEntries()throws IOException{
        FileOutputStream fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(entries);
        oos.close();
        Toast.makeText(context, "Entries Successfully saved to " + FILE_NAME, Toast.LENGTH_LONG).show();
    }


}
