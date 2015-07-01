package mooncakemonster.orbitalcalendar.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.menudrawer.MenuDrawer;

/**
 * This class allows users to login into their account registered
 * and authenticate user by retrieving user details from Cloudant
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button login;
    private TextView link_to_register;
    private EditText username;
    private EditText password;

    private ProgressDialog progressDialog;
    private LoginManager loginManager;
    private CloudantConnect cloudantConnect;

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

        // Load Cloudant settings
        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        // Check if user is already logged in
        if(loginManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MenuDrawer.class));
            finish();
        }

        // Login user
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = username.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if(inputUsername.length() > 0 && inputPassword.length() > 0) {
                    checkLogin(inputUsername, inputPassword);
                } else {
                    if(username.length() < 1) username.setError("Please enter username.");
                    else if(password.length() < 1) password.setError("Please enter password.");
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

    // TODO: This method vertifies login details in Cloudant database.
    private void checkLogin(final String username, final String password) {
        progressDialog.setMessage("Logging in...");
        showDialog();

        // TODO: Check Cloudant database for existing users
        if(cloudantConnect.authenticateUser(username, password)) {
            // User already logged in, next time do not have to login again when using app
            loginManager.setLogin(true);

            // Take user to next activity
            startActivity(new Intent(LoginActivity.this, MenuDrawer.class));
            finish();
        } else {
            alertUser("Error logging in!", "Please check again.");
            hideDialog();
        }
    }

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
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
    protected void onPause() {
        super.onPause();
        hideDialog();
    }
}
