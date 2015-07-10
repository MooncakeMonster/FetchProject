package mooncakemonster.orbitalcalendar.ImportExternal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mooncakemonster.orbitalcalendar.R;

public class ImportExternalFragment extends Fragment {

    public ImportExternalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_import_external, container, false);

        Button facebookImport = (Button) rootView.findViewById(R.id.importFacebook);
        Button nusTimetable = (Button) rootView.findViewById(R.id.importNUS);

        //Facebook importing of friends' birthday
        facebookImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImportFacebookLogin fragment = new ImportFacebookLogin();
                fragment.show(fragmentManager, "facebook_login_fragment");
            }
        });

        //Importing NUS timetable
        nusTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Insert code for nus timetable
            }
        });

        return rootView;
    }
}