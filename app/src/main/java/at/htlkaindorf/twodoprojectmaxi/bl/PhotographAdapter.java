package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.CreationActivity;

public class PhotographAdapter extends RecyclerView.Adapter<PhotographAdapter.ViewHolder>  {

    List<Bitmap> bitmaps = new LinkedList<>();
    List<Uri> imgUris = new LinkedList<>();
    Context context;

    public PhotographAdapter(Context context) {
        this.context = context;
    }

    public void addPhoto(Uri imgUri, Bitmap bitmap)
    {
        bitmaps.add(bitmap);
        imgUris.add(imgUri);
        notifyDataSetChanged();
    }

    public List<Uri> getImageUris()
    {
        return imgUris;
    }

    @NonNull
    @Override
    public PhotographAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,
                                        parent, false);
        return new PhotographAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotographAdapter.ViewHolder holder, int position) {
        holder.ivPhoto.setImageBitmap(bitmaps.get(position));
        ((CreationActivity) context).tvPhotoCount.setText(String.format(context.getString(R.string.photo_count), getItemCount()));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
