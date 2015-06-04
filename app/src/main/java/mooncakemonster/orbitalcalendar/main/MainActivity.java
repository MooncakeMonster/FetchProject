package mooncakemonster.orbitalcalendar.main;

/**
 * This program is first executed when the app runs.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.login.widget.LoginButton;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.menu.MenuActivity;
import mooncakemonster.orbitalcalendar.registeruser.LoginActivity;
import mooncakemonster.orbitalcalendar.registeruser.UserLocalStore;


public class MainActivity extends ActionBarActivity {

    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

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
        Button bLogout = (Button) findViewById(R.id.logout);

        icon.startAnimation(animationFadeIn);
        slogan.startAnimation(animationFadeIn);
        loginButton.startAnimation(animationFadeIn);
        bLogout.startAnimation(animationFadeIn);
        tap.startAnimation(animationAlpha);

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    // This method sets "tap anywhere to continue" to next activity.
    private void setTapAnywhereToContinue() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.fragment);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticate() == true) {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    // This method checks if user is logged in or logged out.
    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
