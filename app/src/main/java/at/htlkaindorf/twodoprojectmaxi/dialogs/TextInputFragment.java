package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;

/***
 * DialogFragment for text-based user input
 *
 * @author Maximilian Strohmaier
 */

public class TextInputFragment extends DialogFragment
{
    private String title;
    private String infoText;
    private CategoryListModel clm;
    private int position;
    private Spinner spCategories;
    private ArrayAdapter<Category> categoryAdapter;
    private Context context;

    public TextInputFragment(String title, String infoText, CategoryListModel clm, int position,
                             Spinner spCategories, Context context) {
        this.title = title;
        this.infoText = infoText;
        this.clm = clm;
        this.position = position;
        this.spCategories = spCategories;
        this.categoryAdapter = (ArrayAdapter<Category>) spCategories.getAdapter();
        this.context = context;
    }

    /**
     * Creates/Inflates the fragment which is used to add and save a new Category
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
                spCategories.setSelection(0);
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
                    catName = "New Category";
                }
                boolean changeSuccessful = clm.setCategoryName(position, catName);
                if(changeSuccessful) {
                    clm.addCategory(new Category("ADD CATEGORY"));
                }
                else
                {
                    spCategories.setSelection(position-1);
                }
                categoryAdapter.notifyDataSetChanged();
                try {
                    clm.saveCategories(context, true);
                }catch(IOException ex){
                    Log.d("ERROR", "An error has occured");
                }
                dismiss();
            }
        });

        return builder.create();
    }
}
