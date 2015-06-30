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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.menudrawer.MenuDrawer;

/**
 * This class registers user and stores user details in Cloudant.
 */
public class RegisterActivity extends Activity {

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
    private SQLiteHelper sqLiteHelper;

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
        sqLiteHelper = new SQLiteHelper(getApplicationContext());

        // (1) Check if user is logged in; if yes, take user to main activity
        loginManager = new LoginManager(getApplicationContext());
        if(loginManager.isLoggedIn()) {
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

        // (3) TODO If user is not logged in yet, allow for registration
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email_address.getText().toString().trim();
                String inputUsername = username.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();
                String inputConfirmPassword = confirm_password.getText().toString().trim();

                // TODO: Prevent users from creating account with email address already in database

                // Prevent users from creating account with invalid email address
                if(!isValidEmailAddress(inputEmail)) {
                    alertUser("Invalid email address!", "Please try again.");
                    resetDetails(1);
                }

                // TODO: Prevent users from creating account with username already in database

                // Prevent users from creating account with username < 5 characters
                else if(inputUsername.length() < 5) {
                    alertUser("Invalid username!", "Username must contain at least 5 characters.");
                    resetDetails(2);
                }

                // Prevent users from creating account with less than 8 characters, no upper and lowercase letters and no digits.
                else if(!isValidPassword(inputPassword)) {
                    alertUser("Invalid password!", "Password must contain at least 8 characters, including:\n\n-Uppercase letters\n-Lowercase letters\n-At least 1 digit");
                    resetDetails(0);
                }

                // Prevent users from logging in if password != confirm password
                else if(!(inputPassword.equals(inputConfirmPassword))) {
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

    // This method registers new users and store user data in Cloudant
    private void registerUser(String email_address, String username, String password) {
        progressDialog.setMessage("Registering...");
        showDialog();

        //TODO: Save user details into Cloudant; if successful, take user to main activity
        if(setDatabase()) {
            // TODO: (1) Store user details in Cloudant
            // Store user in SQLite once successfully stored in Cloudant
            sqLiteHelper.addUser(email_address, username);
            // Launch Login Activity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }

    // TODO: This method sets database from Cloudant
    private boolean setDatabase() {
        return false;
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

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    // This method resets the details keyed in by user.
    private void resetDetails(int num) {
        if(num == 1) email_address.setText("");
        if(num == 1 || num == 2) username.setText("");
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
}
