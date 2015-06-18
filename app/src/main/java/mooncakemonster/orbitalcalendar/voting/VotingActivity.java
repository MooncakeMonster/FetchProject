package mooncakemonster.orbitalcalendar.voting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mooncakemonster.orbitalcalendar.R;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends Activity {

    TextView vote_title_location;
    EditText vote_participants;
    Button numberOfDays, numberOfHours, addOption;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        vote_title_location = (TextView) findViewById(R.id.vote_title_location);
        vote_participants = (EditText) findViewById(R.id.vote_participants);
        numberOfDays = (Button) findViewById(R.id.vote_day);
        numberOfHours = (Button) findViewById(R.id.vote_duration);
        addOption = (Button) findViewById(R.id.add_option);
    }


}
