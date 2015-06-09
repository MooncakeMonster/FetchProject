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
import mooncakemonster.orbitalcalendar.userdatabase.LoginUser;
import mooncakemonster.orbitalcalendar.userdatabase.SQLiteHelper;
import mooncakemonster.orbitalcalendar.userdatabase.SessionManager;

public class SettingActivity extends ActionBarActivity {

    private TextView user_email, user_name;
    private Button facebook, logout;

    private SQLiteHelper db;
    private SessionManager session;

    public SettingActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        user_email = (TextView) findViewById(R.id.useremail);
        user_name = (TextView) findViewById(R.id.updateuser);
        facebook = (Button) findViewById(R.id.login_button);
        logout = (Button) findViewById(R.id.logout);

        db = new SQLiteHelper(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        // Fetch user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String email = user.get("email");
        String username = user.get("name");

        // TODO: Check why is it stored wrongly
        user_email.setText("Email: " + username);
        user_name.setText("Username: " + email);

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

        startActivity(new Intent(getApplicationContext(), LoginUser.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}