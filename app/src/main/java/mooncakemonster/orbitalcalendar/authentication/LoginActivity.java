package mooncakemonster.orbitalcalendar.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.menudrawer.MenuDrawer;

/**
 * This class allows users to login into their account registered
 * and authenticate user by retrieving user details from Cloudant
 */
public class LoginActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button login;
    private TextView link_to_register;
    private EditText username;
    private EditText password;

    private ProgressDialog progressDialog;
    private LoginManager loginManager;
    private CloudantConnect cloudantConnect;
    private UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        link_to_register = (TextView) findViewById(R.id.registerhere);
        username = (EditText) findViewById(R.id.usernameL);
        password = (EditText) findViewById(R.id.passwordL);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        loginManager = new LoginManager(getApplicationContext());
        userDatabase = new UserDatabase(getApplicationContext());

        // Load Cloudant settings
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        // Check if user is already logged in
        if (loginManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MenuDrawer.class));
            finish();
        }

        // Pull all documents from Cloudant before user press login
        cloudantConnect.startPullReplication();

        // Login user
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = username.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if (inputUsername.length() > 0 && inputPassword.length() > 0) {
                    checkLogin(inputUsername, inputPassword);
                } else {
                    if (username.length() < 1) username.setError("Please enter username.");
                    else if (password.length() < 1) password.setError("Please enter password.");
                }
            }
        });

        // Links user to registration activity
        link_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });
    }

    // This method vertifies login details in Cloudant database.
    private void checkLogin(final String text_username, final String text_password) {
        // Check Cloudant database for existing users
        if (cloudantConnect.authenticateUser(text_username, text_password)) {
            progressDialog.setMessage("Logging in...");
            showDialog();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    // User already logged in, next time do not have to login again when using app
                    loginManager.setLogin(true);

                    // Store user in SQLite once successfully stored in Cloudant
                    User user = cloudantConnect.saveUserDetails(text_username, text_password);
                    userDatabase.addUser("" + R.drawable.profile, user.getEmail_address(), text_username);

                    // Take user to next activity
                    startActivity(new Intent(LoginActivity.this, MenuDrawer.class));
                    finish();

                    //Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                    hideDialog();
                }
            }, 3000);
        } else {
            Constant.alertUser(this, "Error logging in!", "Please check again.");
            // Resets username and password
            username.setText("");
            password.setText("");
        }
    }

    // This method reloads replication settings
    private void reloadReplicationSettings() {
        try {
            this.cloudantConnect.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Unable to construct remote URI from configuration", e);
        }
    }

    // This method shows progress dialog when not showing
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    // This method dismiss progress dialog when required (dialog must be showing)
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged()");
        reloadReplicationSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }
}