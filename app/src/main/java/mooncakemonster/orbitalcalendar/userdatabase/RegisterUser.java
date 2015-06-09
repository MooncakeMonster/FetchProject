package mooncakemonster.orbitalcalendar.userdatabase;

/**
 * Created by BAOJUN on 7/6/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.menudrawer.MenuDrawer;

public class RegisterUser extends Activity {
    private static final Pattern LOWER_CASE = Pattern.compile("\\p{Lu}");
    private static final Pattern UPPER_CASE = Pattern.compile("\\p{Ll}");
    private static final Pattern DIGIT = Pattern.compile("\\p{Nd}");

    private static final String TAG = RegisterUser.class.getSimpleName();
    private Button btnRegister;
    private TextView btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHelper db;
    Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        btnRegister = (Button) findViewById(R.id.register);
        btnLinkToLogin = (TextView) findViewById(R.id.rlogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHelper(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterUser.this, MenuDrawer.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                // Prevent users from creating account with invalid email address
                if(!isValidEmailAddress(email)) {
                    alertUser("Invalid email address!", "Please try again.");
                    resetDetails(1);
                }

                // Prevent users from creating account with username < 5 characters
                else if(name.length() < 5) {
                    alertUser("Invalid username!", "Username must contain at least 5 characters.");
                    resetDetails(2);
                }

                // Prevent users from creating account with less than 8 characters, no upper and lowercase letters and no digits.
                else if(!isValidPassword(password)) {
                    alertUser("Invalid password!", "Password must contain at least 8 characters, including:\n\n-Uppercase letters\n-Lowercase letters\n-At least 1 digit");
                    resetDetails(0);
                }

                // Prevent users from logging in if password != confirm password
                else if(!(password.equals(confirmPass))) {
                    alertUser("Passwords do not match!", "Please try again.");
                    resetDetails(0);
                }

                // Successfully registered
                else {
                    registerUser(name, email, password);
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginUser.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, ServerConfiguration.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Intent intent = new Intent(RegisterUser.this, LoginUser.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        ServerController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterUser.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    // This method resets the details keyed in by user.
    private void resetDetails(int num) {
        if(num == 1) inputEmail.setText("");
        if(num == 1 || num == 2) inputFullName.setText("");
        inputPassword.setText("");
        confirmPassword.setText("");
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

