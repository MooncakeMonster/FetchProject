package mooncakemonster.orbitalcalendar.votesend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.friendlist.FriendItem;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;
import mooncakemonster.orbitalcalendar.voteresult.ResultDatabase;
import mooncakemonster.orbitalcalendar.voteresult.ResultItem;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ActionBarActivity implements TokenCompleteTextView.TokenListener {

    private static final String TAG = VotingActivity.class.getSimpleName();

    // Connect to cloudant database
    private CloudantConnect cloudantConnect;

    // Save the option dates in SQLite database
    private ResultDatabase resultDatabase;

    // List to get all the appointments
    private ListView listView;
    private OptionAdapter adapter;
    private FilteredArrayAdapter<String> filteredArrayAdapter;
    private UsernameCompletionView usernameCompletionView;
    private String participants = " ";
    String[] split_participants;

    // Retrieve username from SQLite
    private UserDatabase db;
    private FriendDatabase friendDatabase;
    private VotingDatabase votingDatabase;
    private List<FriendItem> list;

    private TextView vote_title, vote_location;
    private MultiAutoCompleteTextView vote_participants;
    private Button add_option;
    private String notes = "", start_date = "", end_date = "", start_time = "", end_time = "";
    private int colour, eventId;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        // Initialise ArrayAdapter adapter for view
        View header = getLayoutInflater().inflate(R.layout.header_voting, null);
        listView = (ListView) findViewById(R.id.option_list);
        listView.addHeaderView(header);
        votingDatabase = new VotingDatabase(getBaseContext());

        // Instantiate layout
        vote_title = (TextView) header.findViewById(R.id.vote_title);
        vote_location = (TextView) header.findViewById(R.id.vote_location);
        vote_participants = (MultiAutoCompleteTextView) header.findViewById(R.id.vote_participants);
        add_option = (Button) header.findViewById(R.id.add_option);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        db = new UserDatabase(this);

        getSupportActionBar().setElevation(0);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        Appointment appt = (Appointment) bundle.getSerializable("appointment");

        //(2) Enter participants
        friendDatabase = new FriendDatabase(this);
        list = friendDatabase.getAllFriendUsername(friendDatabase);
        int size = list.size();

        String list_participants = "";
        for(int i = 0; i < size; i++) {
            list_participants += list.get(i).getUsername() + " ";
        }

        usernameCompletionView = (UsernameCompletionView) findViewById(R.id.vote_participants);
        split_participants = list_participants.split(" ");
        filteredArrayAdapter = new FilteredArrayAdapter<String>(this, R.layout.row_autocomplete, split_participants) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = layoutInflater.inflate(R.layout.row_autocomplete, parent, false);
                }

                String username = getItem(position);

                if(username != null) {
                    String[] selected_participants = participants.split(" ");
                    int size = selected_participants.length;

                    for (int i = 0; i < size; i++) {
                        if (username.equals(selected_participants[i])) {
                            usernameCompletionView.removeObject(username);
                            //remove(username);
                            //usernameCompletionView.notifyDataSetChanged();
                            break;
                        }
                    }

                    RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(username));
                    ((SimpleDraweeView) convertView.findViewById(R.id.autocomplete_image)).setImageDrawable(roundImage);
                    ((TextView) convertView.findViewById(R.id.autocomplete_username)).setText(username);
                }

                return convertView;
            }

            @Override
            protected boolean keepObject(String username, String ch) {
                ch = ch.toLowerCase();
                return username.toLowerCase().startsWith(ch);
            }

            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
            }

            @Override
            public void remove(String object) {
                super.remove(object);
            }
        };

        usernameCompletionView.setAdapter(filteredArrayAdapter);
        usernameCompletionView.setTokenListener(this);
        usernameCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        if (savedInstanceState == null) usernameCompletionView.setPrefix("To: ");

        //(3) Extract data from appointment
        notes = appt.getNotes();
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

    // This method removes the username from the list once it is selected.
    private String[] removeSelectedParticipants(String[] split_participants, String[] selected_participants) {
        int split_size = split_participants.length;
        int selected_size = selected_participants.length;

        for(int i = 0; i < split_size; i++) {
            for(int j = 0; j < selected_size; j++)
                if(split_participants[i].equals(selected_participants[j]))
                    split_participants[i] = "";
        }

        return split_participants;
    }

    // This method collates the options sent out by the requester.
    private void collateDateTime() {
        int size = adapter.getCount();

        for (int i = 0; i < size; i++) {
            OptionItem optionItem = adapter.getItem(i);
            String startDate = Constant.standardYearMonthDate(optionItem.getEvent_start_date(), new SimpleDateFormat("dd/MM/yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));
            String endDate = Constant.standardYearMonthDate(optionItem.getEvent_end_date(), new SimpleDateFormat("dd/MM/yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));
            String startTime = optionItem.getEvent_start_time();
            String endTime = optionItem.getEvent_end_time();

            // Space to split all dates later when retrieving
            start_date += startDate + " ";
            end_date += endDate + " ";
            start_time += startTime + " ";
            end_time += endTime + " ";
        }
    }

    // This method saves the voting options sent out into database.
    private void saveOptions(ResultItem resultItem) {
        resultDatabase = new ResultDatabase(this);

        String[] split_start_date = resultItem.getStart_date().split(" ");
        String[] split_end_date = resultItem.getEnd_date().split(" ");
        String[] split_start_time = resultItem.getStart_time().split(" ");
        String[] split_end_time = resultItem.getEnd_time().split(" ");

        int size = split_start_date.length;

        for(int i = 0; i < size; i++) {
            resultDatabase.putInformation(resultDatabase, resultItem.getEvent_id(), split_start_date[i],
                    split_end_date[i], split_start_time[i], split_end_time[i], resultItem.getAll_username(),
                    "", "", "", "0");
        }

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
                Constant.alertUser(this, "Sending failed!", "Please enter participants.");
            } else if (adapter.getCount() < 2) {
                Constant.alertUser(this, "Sending failed!", "Please add at least two options.");
            } else if(!friendDatabase.checkUsername(friendDatabase, participants)) {
                Constant.alertUser(this, "Sending failed!", "Please ensure that the username entered is valid.");
            }

            // Save data
            else {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);

                progressDialog.setMessage("Sending voting request...");
                showDialog();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Add information into database
                        collateDateTime();
                        // Retrieve all users
                        String title = vote_title.getText().toString();
                        String location = vote_location.getText().toString().replace(" @ ", "");

                        eventId = votingDatabase.eventSize(votingDatabase);

                        votingDatabase.putInformation(votingDatabase, "" + eventId, "" + colour, title, location,
                                participants, null, null, start_date, end_date, start_time, end_time, null, null, null, null);

                        // Save options in SQLite for voting result
                        saveOptions(new ResultItem("" + eventId, start_date, end_date, start_time, end_time, participants, "", "", "", ""));

                        // Fetch user details from sqlite
                        HashMap<String, String> user = db.getUserDetails();

                        // Send out to users via Cloudant
                        cloudantConnect.sendOptionsToTargetParticipants(user.get("username"), participants, eventId, colour,
                                title, location, notes, start_date, end_date, start_time, end_time);
                        // Push all options to other targeted participants
                        cloudantConnect.startPushReplication();

                        finish();
                        hideDialog();
                    }
                }, 1500);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTokenConfirmation() {
        participants = "";
        for (Object token : usernameCompletionView.getObjects()) {
            participants += token.toString() + " ";
        }
    }

    @Override
    public void onTokenAdded(Object o) {
        updateTokenConfirmation();
    }

    @Override
    public void onTokenRemoved(Object o) {
        updateTokenConfirmation();
    }

    // This method shows progress dialog when not showing
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    // This method dismiss progress dialog when required (dialog must be showing)
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
