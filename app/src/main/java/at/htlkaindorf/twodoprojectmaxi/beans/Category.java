package at.htlkaindorf.twodoprojectmaxi.beans;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Category implements Serializable {

    private String category_name;

    public Category(String category_name, int color_code){
        this.category_name = category_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @NonNull
    @Override
    public String toString() {
        return category_name;
    }
}
