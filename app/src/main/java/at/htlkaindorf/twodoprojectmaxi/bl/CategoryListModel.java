package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;

/**
 * Model-class for the category list
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class CategoryListModel {

    private static final String FILE_NAME = "categories.ser";

    public CategoryListModel(){
        addCategory(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_default)));
        addCategory(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_add_category)));
    }

    private List<Category> allCategories = new LinkedList<>();

    /**
     * Checks if entry already exists, if not it is added to the categoryList
     * @param cat
     */
    public void addCategory(Category cat){
        if(!allCategories.contains(cat)){
            allCategories.add(cat);
        }
    }

    public Category getCategory(int position)
    {
        return allCategories.get(position);
    }

    public void remCategory(Category cat){
        Log.d("ERROR", "remCategory: " + cat);
        allCategories.remove(cat);
    }

    public List<Category> getAllCategories() {
        return allCategories;

    }
    
    public void setAllCategories(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    /**
     * changes the category name
     *      -returns true if successfull
     *      -return false if unsuccessfull
     * @param position
     * @param catName
     * @return
     */
    public boolean setCategoryName(int position, String catName)
    {
        for (Category cat : allCategories)
        {
            if(cat.getCategory_name().equals(catName))
            {
                return false;
            }
        }
        getCategory(position).setCategory_name(catName);
        return true;
    }

    /**
     * Method for loading the Categories from the categories.ser file
     * the "default" and "ADD Category" category is also added
     * @param context
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadCategories(Context context)throws IOException, ClassNotFoundException{
        FileInputStream fis = context.openFileInput(FILE_NAME);
        ObjectInputStream ois = new ObjectInputStream(fis);
        //Log.d("ERROR", "load Categories: " + (List<Category>)ois.readObject());;
        allCategories = new LinkedList<>((List<Category>)ois.readObject());
        addCategory(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_default)));
        addCategory(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_add_category)));
        ois.close();
        //Toast.makeText(context, "Categories Successfully loaded from " + FILE_NAME, Toast.LENGTH_LONG).show();
    }

    /**
     * Method for saving the categories to the categoies.ser file
     * @param context
     * @param isAddCatIncluded
     * @throws IOException
     */
    public void saveCategories(Context context, boolean isAddCatIncluded)throws IOException{
        FileOutputStream fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        allCategories.remove(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_add_category)));
        allCategories.remove(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_default)));
        oos.writeObject(allCategories);
        allCategories.add(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_default)));
        allCategories.add(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_add_category)));
        Log.d("ERROR", "saveCategories: " + allCategories);;
        oos.close();
        //Toast.makeText(context, "Categories Successfully saved to " + FILE_NAME, Toast.LENGTH_LONG).show();
    }

    public void languageChanged(Category addCategory, Category defaultCategory){
        allCategories.remove(addCategory);
        allCategories.remove(defaultCategory);

        allCategories.add(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_default)));
        allCategories.add(new Category(Proxy.getLanguageContext().getString(R.string.add_entry_page_add_category)));
    }
}
