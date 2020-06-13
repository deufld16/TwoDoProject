package at.htlkaindorf.twodoprojectmaxi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.bl.CategoryListModel;
import at.htlkaindorf.twodoprojectmaxi.bl.CategroiesAdapter;
import at.htlkaindorf.twodoprojectmaxi.bl.PhotographAdapter;

/***
 * Fragment to confirm the deletion of a photograph
 *
 * @author Maximilian Strohmaier
 */

public class PhotoDeletionDlg extends DialogFragment
{
    private PhotographAdapter pa;
    private Bitmap bitmap;
    private Uri uri;

    public PhotoDeletionDlg(PhotographAdapter pa, Bitmap bitmap, Uri uri) {
        this.pa = pa;
        this.bitmap = bitmap;
        this.uri = uri;
    }

    /**
     * Inflates the dialog
     *      -OnOk photo is deleted
     *      -onCancel photo deletion is canceled
     * @param savedInstanceState
     * @return dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dlgView = inflater.inflate(R.layout.dialog_delete_photo, null);
        builder.setView(dlgView);

        Button btnCancel = dlgView.findViewById(R.id.btDeletePhotoNo);
        Button btnOk = dlgView.findViewById(R.id.btDeletePhotoYes);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pa.removePhoto(uri, bitmap);
                dismiss();
            }
        });

        return builder.create();
    }
}
