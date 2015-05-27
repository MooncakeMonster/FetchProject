package mooncakemonster.orbitalcalendar.fetchelp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mooncakemonster.orbitalcalendar.R;

public class FetchHelpFragment extends Fragment {

    public FetchHelpFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        return rootView;
    }
}