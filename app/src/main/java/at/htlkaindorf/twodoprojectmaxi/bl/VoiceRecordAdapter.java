package at.htlkaindorf.twodoprojectmaxi.bl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.SoundRecorder;

/***
 * Adapter-class for the voice-recordings recylcer-view
 *
 * @author Maximilian Strohmaier
 */

public class VoiceRecordAdapter extends RecyclerView.Adapter<VoiceRecordAdapter.ViewHolder> {

    /* ToDo:
     *  - add List with Type of Voice Recording
     *  - adjust getItemCount()
     *  - onBindViewHolder() to assign the particular values to the components
     */

    List<String> displayedAudios = new LinkedList<>();

    private FragmentManager fm;
    private Context context;
    private Entry entry;
    private boolean isPlaying = false;
    private Activity sourceActivity;
    private UpdateCurrentTimeOfRecording updateThread;
    private int positionOfPlayingEntry = -1;
    private ProgressBar pbOfOldEntry = null;
    private TextView tvOld = null;
    private ImageView ivOld = null;

    public VoiceRecordAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    public Activity getSourceActivity() {
        return sourceActivity;
    }

    public void setSourceActivity(Activity sourceActivity) {
        this.sourceActivity = sourceActivity;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
        renew();
    }

    @NonNull
    @Override
    public VoiceRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VoiceRecordAdapter.ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice_recording,
                parent, false);
        viewHolder = new VoiceRecordAdapter.ViewHolder(view);

        return viewHolder;
    }

    public void renew(){
        displayedAudios.clear();
        for (String audio:
             entry.getAllAudioFileLocations()) {
            displayedAudios.add(audio);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceRecordAdapter.ViewHolder holder, int position) {
        for (File file:
                Proxy.getContext().getFilesDir().listFiles()) {
            try {
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                fis.getFD();
                Log.d("FIXINGVR", "onBindViewHolder: no error: " + file.getAbsolutePath());
            } catch (Exception e) {
                Log.d("FIXINGVR", "onBindViewHolder: " + file.getAbsolutePath());
            }
        }
        holder.pbProgress.setMax(Proxy.getSoundRecorder().getLengthOfAudio(displayedAudios.get(position)));
        holder.pbProgress.setMin(0);
        holder.pbProgress.setProgress(0);
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    startPlayingAudio(holder, position);
                    holder.ivPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                }else{
                    if(updateThread.isAlive()){
                        updateThread.interrupt();
                    }
                    holder.ivPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    if(positionOfPlayingEntry == position){
                        if(!Proxy.getSoundRecorder().getMediaPlayer().isPlaying()){
                            holder.tvCurrentTime.setText(holder.tvEndTime.getText());
                            holder.pbProgress.setProgress(holder.pbProgress.getMax());
                        }
                        Proxy.getSoundRecorder().pauseAudio();
                        isPlaying = false;
                    }else{
                        while(updateThread.isAlive()){
                            //idle
                        }
                        startPlayingAudio(holder, position);
                    }
                }

            }
        });
        holder.tvEndTime.setText(Proxy.getSoundRecorder().getLengthOfAudioToString(displayedAudios.get(position)));
        holder.tvCurrentTime.setText("00:00");
    }

    private void startPlayingAudio(VoiceRecordAdapter.ViewHolder holder, int position){
        Log.d("VOICE2", "onClick: starting Audio Play");
        if(position == positionOfPlayingEntry){
            Log.d("VOICE2", "startPlayingAudio: same entry");
            if(holder.pbProgress.getMax() == holder.pbProgress.getProgress()){
                holder.pbProgress.setProgress(0);
                holder.tvCurrentTime.setText("00:00");
            }
        }else{
            Log.d("VOICE2", "startPlayingAudio: diffrent  entry");
            if(pbOfOldEntry != null && tvOld != null){
                pbOfOldEntry.setProgress(0);
                tvOld.setText("00:00");
                ivOld.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        }

        if(Proxy.getSoundRecorder().isPaused()){
            if(position == positionOfPlayingEntry){
                Proxy.getSoundRecorder().playRecording(displayedAudios.get(position));
            }else{
                Proxy.getSoundRecorder().stopPlayingAudio();
                Proxy.getSoundRecorder().playRecording(displayedAudios.get(position));
            }
        }else{
            if(isPlaying){
                Proxy.getSoundRecorder().stopPlayingAudio();
            }
            Proxy.getSoundRecorder().playRecording(displayedAudios.get(position));
        }

        updateThread = new UpdateCurrentTimeOfRecording(holder.tvCurrentTime,  holder.tvEndTime, holder.pbProgress, holder.ivPlay);
        updateThread.start();
        isPlaying = true;
        positionOfPlayingEntry = position;
        pbOfOldEntry = holder.pbProgress;
        tvOld = holder.tvCurrentTime;
        ivOld = holder.ivPlay;
    }

    @Override
    public int getItemCount() {
        return displayedAudios.size();
    }

    /**
     * This class contains all the components that are required to display the RecyclerView as wanted
     *
     * @author Maximilian Strohmaier
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivPlay;
        private TextView tvCurrentTime;
        private ProgressBar pbProgress;
        private TextView tvEndTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            tvCurrentTime = itemView.findViewById(R.id.tvCurrentTime);
            pbProgress = itemView.findViewById(R.id.pbRecordingProgress);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
        }
    }

    /**
     * Class/Thread that is resposible for updating the progressbar according to the played seconds of the audio
     *
     *  @author Florian Deutschmann
     */
    private class UpdateCurrentTimeOfRecording extends Thread{

        private TextView tvCurrentTime;
        private TextView tvEndTime;
        private ImageView ivPlayStopButton;
        private ProgressBar pbProgress;
        private LocalDateTime helpDate;
        private LocalDateTime progressBarDate;
        private int currentSeconds = 0;
        private int minutes = 0;

        public UpdateCurrentTimeOfRecording(TextView tvCurrentTime, TextView tvEndTime, ProgressBar pbProgress, ImageView ivPlayStopButton){
            this.tvCurrentTime = tvCurrentTime;
            this.tvEndTime = tvEndTime;
            this.pbProgress = pbProgress;
            this.ivPlayStopButton = ivPlayStopButton;

            if(!tvCurrentTime.getText().equals("00:00")){
                minutes = Integer.parseInt(tvCurrentTime.getText().toString().split(":")[0]);
                currentSeconds = Integer.parseInt(tvCurrentTime.getText().toString().split(":")[1]);
            }
        }



        @Override
        public void run() {
            helpDate = LocalDateTime.now();
            progressBarDate = LocalDateTime.now();

            while(!Thread.interrupted()){

                if(!Proxy.getSoundRecorder().getMediaPlayer().isPlaying()){
                    sourceActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbProgress.setProgress(pbProgress.getMax());
                            tvCurrentTime.setText(tvEndTime.getText());
                            isPlaying = false;
                            ivPlayStopButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                            interrupt();
                        }
                    });
                }else{
                    if(progressBarDate.plusNanos(200000000).isEqual(LocalDateTime.now())){
                        progressBarDate = LocalDateTime.now();
                        sourceActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(Proxy.getSoundRecorder().getMediaPlayer().isPlaying()){
                                    pbProgress.setProgress(pbProgress.getProgress() + 200);
                                }else{
                                    pbProgress.setProgress(pbProgress.getMax());
                                }
                            }
                        });
                    }else if(progressBarDate.plusNanos(200000000).isBefore(LocalDateTime.now())){
                        progressBarDate = LocalDateTime.now();
                        sourceActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!tvEndTime.getText().equals(tvCurrentTime.getText())){
                                    pbProgress.setProgress(pbProgress.getProgress() + 200);
                                }else{
                                    pbProgress.setProgress(pbProgress.getMax());
                                }
                            }
                        });
                    }

                    if(helpDate.plusSeconds(1).isEqual(LocalDateTime.now())){
                        helpDate = LocalDateTime.now();
                        currentSeconds ++;
                        if(currentSeconds == 60){
                            currentSeconds = 0;
                            minutes ++;
                        }
                        sourceActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!tvEndTime.getText().equals(tvCurrentTime.getText())){
                                    tvCurrentTime.setText(String.format("%02d:%02d", minutes, currentSeconds));
                                }
                            }
                        });
                        if(tvEndTime.getText().equals(tvCurrentTime.getText())){
                            this.interrupt();
                            isPlaying = false;
                        }
                    }
                }
            }
        }

    }

    public List<String> getDisplayedAudios() {
        return displayedAudios;
    }
}
