package at.htlkaindorf.twodoprojectmaxi.mediaRecorders;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/***
 * Class to take photographs with the camera
 *
 * @author Maximilian Strohmaier
 */

public class ImageRecorder
{
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static void takePhoto(Activity srcActivity)
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(photoIntent.resolveActivity(srcActivity.getPackageManager()) != null)
        {
            srcActivity.startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
