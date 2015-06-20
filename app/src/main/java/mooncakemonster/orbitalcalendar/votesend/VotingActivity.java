package mooncakemonster.orbitalcalendar.votesend;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ActionBarActivity {

    //List to get all the appointments
    private ListView listView;
    private List<OptionItem> items = new ArrayList<OptionItem>();
    OptionAdapter adapter;

    TextView vote_title, vote_location;
    EditText vote_participants;
    Button add_option;
    String start_date, end_date, start_time, end_time;
    int colour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        getSupportActionBar().setElevation(0);

        // Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        colour = bundle.getInt("event_colour");

        // Initialise ArrayAdapter adapter for view
        listView = (ListView) findViewById(R.id.option_list);
        // Add default first item to List
        items.add(new OptionItem(bundle.getString("event_start_date"), bundle.getString("event_end_date"),
                bundle.getString("event_start_time"), bundle.getString("event_end_time")));

        adapter = new OptionAdapter(this, R.layout.row_vote, items);
        listView.setAdapter(adapter);

        vote_title = (TextView) findViewById(R.id.vote_title);
        vote_location = (TextView) findViewById(R.id.vote_location);
        vote_participants = (EditText) findViewById(R.id.vote_participants);
        add_option = (Button) findViewById(R.id.add_option);

        // Get data from bundle and set to relevant texts
        vote_title.setText(bundle.getString("event_title"));
        vote_location.setText(" @ " + bundle.getString("event_location"));

        add_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button pressed", "Add option");
                adapter.add(new OptionItem(bundle.getString("event_start_date"), bundle.getString("event_end_date"),
                        bundle.getString("event_start_time"), bundle.getString("event_end_time")));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void collateDateTime() {
        int size = adapter.getCount();

        for(int i = 0; i < size; i++) {
            String startDate = Constant.standardYearMonthDate(adapter.getItem(i).getEvent_start_date(), new SimpleDateFormat("dd MMM yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));
            String endDate = Constant.standardYearMonthDate(adapter.getItem(i).getEvent_end_date(), new SimpleDateFormat("dd MMM yyyy, EEE"), new SimpleDateFormat("dd/MM/yyyy"));

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
                VotingDatabase votingDatabase = new VotingDatabase(getBaseContext());
                votingDatabase.putInformation(votingDatabase, colour, vote_title.getText().toString(), vote_location.getText().toString(), vote_participants.getText().toString(), start_date, end_date, start_time, end_time);

                Toast.makeText(getBaseContext(), "Details successfully saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
