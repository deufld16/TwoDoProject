package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Arrays;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;

/**
 * This activity is used to allow the user to transfer his/her own To Do List
 * to another device via Bluetooth. Furthermore, it informs the user about
 * all steps during this process
 *
 * @author Maximilan Strohmaier
 */

public class TransferActivity extends AppCompatActivity {

    private Spinner spRole;
    private TextView tvInfoArea;
    private LottieAnimationView lavBluetooth;
    private ImageView ivCancel;
    private TextView tvCancel;

    private List<String> roles = Arrays.asList("sender", "receiver");
    private ArrayAdapter<String> roleAdapter;

    /***
     * Method to inflate the GUI and initialize vital variables
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        spRole = findViewById(R.id.spTransferRole);
        tvInfoArea = findViewById(R.id.tvTransferInfoArea);
        lavBluetooth = findViewById(R.id.lav_bluetooth);
        ivCancel = findViewById(R.id.ivTransferCancel);
        tvCancel = findViewById(R.id.tvTransferCancel);

        tvInfoArea.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvInfoArea.setVerticalScrollBarEnabled(true);

        initRoleSpinner();

        lavBluetooth.setOnClickListener(view ->
                lavBluetooth.animate()
                            .alpha(0.0f)
                            .setDuration(1000)
                            .withEndAction(() -> {
                                lavBluetooth.setVisibility(View.INVISIBLE);
                                informUser("Transfer process has started");
                            })
                            .start());

        ivCancel.setOnClickListener(view -> onCancel(view));
        tvCancel.setOnClickListener(view -> onCancel(view));
    }

    /***
     * Method to terminate the current transfer process and return to previous activity
     *
     * @param view
     */
    private void onCancel(View view)
    {
        finish();
        overridePendingTransition(0, R.anim.from_right);
    }


    /***
     * Method to initialize the role spinner with the predefined roles
     */
    private void initRoleSpinner()
    {
        roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
    }

    /***
     * Method to inform the user about the progress of the transfer using a specific message
     *
     * @param msg
     */
    public void informUser(String msg)
    {
        tvInfoArea.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        tvInfoArea.append(msg+"\n");
    }

}
