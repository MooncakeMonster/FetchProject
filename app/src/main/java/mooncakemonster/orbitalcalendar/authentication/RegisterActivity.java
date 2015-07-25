package mooncakemonster.orbitalcalendar.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.menudrawer.MenuDrawer;

/**
 * This class registers user and stores user details in Cloudant.
 */
public class RegisterActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Pattern LOWER_CASE = Pattern.compile("\\p{Lu}");
    private static final Pattern UPPER_CASE = Pattern.compile("\\p{Ll}");
    private static final Pattern DIGIT = Pattern.compile("\\p{Nd}");

    private static final String TAG = RegisterActivity.class.getSimpleName();

    Button register;
    TextView link_to_login;
    EditText email_address;
    EditText username;
    EditText password;
    EditText confirm_password;

    private ProgressDialog progressDialog;

    // Cloudant connection
    private CloudantConnect cloudantConnect;

    // Check if user is logged in
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.register);
        link_to_login = (TextView) findViewById(R.id.rlogin);
        email_address = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirmpassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Load Cloudant settings
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");
        this.cloudantConnect.setReplicationListener(this);

        // (1) Check if user is logged in; if yes, take user to main activity
        loginManager = new LoginManager(getApplicationContext());
        if (loginManager.isLoggedIn()) {
            startActivity(new Intent(RegisterActivity.this, MenuDrawer.class));
            finish();
        }

        // (2) Link to Login Screen
        link_to_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        // (3) if user is not logged in yet, allow for registration
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email_address.getText().toString().trim();
                String inputUsername = username.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();
                String inputConfirmPassword = confirm_password.getText().toString().trim();

                // Start retrieving all documents from Cloudant
                cloudantConnect.startPullReplication();

                // Prevent users from creating account with invalid email address
                if (!isValidEmailAddress(inputEmail)) {
                    alertUser("Invalid email address!", "Please try again.");
                    resetDetails(1);
                }

                // Prevent users from creating account with email address already in database
                else if(cloudantConnect.checkExistingItems("email_address", inputEmail)) {
                    alertUser("Email address exist!", "Please input other email address.");
                    resetDetails(1);
                }

                // Prevent users from creating account with username already in database
                else if(cloudantConnect.checkExistingItems("username", inputUsername)) {
                    alertUser("Username exist!", "Please input other username.");
                    resetDetails(2);
                }

                // Prevent users from creating account with username < 5 characters
                else if (inputUsername.length() < 5) {
                    alertUser("Invalid username!", "Username must contain at least 5 characters.");
                    resetDetails(2);
                }

                // Prevent users from creating account with less than 8 characters, no upper and lowercase letters and no digits.
                else if (!isValidPassword(inputPassword)) {
                    alertUser("Invalid password!", "Password must contain at least 8 characters, including:\n\n-Uppercase letters\n-Lowercase letters\n-At least 1 digit");
                    resetDetails(0);
                }

                // Prevent users from logging in if password != confirm password
                else if (!(inputPassword.equals(inputConfirmPassword))) {
                    alertUser("Passwords do not match!", "Please try again.");
                    resetDetails(0);
                }

                // Successfully registered
                else {
                    registerUser(inputEmail, inputUsername, inputPassword);
                }
            }
        });
    }

    /****************************************************************************************************
     * Helper methods to register user
     ****************************************************************************************************/

    // This method registers new users and store user data in Cloudant
    private void registerUser(final String email_address, final String username, final String password) {
        progressDialog.setMessage("Registering...");
        showDialog();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Save user details into Cloudant; if successful, take user to login activity
                if (createNewUser(email_address, username, password)) {
                    // Push new user details into Cloudant
                    cloudantConnect.startPushReplication();

                    // Launch Login Activity
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
                //Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }, 1500);
    }

    /****************************************************************************************************
     * Helper methods for Cloudant database
     ****************************************************************************************************/

    // This method creates new user document into Cloudant
    private boolean createNewUser(String email_address, String username, String password) {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.bearicon);
        User user = new User("", email_address, username, password);
        try {
            User final_user = cloudantConnect.createNewUserDocument(user);
            Log.d(TAG, "Saved new user " + final_user.getEmail_address() + " successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Unable to create new user document");
        }

        return false;
    }

    // This method reloads replication settings
    private void reloadReplicationSettings() {
        try {
            this.cloudantConnect.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Unable to construct remote URI from configuration", e);
        }
    }

    // This method stops all replication
    public void stopReplication() {
        cloudantConnect.stopAllReplication();
    }

    // This method completes replication
    public void replicationComplete() {
        progressDialog.dismiss();
    }

    // This method is called if there is an error in replication
    public void replicationError() {
        progressDialog.dismiss();
    }

    /****************************************************************************************************
     * Helper methods to check validity of user details
     ****************************************************************************************************/

    // This method resets the details keyed in by user.
    private void resetDetails(int num) {
        if (num == 1) email_address.setText("");
        if (num == 1 || num == 2) username.setText("");
        password.setText("");
        confirm_password.setText("");
    }

    // This method ensures that email keyed in by user is valid (but can't ensure it exist)
    public boolean isValidEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // This method ensures that password contains at least 1 digit with both uppercase and lowercase letters.
    public boolean isValidPassword(final String password) {
        return hasEightChar(password) && hasDigit(password) && hasUpperCase(password) && hasLowerCase(password);
    }

    // This method ensures that password contains at least 8 characters and no more than 20 characters.
    private boolean hasEightChar(final String password) {
        return (password.length() >= 8 && password.length() <= 20);
    }

    // This method ensures that password contains at least 1 digit.
    private boolean hasDigit(final String password) {
        return DIGIT.matcher(password).find();
    }

    // This method ensures that password contains at least 1 uppercase letter.
    private boolean hasUpperCase(final String password) {
        return UPPER_CASE.matcher(password).find();
    }

    // This method ensures that password contains at least 1 lowercase letter.
    private boolean hasLowerCase(final String password) {
        return LOWER_CASE.matcher(password).find();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged()");
        reloadReplicationSettings();
    }

    /****************************************************************************************************
     * Other helper methods
     ****************************************************************************************************/

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

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }
}