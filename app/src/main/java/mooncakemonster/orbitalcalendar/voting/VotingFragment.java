package mooncakemonster.orbitalcalendar.voting;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mooncakemonster.orbitalcalendar.R;

public class VotingFragment extends Fragment {

    public VotingFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        return rootView;
    }
}