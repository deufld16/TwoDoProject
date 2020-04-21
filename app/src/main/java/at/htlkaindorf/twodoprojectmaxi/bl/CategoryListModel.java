package at.htlkaindorf.twodoprojectmaxi.bl;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Category;

public class CategoryListModel {

    public CategoryListModel(){
        /*allCategories.add(new Category("School", 1));
        allCategories.add(new Category("Friends", 2));
        allCategories.add(new Category("To Do", 3));
        allCategories.add(new Category("Add category", 4));*/

        allCategories.add(new Category("School"));
        allCategories.add(new Category("Friends"));
        allCategories.add(new Category("To Do"));
        allCategories.add(new Category("ADD CATEGORY"));
    }

    private List<Category> allCategories = new LinkedList<>();

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
}
