package at.htlkaindorf.twodoprojectmaxi.mediaRecorders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/***
 * Class to take photographs with the camera
 *
 * @author Maximilian Strohmaier
 */

public class ImageRecorder
{
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    /***
     * Method to take a picture with the camera
     * @param srcActivity
     * @return Uri
     */
    public static Uri takePhoto(Activity srcActivity)
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(photoIntent.resolveActivity(srcActivity.getPackageManager()) != null)
        {
            File file = null;
            try {
                file = createPhotoFile(srcActivity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(file != null) {
                Uri uri = FileProvider.getUriForFile(srcActivity,
                        "at.htlkaindorf.fileprovider",
                        file);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                srcActivity.startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
                return uri;
            }
        }
        return null;
    }

    /***
     * Method to create the place of storage for the photograph
     * @return File
     */
    private static File createPhotoFile(Context context) throws IOException {
        /*File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "T(W)O_DO");
        if(!storageDir.exists())
        {
            if(!storageDir.mkdirs())
            {
                Log.d("PHOTO_STORAGE", "failed to create directory");
                return null;
            }
            Log.d("PHOTO_STORAGE", "directory created: "+storageDir.toString());
        }*/

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String fileName = createFileNameNow();
        String suffix = ".jpg";
        //Log.d("FIXINGVR", "Photo File Name: "+storageDir);
        return File.createTempFile(fileName, suffix, storageDir);
    }

    /***
     * Method to assemble a path for a photograph
     *
     * @return String
     */
    public static String assemblePhotoPath()
    {
        return createFileNameNow()+".jpg";
    }

    /***
     * Method to create a name for a file using the current date & time
     * @return
     */
    private static String createFileNameNow()
    {
        return "twodo_img_"
                + DateTimeFormatter
                .ofPattern("yyyy-MM-dd_HH-mm-ss-SS_")
                .format(LocalDateTime.now());
    }

    public static Bitmap createScaledBitmap(Context context, Uri uri, double targetWidth) throws IOException {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT < 28) {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } else {
            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
            bitmap = ImageDecoder.decodeBitmap(source);
        }
        Log.d("PHOTO_STORAGE", bitmap.getWidth() + " - " + bitmap.getHeight());

        double bmWidth = bitmap.getWidth();
        double bmHeight = bitmap.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int)targetWidth,
                (int)(bmHeight / bmWidth * targetWidth),
                false);
        return bitmap;
    }
}
