package mooncakemonster.orbitalcalendar.registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 5/6/15.
 */
public class RegisterActivity extends Activity{

    EditText USER_EMAIL, USER_NAME, USER_PASS, CONFIRM_PASS;
    String user_email, user_name, user_pass, confirm_pass;
    Button REG;
    Context context = this;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        USER_EMAIL = (EditText) findViewById(R.id.email);
        USER_NAME = (EditText) findViewById(R.id.username);
        USER_PASS = (EditText) findViewById(R.id.password);
        CONFIRM_PASS = (EditText) findViewById(R.id.confirmpassword);
        REG = (Button) findViewById(R.id.register);
        REG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from user upon registration
                user_email = USER_EMAIL.getText().toString();
                user_name = USER_NAME.getText().toString();
                user_pass = USER_PASS.getText().toString();
                confirm_pass = CONFIRM_PASS.getText().toString();

                // Prevent users from logging in if password != confirm password
                if(!(user_pass.equals(confirm_pass))) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    dialogBuilder.setMessage("Passwords do not match.\n Please try again!");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();

                    // Reset texts
                    USER_PASS.setText("");
                    CONFIRM_PASS.setText("");
                }else {
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
}
