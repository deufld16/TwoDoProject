package at.htlkaindorf.twodoprojectmaxi.bl;

public class Operations {
    public static ToDoAdapter toDoAdapter;

    public static ToDoAdapter getToDoAdapter() {
        return toDoAdapter;
    }

    public static void setToDoAdapter(ToDoAdapter toDoAdapter) {
        Operations.toDoAdapter = toDoAdapter;
    }
}
