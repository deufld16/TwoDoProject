package at.htlkaindorf.twodoprojectmaxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import at.htlkaindorf.twodoprojectmaxi.activities.ToDoListActivity;

/***
 * Entry point of app, which is used as a welcome screen
 *
 * @author Maximilian Strohmaier
 * @author Florian Deutschmann
 */

public class MainActivity extends AppCompatActivity {

    private Animation fromBottom;
    private Animation fromTop;

    private View vwCurves;
    private TextView tvInfo;
    private TextView tvTitle;
    private ImageView ivIcBg;
    private ImageView ivIcFg;

    /***
     * inflate the welcome screen GUI
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_intro);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top_intro);

        vwCurves = (View) findViewById(R.id.vwCurves);
        vwCurves.setAnimation(fromBottom);

        tvInfo = (TextView) findViewById(R.id.tvWelcomeInfo);
        tvInfo.setAnimation(fromBottom);

        tvTitle = findViewById(R.id.tvWelcomeTitle);
        tvTitle.setAnimation(fromTop);

        ivIcBg = findViewById(R.id.ivWelcomeIconBg);
        ivIcBg.setAnimation(fromTop);
        ivIcBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openToDoList();
            }
        });

        ivIcFg = findViewById(R.id.ivWelcomeIconFg);
        ivIcFg.setAnimation(fromTop);
        ivIcFg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openToDoList();
            }
        });

    }

    /***
     * Move on to To-Do-List
     */
    public void openToDoList()
    {
        Intent toDoListIntent = new Intent(this, ToDoListActivity.class);
        startActivity(toDoListIntent);
        overridePendingTransition(0, R.anim.from_left);
    }

}
