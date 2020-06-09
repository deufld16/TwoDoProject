package at.htlkaindorf.twodoprojectmaxi.mediaRecorders;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.file.Path;
import java.util.UUID;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class SoundRecorder {
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaPlayer mediaPlayer = new MediaPlayer();

    public static final int REQUEST_PERMISSION_CODE = 1000;

    public void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    public boolean checkAudioPremissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(Proxy.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(Proxy.getContext(), Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void setupMediaRecorder(Entry entry){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        int audio_file_number = getUniqueAudioFileNumber(entry);
        String savePath = Proxy.getContext().getFilesDir().getAbsolutePath() + "/" + audio_file_number + "_audio_record_3gp";
        entry.getAllAudioFileLocations().add(savePath);
        Log.d("VOICE2", "setupMediaRecorder: " + savePath);
        mediaRecorder.setOutputFile(savePath);
    }

    public void recordAudio(Entry entry){
        setupMediaRecorder(entry);
        try{
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void stopRecordAudio(){
        mediaRecorder.stop();
    }

    public int getLengthOfAudio(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return Integer.parseInt(durationStr);
    }

    public void playRecording(String pathToRecording){
        mediaPlayer = new MediaPlayer();

        try{
            mediaPlayer.setDataSource(pathToRecording);
            mediaPlayer.prepare();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void stopRecording(Entry entry){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            setupMediaRecorder(entry);
        }
    }

    private int getUniqueAudioFileNumber(Entry currentEntry){
        int uniqueNumber = 0;
        boolean isNew = true;
        for (Entry entry:
                Proxy.getToDoAdapter().getEntries()) {
            if(entry == currentEntry){
                isNew = false;
                uniqueNumber += currentEntry.getAllAudioFileLocations().size();
            }else{
                uniqueNumber += entry.getAllAudioFileLocations().size();
            }
        }

        if(isNew){
            uniqueNumber += currentEntry.getAllAudioFileLocations().size();
        }

        return uniqueNumber;
    }
}
