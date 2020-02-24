package at.htlkaindorf.twodoprojectmaxi.bl;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;

public class EntryListModel {

    private List<Entry> allEntries = new LinkedList<>();
    private List<Entry> filteredEntries = new LinkedList<>();

    private Category filterCategory;
    private String filter;

    private boolean descPriority;
    private boolean descDate;
    private boolean priorityOrDate;

    public void addEntry(Entry entry){
        allEntries.add(entry);
        filterAndSort();
    }

    public void remEntry(Entry entry){
        allEntries.remove(entry);
        filterAndSort();
    }

    private void filterAndSort(){
        filteredEntries.clear();

        for (Entry entry:
             allEntries) {
            if(entry.getCategory() == filterCategory || filterCategory == null){
                if(entry.getTitle().equalsIgnoreCase(filter)){
                    filteredEntries.add(entry);
                }
            }
        }

        //wenn true sortiert es nach datum
        if(priorityOrDate){
            if(descDate){
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate).reversed());
            }else{
                filteredEntries.sort(Comparator.comparing(Entry::getDueDate));
            }
        }else{
            if(descPriority){
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue).thenComparing(Entry::getDueDate).reversed());
            }else{
                filteredEntries.sort(Comparator.comparing(Entry::getPriorityValue).thenComparing(Entry::getDueDate));
            }
        }
    }

    public List<Entry> getAllEntries() {
        return allEntries;
    }

    public List<Entry> getFilteredEntries() {
        return filteredEntries;
    }

    public void setFilterParameters(Category filterCategory, String filter, boolean descPriority, boolean descDate, boolean priorityOrDate){
        this.filterCategory = filterCategory;
        this.filter = filter;
        this.descPriority = descPriority;
        this.descDate = descDate;
        this.priorityOrDate = priorityOrDate;
    }
}
