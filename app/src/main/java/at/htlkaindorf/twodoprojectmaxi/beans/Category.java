package at.htlkaindorf.twodoprojectmaxi.beans;

import java.io.Serializable;

public class Category implements Serializable {

    private String category_name;
    private int color_code;

    public Category(String category_name, int color_code){
        this.category_name = category_name;
        this.color_code = color_code;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getColor_code() {
        return color_code;
    }

    public void setColor_code(int color_code) {
        this.color_code = color_code;
    }
}
