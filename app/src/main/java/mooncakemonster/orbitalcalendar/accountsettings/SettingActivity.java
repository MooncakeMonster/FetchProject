package mooncakemonster.orbitalcalendar.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.LoginActivity;
import mooncakemonster.orbitalcalendar.authentication.LoginManager;
import mooncakemonster.orbitalcalendar.authentication.SQLiteHelper;

public class SettingActivity extends ActionBarActivity {

    private TextView user_email, user_name;
    private Button facebook, logout;

    private SQLiteHelper db;
    private LoginManager session;

    public SettingActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        getSupportActionBar().setElevation(0);

        user_email = (TextView) findViewById(R.id.useremail);
        user_name = (TextView) findViewById(R.id.updateuser);
        facebook = (Button) findViewById(R.id.login_button);
        logout = (Button) findViewById(R.id.logout);

        db = new SQLiteHelper(getApplicationContext());
        session = new LoginManager(getApplicationContext());

        // Fetch user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String email = user.get("email");
        String username = user.get("username");

        // TODO: Check why is it stored wrongly
        user_email.setText("Email: " + email);
        user_name.setText("Username: " + username);

        // Logout user when button pressed.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    // This method logout the user.
    private void logoutUser() {
        session.setLogin(false);

        // Remove user from sqlite in phone
        db.deleteUsers();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // Finish all previous activity and show login activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}