package mooncakemonster.orbitalcalendar.ImportExternal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mooncakemonster.orbitalcalendar.R;

public class ImportExternalFragment extends Fragment {

    public static final int ICS_IMPORT_REQUEST = 1;

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
                //Get file path from file picker
                Intent intentFilePath = new Intent(Intent.ACTION_GET_CONTENT);
                intentFilePath.setType("*/*");
                startActivityForResult(intentFilePath, ICS_IMPORT_REQUEST);

                String filePath = intentFilePath.getData().getPath();
                Intent intent = new Intent(getActivity(), ImportICSParser.class);
                intent.putExtra("filePath", filePath);
                startActivity(intent);
            }
        });

        return rootView;
    }
}