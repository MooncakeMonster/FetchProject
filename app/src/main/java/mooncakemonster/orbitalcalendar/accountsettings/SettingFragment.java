package mooncakemonster.orbitalcalendar.accountsettings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.userdatabase.LoginUser;
import mooncakemonster.orbitalcalendar.userdatabase.SQLiteHelper;
import mooncakemonster.orbitalcalendar.userdatabase.SessionManager;

public class SettingFragment extends Fragment {

    private TextView user_email, user_name;
    private Button logout;

    private SQLiteHelper db;
    private SessionManager session;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        user_email = (TextView) rootView.findViewById(R.id.useremail);
        user_name = (TextView) rootView.findViewById(R.id.updateuser);

        logout = (Button) rootView.findViewById(R.id.logout);

        db = new SQLiteHelper(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());

        // Fetch user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String email = user.get("email");
        String username = user.get("name");

        // TODO: Check why is it stored wrongly
        user_email.setText("Email: " + username);
        user_name.setText("Username: " + email);

        // Logout user when button pressed.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        return rootView;
    }

    // This method logout the user.
    private void logoutUser() {
        session.setLogin(false);

        // Remove user from sqlite in phone
        db.deleteUsers();

        startActivity(new Intent(getActivity().getApplicationContext(), LoginUser.class));
        getActivity().finish();
    }
}