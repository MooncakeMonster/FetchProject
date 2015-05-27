package mooncakemonster.orbitalcalendar.event;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 27/5/15.
 */
public class EventFragment extends Fragment {

    public EventFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        return rootView;
    }
}

