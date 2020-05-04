package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;
import at.htlkaindorf.twodoprojectmaxi.enums.SortingType;
import at.htlkaindorf.twodoprojectmaxi.enums.Status;
import at.htlkaindorf.twodoprojectmaxi.io.Load;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private static final String FILE_NAME = "entries.ser";

    private List<Entry> entries = new LinkedList<>();
    private List<Entry> filteredEntries = new LinkedList<>();
    private Context context;
    private String filter = "";
    private Enum filterEnum = null;
    private String filterCategory = "";
    private Status displayStatus = Status.Working;

    public ToDoAdapter(Context context) {
        this.context = context;
    }

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

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_do_list,
                parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        /*holder.imageName.setText(mImageNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();
            }
        });*/
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
                if(entry.getStatus() == Status.Working){
                    entry.setStatus(Status.Done);
                }else if(entry.getStatus() == Status.Done){
                    entry.setStatus(Status.Deleted);
                }else{
                    entry.setStatus(Status.Working);
                }
                try {
                    saveEntries();
                }catch (Exception ex){
                    Log.d("Error", "An error occured while saving the file");
                }
                filter();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout clEntryLayout;
        private TextView tvEntryTitle;
        private TextView tvEntryCategory;
        private TextView tvEntryDueDate;
        private TextView tvEntryPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clEntryLayout = itemView.findViewById(R.id.cl_entry_layout);
            tvEntryTitle = itemView.findViewById(R.id.tv_entry_title);
            tvEntryCategory = itemView.findViewById(R.id.tv_entry_category);
            tvEntryDueDate = itemView.findViewById(R.id.tv_entry_due_date);
            tvEntryPriority = itemView.findViewById(R.id.tv_priority_of_entry);
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

    public void switchView(Status status){
        this.displayStatus = status;
        filter();
    }

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
            if((filterCategory.equalsIgnoreCase("All Categories") ||
                    filterCategory.equalsIgnoreCase(entry.getCategory().getCategory_name())) && entry.getStatus() == displayStatus){
                filteredEntries.add(entry);
            }
        }
        sortCategories();
        notifyDataSetChanged();
    }


    private void sortCategories(){
        if(filterEnum != null){
            if(filterEnum == SortingType.DUE_DATE_UPWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate).reversed());
            }else if(filterEnum == SortingType.DUE_DATE_DOWNWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate));
            }else if(filterEnum == SortingType.PRIORITY_UPWARDS){
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue).reversed());
            }else{
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue));
            }
        }
    }

    public void loadEntries() throws IOException, ClassNotFoundException{
        List<Entry> allEntries = new LinkedList<>();
        FileInputStream fis = context.openFileInput(FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        entries = (List<Entry>)ois.readObject();
        ois.close();
        Toast.makeText(context, "Entries Successfully loaded from " + FILE_NAME, Toast.LENGTH_LONG).show();
        filter();
    }

    public void saveEntries()throws IOException{
        FileOutputStream fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(entries);
        oos.close();
        Toast.makeText(context, "Entries Successfully saved to " + FILE_NAME, Toast.LENGTH_LONG).show();
    }


}
