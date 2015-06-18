package mooncakemonster.orbitalcalendar.voting;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ListFragment {

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<VoteItem> allVoteOptions;
    VoteAdapter adapter;

    TextView vote_title_location, vote_date, vote_time;
    EditText vote_participants;
    Button numberOfDays, numberOfHours, addOption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_voting, container, false);

        vote_title_location = (TextView) rootView.findViewById(R.id.vote_title_location);
        vote_date = (TextView) rootView.findViewById(R.id.vote_date);
        vote_time = (TextView) rootView.findViewById(R.id.vote_time);
        vote_participants = (EditText) rootView.findViewById(R.id.vote_participants);
        numberOfDays = (Button) rootView.findViewById(R.id.vote_day);
        numberOfHours = (Button) rootView.findViewById(R.id.vote_duration);
        addOption = (Button) rootView.findViewById(R.id.add_option);

        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        //Initialise ArrayAdapter adapter for view
        adapter = new VoteAdapter(getActivity(), R.layout.row_event, allVoteOptions);
        setListAdapter(adapter);

        return rootView;
    }

}
