package mooncakemonster.orbitalcalendar.votesend;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.friendlist.FriendItem;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ActionBarActivity {

    private static final String TAG = VotingActivity.class.getSimpleName();

    // Connect to cloudant database
    CloudantConnect cloudantConnect;

    // List to get all the appointments
    private ListView listView;
    OptionAdapter adapter;

    // Retrieve username from SQLite
    FriendDatabase friendDatabase;
    List<FriendItem> list;

    TextView vote_title, vote_location;
    MultiAutoCompleteTextView vote_participants;
    Button add_option;
    String start_date = "", end_date = "", start_time = "", end_time = "";
    int colour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        //(0) Instantiate layout
        vote_title = (TextView) findViewById(R.id.vote_title);
        vote_location = (TextView) findViewById(R.id.vote_location);
        vote_participants = (MultiAutoCompleteTextView) findViewById(R.id.vote_participants);
        add_option = (Button) findViewById(R.id.add_option);
        // Initialise ArrayAdapter adapter for view
        listView = (ListView) findViewById(R.id.option_list);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        getSupportActionBar().setElevation(0);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        Appointment appt = (Appointment) bundle.getSerializable("appointment");

        //(2) Enter participants
        friendDatabase = new FriendDatabase(this);
        list = friendDatabase.getAllFriendUsername(friendDatabase);
        int size = list.size();

        String participants = "";
        for(int i = 0; i < size; i++) {
            participants += "@" + list.get(i).getUsername() + " ";
        }

        String[] split_participants = participants.split(" ");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.row_participants, split_participants);
        vote_participants.setAdapter(arrayAdapter);
        vote_participants.setThreshold(1);
        vote_participants.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        //(3) Extract data from appointment
        colour = appt.getColour();
        //Get Event Title and Location
        String event = appt.getEvent();
        String location = appt.getLocation();
        //Get millisecond
        long beginMillisec = appt.getStartDate();
        long endMillisec = appt.getEndDate();
        //Convert to standard format
        final String startDate = Constant.getDate(beginMillisec, Constant.DATEFORMATTER);
        final String startTime = Constant.getDate(beginMillisec, Constant.TIMEFORMATTER);
        final String endDate = Constant.getDate(endMillisec, Constant.DATEFORMATTER);
        final String endTime = Constant.getDate(endMillisec, Constant.TIMEFORMATTER);

        // Add default first item to List
        OptionItem firstProposedDate = new OptionItem(startDate, endDate, startTime, endTime);

        adapter = new OptionAdapter(this, R.layout.row_vote, new ArrayList<OptionItem>());
        listView.setAdapter(adapter);

        adapter.add(firstProposedDate);
        adapter.notifyDataSetChanged();

        // Get data from bundle and set to relevant texts
        vote_title.setText(event);

        if (location != null)
        {
            vote_location.setText(" @ " + location);
        }
        else
        {
            //Make TextView disappear
            vote_location.setVisibility(View.GONE);
        }


        add_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button pressed", "Add option");
                adapter.add(new OptionItem(startDate, endDate, startTime, endTime));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void collateDateTime() {
        int size = adapter.getCount();

        for (int i = 0; i < size; i++) {
            String startDate = Constant.standardYearMonthDate(adapter.getItem(i).getEvent_start_date(), new SimpleDateFormat("dd/MM/yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));
            String endDate = Constant.standardYearMonthDate(adapter.getItem(i).getEvent_end_date(), new SimpleDateFormat("dd/MM/yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));

            // Space to split all dates later when retrieving
            start_date += startDate + " ";
            end_date += endDate + " ";
            start_time += adapter.getItem(i).getEvent_start_time() + " ";
            end_time += adapter.getItem(i).getEvent_end_time() + " ";
        }
    }

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(VotingActivity.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            // Do not save data
            if (vote_participants.getText().toString().isEmpty()) {
                alertUser("Sending failed!", "Please enter participants.");
            } else if (adapter.getCount() < 2) {
                alertUser("Sending failed!", "Please add at least two options.");
            }

            // Save data
            else {
                // Add information into database
                collateDateTime();
                // Retrieve all users
                String participants = vote_participants.getText().toString();

                VotingDatabase votingDatabase = new VotingDatabase(getBaseContext());
                votingDatabase.putInformation(votingDatabase, colour, vote_title.getText().toString(), vote_location.getText().toString(), participants, start_date, end_date, start_time, end_time);

                // Send out to users via Cloudant
                cloudantConnect.sendOptionsToTargetParticipants(participants, start_date, end_date, start_time, end_time);
                // Push all options to other targeted participants
                cloudantConnect.startPushReplication();

                Toast.makeText(getBaseContext(), "Successfully sent out for voting", Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
