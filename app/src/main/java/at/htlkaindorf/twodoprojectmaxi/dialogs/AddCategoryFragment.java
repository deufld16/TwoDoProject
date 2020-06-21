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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.CategroiesAdapter;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/***
 * Fragment for adding a new category
 *
 * @author Maximilian Strohmaier
 */

public class AddCategoryFragment extends DialogFragment {

    private Context context;
    private CategroiesAdapter catAdapter;

    public AddCategoryFragment(Context context, CategroiesAdapter catAdapter) {
        this.context = context;
        this.catAdapter = catAdapter;
    }

    /**
     * Method that creates/inflates the Fragment which is used to add new Categories
     *      -it is checked if the typed in name is valid, if not a default name is selected
     *      -after that the categories are saved to the categories.ser file
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.dialog_text_input, null);
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
                String catName = etName.getText().toString();
                if(catName == null || catName.equals(""))
                {
                    catName = Proxy.getLanguageContext().getString(R.string.default_category_name);
                }
                Proxy.getClm().addCategory(new Category(catName));
                catAdapter.setEntries(Proxy.getClm().getAllCategories());
                catAdapter.notifyDataSetChanged();
                try {
                    Proxy.getClm().saveCategories(context, true);
                }catch(IOException ex){
                    Log.d("ERROR", "An error has occured");
                }
                dismiss();
            }
        });

        return builder.create();
    }
}
