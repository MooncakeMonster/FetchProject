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
 * This program allows users to login upon registration.
 */
public class LoginActivity extends ActionBarActivity{

    Button login, details;
    TextView registerhere;
    String username, userpass;
    EditText USERNAME, USERPASS;
    Context context = this;
    DatabaseAdapter dop = new DatabaseAdapter(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.login);
        details = (Button) findViewById(R.id.details);
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
                    Cursor CR = dop.getCursor(dop);
                    CR.moveToFirst();

                    boolean loginstatus = false;
                    // Get user details from database
                    String NAME = "";

                    do {
                        // Email -> column 0, Name -> column 1, password -> column 2
                        if (username.equals(CR.getString(1)) && (userpass.equals(CR.getString(2)))) {
                            loginstatus = true;
                            NAME = CR.getString(1);
                        }
                    } while (CR.moveToNext());

                    if (loginstatus) {
                        Toast.makeText(getBaseContext(), "Login Success!\nWelcome " + NAME, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
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

    // This method shows all the user details. TODO: Remove once app is up.
    public void viewAllDetails(View view) {
        String data = DatabaseAdapter.getAllUsers(dop);
        Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();
    }
}