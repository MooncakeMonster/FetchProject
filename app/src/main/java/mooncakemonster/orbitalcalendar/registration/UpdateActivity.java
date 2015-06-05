package mooncakemonster.orbitalcalendar.registration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 5/6/15.
 */
public class UpdateActivity extends Activity{

    String user_name, user_pass, new_user_name;
    Button update;
    EditText newuser;
    Context context = this;
    DatabaseOperations dop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update);

        Bundle BN = getIntent().getExtras();
        user_name = BN.getString("user_name");
        user_pass = BN.getString("user_pass");
        update = (Button) findViewById(R.id.updatebutton);
        newuser = (EditText) findViewById(R.id.updateuser);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_user_name = newuser.getText().toString();
                dop = new DatabaseOperations(context);
                dop.updateUserInfo(dop, user_name, user_pass, new_user_name);

                Toast.makeText(getBaseContext(), "Updation success...", Toast.LENGTH_LONG).show();
            }
        });
    }
}
