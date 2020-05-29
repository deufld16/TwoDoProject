package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.CategroiesAdapter;

/***
 * Fragment for changing the name of a specific category
 *
 * @author Florian Deutschmann
 */

public class ChangeCategoryName extends DialogFragment {

    private Category category;
    private CategroiesAdapter catAdapter;
    private CategoryListModel clm;
    private Context context;

    public ChangeCategoryName(Category category, CategroiesAdapter catAdapter, CategoryListModel clm, Context context){
        this.category = category;
        this.catAdapter = catAdapter;
        this.clm = clm;
        this.context = context;
    }

    /**
     * Method that creates/inflates the Fragment which is used to change the Category Name
     *      -it is checked if the typed in name is valid, if not the whole process is terminated/the name change is reverted
     *      -after that the categories are saved to the categories.ser file
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.dialog_change_category_name, null);
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
                EditText etName = dlgView.findViewById(R.id.etNewCatName);
                String newCatName = etName.getText().toString();

                if(newCatName == null || newCatName.trim().isEmpty()){
                    dismiss();
                }

                category.setCategory_name(newCatName);
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
