package mooncakemonster.orbitalcalendar.registration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.menu.MenuActivity;

/**
 * Created by BAOJUN on 5/6/15.
 */
public class LoginActivity extends ActionBarActivity{

    Button login;
    TextView registerhere;
    String username, userpass;
    EditText USERNAME, USERPASS;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        registerhere = (TextView) findViewById(R.id.registerhere);
        USERNAME = (EditText) findViewById(R.id.usernameL);
        USERPASS = (EditText) findViewById(R.id.passwordL);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getIntent().getExtras();
                int status = b.getInt("status");

                // Usual login activity
                if (status == 1) {
                    username = USERNAME.getText().toString();
                    userpass = USERPASS.getText().toString();
                    DatabaseOperations dop = new DatabaseOperations(context);
                    Cursor CR = dop.getInformation(dop);
                    CR.moveToFirst();

                    boolean loginstatus = false;
                    // Get user details from database
                    String NAME = "";

                    do {
                        // Name -> column 0, password -> column 1
                        if (username.equals(CR.getString(0)) && (userpass.equals(CR.getString(1)))) {
                            loginstatus = true;
                            NAME = CR.getString(0);
                        }
                    } while (CR.moveToNext());

                    if (loginstatus) {
                        Toast.makeText(getBaseContext(), "Login Success!\n Welcome ", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                    } else {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        dialogBuilder.setTitle("Incorrect user details");
                        dialogBuilder.setMessage("Please try again!");
                        dialogBuilder.setPositiveButton("Ok", null);
                        dialogBuilder.show();
                    }
                }

                // User must login again to change username
                else if (status == 2) {
                    username = USERNAME.getText().toString();
                    userpass = USERPASS.getText().toString();
                    DatabaseOperations dop = new DatabaseOperations(context);
                    Cursor CR = dop.getInformation(dop);
                    CR.moveToFirst();

                    boolean loginstatus = false;
                    // Get user details from database
                    String NAME = "";

                    do {
                        // Name -> column 0, password -> column 1
                        if (username.equals(CR.getString(0)) && (userpass.equals(CR.getString(1)))) {
                            loginstatus = true;
                            NAME = CR.getString(0);
                        }
                    } while (CR.moveToNext());

                    if (loginstatus) {
                        Bundle BN = new Bundle();
                        BN.putString("user_name", NAME);
                        BN.putString("user_pass", userpass);

                        Intent intent = new Intent(LoginActivity.this, UpdateActivity.class);
                        intent.putExtras(BN);
                        finish();
                    } else {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        dialogBuilder.setTitle("Incorrect user details");
                        dialogBuilder.setMessage("Please try again!");
                        dialogBuilder.setPositiveButton("Ok", null);
                        dialogBuilder.show();
                    }
                }
            }
        });

        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}