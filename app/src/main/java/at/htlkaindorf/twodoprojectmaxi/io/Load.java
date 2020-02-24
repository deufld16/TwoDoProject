package at.htlkaindorf.twodoprojectmaxi.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Category;

public class Load {

    private static final Path resPath = Paths.get(System.getProperty("user.dir"));

    public List<Category> loadCategories() throws IOException, ClassNotFoundException {
        List<Category> allCategoies = new LinkedList<>();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resPath.toFile()));
        allCategoies = (List<Category>)ois.readObject();
        ois.close();

        return allCategoies;
    }

    public void saveCategories(List<Category> allCategories) throws IOException{

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resPath.toFile()));
        oos.writeObject(allCategories);
        oos.close();
    }
}
