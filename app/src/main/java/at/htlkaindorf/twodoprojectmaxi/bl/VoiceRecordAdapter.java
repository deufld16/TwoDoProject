package at.htlkaindorf.twodoprojectmaxi.bl;

import android.content.Context;
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
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;
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

    List<String> test = Arrays.asList("Test");

    private FragmentManager fm;
    private Context context;

    public VoiceRecordAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
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

    @Override
    public void onBindViewHolder(@NonNull VoiceRecordAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return test.size();
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
