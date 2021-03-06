package mooncakemonster.orbitalcalendar.zremove;

/**
 * This program is first executed when the app runs.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.LoginActivity;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAnimationFadeIn();
        setTapAnywhereToContinue();
    }

    // This method creates fade in effect when users open the app.
    private void setAnimationFadeIn() {
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.blink);
        ImageView icon = (ImageView) findViewById(R.id.icon),
                slogan = (ImageView) findViewById(R.id.slogan),
                tap = (ImageView) findViewById(R.id.tap);

        icon.startAnimation(animationFadeIn);
        slogan.startAnimation(animationFadeIn);
        tap.startAnimation(animationAlpha);
    }

    // This method sets "tap anywhere to continue" to next activity.
    private void setTapAnywhereToContinue() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
