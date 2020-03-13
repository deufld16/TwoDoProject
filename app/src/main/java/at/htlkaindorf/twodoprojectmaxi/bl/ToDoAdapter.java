package at.htlkaindorf.twodoprojectmaxi.bl;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;

public class ToDoAdapter extends RecyclerView.Adapter {

    private List<Entry> entries;

    public ToDoAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
