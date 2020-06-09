package at.htlkaindorf.twodoprojectmaxi.bl;

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

    public VoiceRecordAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
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
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    Proxy.getSoundRecorder().playRecording(displayedAudios.get(position));
                    Log.d("VOICE2", "onClick: " + displayedAudios.get(position) + " - " + position);
                }else{
                    Proxy.getSoundRecorder().stopRecording(entry);
                }

            }
        });

        holder.tvEndTime.setText(Proxy.getSoundRecorder().getLengthOfAudio(displayedAudios.get(position)));
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
}
