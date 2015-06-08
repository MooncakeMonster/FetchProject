package mooncakemonster.orbitalcalendar.main;

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

import com.facebook.login.widget.LoginButton;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.menu.MenuActivity;


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
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        icon.startAnimation(animationFadeIn);
        slogan.startAnimation(animationFadeIn);
        loginButton.startAnimation(animationFadeIn);
        tap.startAnimation(animationAlpha);
    }

    // This method sets "tap anywhere to continue" to next activity.
    private void setTapAnywhereToContinue() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.fragment);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: When login is successful, redirect to MenuActivity
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

}
