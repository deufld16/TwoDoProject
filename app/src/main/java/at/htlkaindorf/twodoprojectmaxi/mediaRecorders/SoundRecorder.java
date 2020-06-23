package at.htlkaindorf.twodoprojectmaxi.mediaRecorders;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class SoundRecorder {
    private MediaRecorder mediaRecorder = new MediaRecorder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPaused = false;

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
        String savePath = Proxy.getContext().getFilesDir().getAbsolutePath() + "/" + audio_file_number + "_audio_record.3gp";
        entry.getAllAudioFileLocations().add(savePath);
        Log.d("FIXINGVR", "setupMediaRecorder: " + savePath);
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
        try{
            Thread.sleep(300);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        mediaRecorder.stop();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getLengthOfAudio(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(path);
            Log.d("FIXINGVR", "getLengthOfAudio: " + path);
            mmr.setDataSource(fis.getFD());
        }catch(Exception ex){
            ex.printStackTrace();
        }

        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int lengthInSeconds = Integer.parseInt(durationStr);

        return  lengthInSeconds;
    }

    public String getLengthOfAudioToString(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int lengthInSeconds = Integer.parseInt(durationStr) % 1000 >= 500 ? Integer.parseInt(durationStr)/1000 + 1 : Integer.parseInt(durationStr) /1000;

        String displayString = "";
        int minutes = lengthInSeconds / 60;
        if(minutes > 99){
            return "99:59";
        }

        lengthInSeconds = lengthInSeconds % 60;

        return String.format("%02d:%02d", minutes, lengthInSeconds);
    }

    public void playRecording(String pathToRecording){
        if(!isPaused){
            mediaPlayer = new MediaPlayer();
        }

        try{
            if(!isPaused){
                mediaPlayer.setDataSource(pathToRecording);
                mediaPlayer.prepare();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        isPaused = false;
        mediaPlayer.start();
    }

    public void pauseAudio(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            isPaused = true;
        }
    }


    public void stopPlayingAudio(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            //setupMediaRecorder(entry);
            isPaused = false;
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

    public MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
