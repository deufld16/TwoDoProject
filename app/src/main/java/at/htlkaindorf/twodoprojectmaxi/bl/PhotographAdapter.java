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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.CreationActivity;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.dialogs.PhotoDeletionDlg;

/***
 * Adapter class for the photograph-recycler-view
 *
 * @author Maximilian Strohmaier
 */

public class PhotographAdapter extends RecyclerView.Adapter<PhotographAdapter.ViewHolder>  {

    private List<Bitmap> bitmaps = new LinkedList<>();
    private List<Uri> imgUris = new LinkedList<>();
    private List<Uri> imgUrisDel = new LinkedList<>();
    private Context context;
    private PhotographAdapter thisPhotographAdapter = this;

    public PhotographAdapter(Context context) {
        this.context = context;
    }

    public void addPhoto(Uri imgUri, Bitmap bitmap)
    {
        bitmaps.add(bitmap);
        imgUris.add(imgUri);
        notifyDataSetChanged();
    }

    public void removePhoto(Uri imgUri, Bitmap bitmap)
    {
        bitmaps.remove(bitmap);
        imgUrisDel.add(imgUri);
        imgUris.remove(imgUri);
        notifyDataSetChanged();
        updatePhotoCount();
    }

    public List<Uri> getImageUris()
    {
        return imgUris;
    }

    public List<Uri> getImgUrisDel() {
        return imgUrisDel;
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
        Bitmap bitmap = bitmaps.get(position);
        holder.ivPhoto.setImageBitmap(bitmap);
        holder.ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogFragment deletionDlg = new PhotoDeletionDlg(thisPhotographAdapter, bitmap, imgUris.get(position));
                deletionDlg.show(((CreationActivity)context).getSupportFragmentManager(), "deletePhoto");
                return true;
            }
        });
        updatePhotoCount();
    }

    /***
     * Method to update the displayed number of attached photos
     */
    private void updatePhotoCount()
    {
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
