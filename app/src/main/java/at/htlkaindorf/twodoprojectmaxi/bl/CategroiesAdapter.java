package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.dialogs.ChangeCategoryName;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DatePickerFragment;
import at.htlkaindorf.twodoprojectmaxi.dialogs.DeleteCategoryName;
import at.htlkaindorf.twodoprojectmaxi.enums.PriorityEnum;

public class CategroiesAdapter extends RecyclerView.Adapter<CategroiesAdapter.ViewHolder> {

    private List<Category> categories = new LinkedList<>();
    private Context context;
    private CategroiesAdapter catAdapter = this;
    private FragmentManager fm;

    public CategroiesAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    public List<Category> getEntries() {
        return categories;
    }

    /**
     * Sets the categoriesList of this class as required (removes default and Add Category because they should not be removeable/editable)
     * @param entries
     */
    public void setEntries(List<Category> entries) {
        this.categories = new LinkedList<>(entries);
        List<Category> catToRemove  = new LinkedList<>();
        for (Category cat:
             Proxy.getClm().getAllCategories()) {
            if(cat.getCategory_name().equalsIgnoreCase("ADD CATEGORY")){
                catToRemove.add(cat);
            }else if(cat.getCategory_name().equalsIgnoreCase(("default"))){
                catToRemove.add(cat);
            }
        }
        for (Category cat:
             catToRemove) {
            categories.remove(cat);
        }
        notifyDataSetChanged();
    }

    /**
     *  Creates/inflates the ViewHolder for the RecyclerView with the wanted layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CategroiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategroiesAdapter.ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_management,
                parent, false);
        viewHolder = new CategroiesAdapter.ViewHolder(view);

        return viewHolder;
    }

    /**
     * Prepares the RecyclerView with the required values and sets the OnClick-Events for the Delete/Edit button
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Category category = categories.get(position);
        holder.tvCatManageName.setText(category.getCategory_name());
        holder.ivDeleteCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment onEdit = new DeleteCategoryName(Proxy.getClm().getAllCategories().get(Proxy.getClm().getAllCategories().indexOf(category)),
                        catAdapter, Proxy.getClm(), context, categories);
                onEdit.show(fm, "deleteCategoryName");
            }
        });
        holder.ivEditCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment onEdit = new ChangeCategoryName(Proxy.getClm().getAllCategories().get(Proxy.getClm().getAllCategories().indexOf(category)),
                        catAdapter, Proxy.getClm(), context);
                onEdit.show(fm, "changeCategoryName");
            }
        });
    }

    /**
     * returns the size of the categoryList
     * @return
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * This class contains all the Components that are required to display the RecyclerView as wanted
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout clManageCatagories;
        private TextView tvCatManageName;
        private ImageView ivEditCat;
        private ImageView ivDeleteCat;
        private ImageView ivCatMngmtBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clManageCatagories = itemView.findViewById(R.id.clManageCatagories);
            tvCatManageName = itemView.findViewById(R.id.tvCatManageName);
            ivEditCat = itemView.findViewById(R.id.ivEditCat);
            ivDeleteCat = itemView.findViewById(R.id.ivDeleteCat);
            ivCatMngmtBg = itemView.findViewById(R.id.ivCatMngmtBg);
        }
    }


}
