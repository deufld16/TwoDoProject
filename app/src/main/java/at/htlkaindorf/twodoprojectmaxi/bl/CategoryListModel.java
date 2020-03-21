package at.htlkaindorf.twodoprojectmaxi.bl;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Category;

public class CategoryListModel {

    public CategoryListModel(){
        allCategories.add(new Category("School", 1));
        allCategories.add(new Category("Friends", 2));
        allCategories.add(new Category("ToDo", 3));
    }

    private List<Category> allCategories = new LinkedList<>();

    public void addCategory(Category cat){
        if(!allCategories.contains(cat)){
            allCategories.add(cat);
        }
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
}
