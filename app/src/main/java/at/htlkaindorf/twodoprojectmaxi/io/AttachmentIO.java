package at.htlkaindorf.twodoprojectmaxi.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/***
 * Class that handles IO actions for attachments for the transfer process
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */

public class AttachmentIO {

    private static final String KEY_AUDIO = "audio";
    private static final String KEY_PHOTO = "photo";
    private static Map<byte[], String> imageFilenameMapping;
    private static List<byte[]> bytes = new LinkedList<>();
    private static String storageDirStr;

    /***
     * Method to initialize the storage directory of the images, so that the
     * location of it could be transferred too
     */
    public static void initStorageDirectory()
    {
        File storageDir = Proxy.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDirStr = storageDir.toString()+File.separator;
    }

    /***
     * Method to convert the Attachments to a format (Map) that can be transferred via Bluetooth
     *  - key:      attachment type description
     *  - value:    collection of storage locations for each attachment item
     *
     * @return Map
     */
    //public static Map<String, List<File>> getAllAttachments(){
    public static List<byte[]> getAllAttachments(){
        imageFilenameMapping = new HashMap<>();

        for (Entry entry:Proxy.getToDoAdapter().getEntries()) {
            for(String imgPath : entry.getAllPhotoLocations())
            {
                imgPath = imgPath.replace("content://at.htlkaindorf.fileprovider/twodo_images/",
                        "");
                Log.d("IMG", storageDirStr+imgPath);

                try {
                    String imgFileStr = storageDirStr+imgPath;
                    File imgFile = new File(imgFileStr);
                    FileInputStream fis = new FileInputStream(imgFile);
                    byte[] byteArr = new byte[fis.available()];
                    fis.read(byteArr);
                    bytes.add(byteArr);
                    imageFilenameMapping.put(byteArr, imgFileStr);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    /***
     * Method to save a successfully transferred attachment collection
     *
     * @param
     * @param
     */
    public static void saveAttachments(String imgPath, byte[] sentArray){
        try {
            FileOutputStream fos = new FileOutputStream(imgPath);
            fos.write(sentArray);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Method to prepare the receiving device for the bluetooth transfer
     */
    public static void deleteAudiosForTransfer(){
        List<Entry> allAudioFileLocations = new LinkedList<>(Proxy.getToDoAdapter().getEntries());

        for (Entry entry:
             allAudioFileLocations) {
            for (String location:
                 entry.getAllAudioFileLocations()) {
                try{
                    Files.delete(Paths.get(location));
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public static Map<byte[], String> getImageFilenameMapping() {
        return imageFilenameMapping;
    }

    public static void setImageFilenameMapping(Map<byte[], String> imageFilenameMapping) {
        AttachmentIO.imageFilenameMapping = imageFilenameMapping;
    }

    public static String getStorageDirStr() {
        return storageDirStr;
    }

    public static void setStorageDirStr(String storageDirStr) {
        AttachmentIO.storageDirStr = storageDirStr;
    }
}
