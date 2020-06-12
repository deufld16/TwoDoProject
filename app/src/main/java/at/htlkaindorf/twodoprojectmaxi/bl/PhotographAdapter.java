package at.htlkaindorf.twodoprojectmaxi.bl;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;

public class PhotographAdapter extends RecyclerView.Adapter<PhotographAdapter.ViewHolder>  {

    List<Bitmap> thumbnails = new LinkedList<>();

    public void addThumbnail(Bitmap thumbnail)
    {
        thumbnails.add(thumbnail);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotographAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotographAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return thumbnails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
