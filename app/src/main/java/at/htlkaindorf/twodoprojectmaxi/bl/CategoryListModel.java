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

import at.htlkaindorf.twodoprojectmaxi.beans.Category;

public class CategoryListModel {

    private static final String FILE_NAME = "categories.ser";

    public CategoryListModel(){
        addCategory(new Category("default"));
        addCategory(new Category("ADD CATEGORY"));
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
        addCategory(new Category("default"));
        addCategory(new Category("ADD CATEGORY"));
        ois.close();
        Toast.makeText(context, "Categories Successfully loaded from " + FILE_NAME, Toast.LENGTH_LONG).show();
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
        oos.writeObject(allCategories);
        Log.d("ERROR", "saveCategories: " + allCategories);;
        oos.close();
        Toast.makeText(context, "Categories Successfully saved to " + FILE_NAME, Toast.LENGTH_LONG).show();
    }
}
