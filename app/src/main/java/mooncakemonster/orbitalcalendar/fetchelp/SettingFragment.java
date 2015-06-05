package mooncakemonster.orbitalcalendar.fetchelp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.registration.LoginActivity;

public class SettingFragment extends Fragment {

    Button update, delete, logout;
    int status = 0;

    public SettingFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        update = (Button) rootView.findViewById(R.id.update);
        logout = (Button) rootView.findViewById(R.id.logout);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                Bundle b = new Bundle();
                b.putInt("status", status);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 3;
                Bundle b = new Bundle();
                b.putInt("status", status);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        return rootView;
    }
}