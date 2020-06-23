package at.htlkaindorf.twodoprojectmaxi.io;

import android.util.Log;

import java.io.File;
import java.io.IOException;
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

    /***
     * Method to convert the Attachments to a format (Map) that can be transferred via Bluetooth
     *  - key:      attachment type description
     *  - value:    collection of storage locations for each attachment item
     *
     * @return Map
     */
    public static Map<String, List<File>> getAllAttachments(){
        List<File> allAudioFiles = new LinkedList<>();
        List<File> allPhotoFiles = new LinkedList<>();

        for (Entry entry:Proxy.getToDoAdapter().getEntries()) {
            for (String path:
                 entry.getAllAudioFileLocations()) {
                try {
                    File file = new File(path);
                    allAudioFiles.add(file);
                    Log.d("FIXINGVR", "getAllAttachments: " + file.getAbsolutePath());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            for(String imgPath : entry.getAllPhotoLocations())
            {
                allPhotoFiles.add(new File(imgPath));
            }
        }

        Map<String, List<File>> attachments = new HashMap<>();
        attachments.put(KEY_AUDIO, allAudioFiles);
        attachments.put(KEY_PHOTO, allPhotoFiles);
        return attachments;
    }

    /***
     * Method to save a successfully transferred attachment collection
     *
     * @param attachment
     */
    public static void saveAttachments(Map<String, List<File>> attachment){
        List<File> allAudioFiles = attachment.get(KEY_AUDIO);
        List<File> allPhotoFiles = attachment.get(KEY_PHOTO);

        for (File file:
             allAudioFiles) {
            try{
                if(file.createNewFile()) {
                    Log.d("FIXINGVR", "getAllAttachments: " + file.getAbsolutePath());
                    Log.d("FIXINGVR", "audios successfully created");
                }
                else
                {
                    Log.d("FIXINGVR", "audios unsuccessfully created");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        for (File file : allPhotoFiles)
        {
            try {
                if(file.createNewFile())
                {
                    Log.d("TRANSFER_DATA", "photos successfully created");
                }
                else
                {
                    Log.d("TRANSFER_DATA", "photos unsuccessfully created");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

}
