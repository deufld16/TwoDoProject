package at.htlkaindorf.twodoprojectmaxi.bl;

import android.net.Uri;
import android.util.Log;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;

/**
 * Model-class for the entry list
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class EntryListModel {

    private List<Entry> allEntries = new LinkedList<>();
    private List<Entry> filteredEntries = new LinkedList<>();

    private Category filterCategory;
    private String filter;

    private boolean descPriority;
    private boolean descDate;
    private boolean priorityOrDate;

    /**
     * Adds an entry and filters all the entries afterwards
     * @param entry
     */
    public void addEntry(Entry entry){
        allEntries.add(entry);
        filterAndSort();
    }

    /**
     * Removese an entry and filters all the remaining entries afterwards
     * @param entry
     */
    public void remEntry(Entry entry){
        allEntries.remove(entry);
        filterAndSort();
    }

    /**
     * Method that filters and sorts all the entries according to the filter and sorting criteria
     */
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

    /**
     * Method that is used to set all the filter Parameters
     * @param filterCategory
     * @param filter
     * @param descPriority
     * @param descDate
     * @param priorityOrDate
     */
    public void setFilterParameters(Category filterCategory, String filter, boolean descPriority, boolean descDate, boolean priorityOrDate){
        this.filterCategory = filterCategory;
        this.filter = filter;
        this.descPriority = descPriority;
        this.descDate = descDate;
        this.priorityOrDate = priorityOrDate;
    }
}
