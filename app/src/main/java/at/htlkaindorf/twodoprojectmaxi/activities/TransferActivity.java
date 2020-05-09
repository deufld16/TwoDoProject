package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Arrays;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.R;

public class TransferActivity extends AppCompatActivity {

    private Spinner spRole;
    private TextView tvInfoArea;
    private LottieAnimationView lavBluetooth;

    private List<String> roles = Arrays.asList("sender", "receiver");
    private ArrayAdapter<String> roleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        spRole = findViewById(R.id.spTransferRole);
        tvInfoArea = findViewById(R.id.tvTransferInfoArea);
        lavBluetooth = findViewById(R.id.lav_bluetooth);

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
    }

    private void initRoleSpinner()
    {
        roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
    }

    public void informUser(String msg)
    {
        tvInfoArea.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
        tvInfoArea.append(msg+"\n");
    }

}
