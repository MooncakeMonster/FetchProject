package mooncakemonster.orbitalcalendar.registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;

/**
 * This program allows users to register their own account.
 */

public class RegisterActivity extends Activity{

    EditText USER_EMAIL, USER_NAME, USER_PASS, CONFIRM_PASS;
    String user_email, user_name, user_pass, confirm_pass;
    Button registration;
    Context context = this;

    private static final Pattern LOWER_CASE = Pattern.compile("\\p{Lu}");
    private static final Pattern UPPER_CASE = Pattern.compile("\\p{Ll}");
    private static final Pattern DECIMAL_DIGIT = Pattern.compile("\\p{Nd}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        USER_EMAIL = (EditText) findViewById(R.id.email);
        USER_NAME = (EditText) findViewById(R.id.username);
        USER_PASS = (EditText) findViewById(R.id.password);
        CONFIRM_PASS = (EditText) findViewById(R.id.confirmpassword);

        registration = (Button) findViewById(R.id.register);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from user upon registration
                user_email = USER_EMAIL.getText().toString();
                user_name = USER_NAME.getText().toString();
                user_pass = USER_PASS.getText().toString();
                confirm_pass = CONFIRM_PASS.getText().toString();

                // Prevent duplicate email by users
                // TODO: Check duplicate email in database

                // Prevent duplicate username by users and limit username length to be at least 5
                // TODO: Check duplicate username in database

                // Prevent users from creating account with invalid email address
                if(!isValidEmailAddress(user_email)) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    dialogBuilder.setTitle("Invalid email address!");
                    dialogBuilder.setMessage("Please try again.");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                    resetDetails(1);
                }

                // Prevent users from creating account with username < 5 characters
                else if(user_name.length() < 5) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    dialogBuilder.setTitle("Invalid username!");
                    dialogBuilder.setMessage("Username must contain at least 5 characters.");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                    resetDetails(2);
                }

                // Prevent users from creating account with less than 8 characters, no upper and lowercase letters and no digits.
                else if(!isValidPassword(user_pass)) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    dialogBuilder.setTitle("Invalid password!");
                    dialogBuilder.setMessage("Password must contain at least 8 characters, including:\n\n-Uppercase letters\n-Lowercase letters\n-At least 1 digit");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                    resetDetails(0);
                }

                // Prevent users from logging in if password != confirm password
                else if(!(user_pass.equals(confirm_pass))) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    dialogBuilder.setTitle("Passwords do not match.");
                    dialogBuilder.setMessage("Please try again!");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                    resetDetails(0);
                }

                // Successfully registered
                else {
                    DatabaseOperations DB = new DatabaseOperations(context);
                    // insert users data
                    DB.putInformation(DB, user_name, user_pass);
                    Toast.makeText(getBaseContext(), "Registration success!\nYou may login to Fetch.", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        });
    }

    // This method resets the details keyed in by user.
    private void resetDetails(int num) {
        if(num == 1) USER_EMAIL.setText("");
        if(num == 1 || num == 2) USER_NAME.setText("");
        USER_PASS.setText("");
        CONFIRM_PASS.setText("");
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
        return DECIMAL_DIGIT.matcher(password).find();
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
