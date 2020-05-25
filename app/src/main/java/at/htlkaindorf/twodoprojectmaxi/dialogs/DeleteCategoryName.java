package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.CategroiesAdapter;

/***
 * Fragment to confirm the deletion of a sepcific category item
 *
 * @author Florian Deutschmann
 */

public class DeleteCategoryName extends DialogFragment {

    private Category category;
    private CategroiesAdapter catAdapter;
    private CategoryListModel clm;
    private Context context;
    private List<Category> help;

    public DeleteCategoryName(Category category, CategroiesAdapter catAdapter, CategoryListModel clm, Context context, List<Category> help){
        this.category = category;
        this.catAdapter = catAdapter;
        this.clm = clm;
        this.context = context;
        this.help = help;
    }

    /**
     * Inflates/Creates the dialog which is used to confirm the deletion of a category (safety check)
     *      -OnOk category is deleted
     *      -onCancel category deletion is canceled
     * @param savedInstanceState
     * @return dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.delete_category_dialog, null);
        builder.setView(dlgView);

        Button btnCancel = dlgView.findViewById(R.id.newCatDlgBtnCancel);
        Button btnOk = dlgView.findViewById(R.id.newCatDlgBtnOk);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clm.remCategory(category);
                help.remove(category);
                catAdapter.notifyDataSetChanged();
                try{
                    clm.saveCategories(context, false);
                }catch (Exception ex){
                    Log.e("ERROR", "Error while saving the categories");
                }
                dismiss();
            }
        });

        return builder.create();
    }
}
