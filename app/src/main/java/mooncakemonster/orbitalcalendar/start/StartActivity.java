package mooncakemonster.orbitalcalendar.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.LoginActivity;

/**
 * StartActivity displays company logo in fullscreen mode when app startup.
 */
public class StartActivity extends Activity {

    // Start activity after 1.5 seconds
    private static final int TIME_MILLISECONDS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        startActivity();
    }

    private void startActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, TIME_MILLISECONDS);
    }
}