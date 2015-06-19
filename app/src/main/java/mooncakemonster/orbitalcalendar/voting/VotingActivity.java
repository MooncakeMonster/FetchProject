package mooncakemonster.orbitalcalendar.voting;

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

import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ActionBarActivity {

    //List to get all the appointments
    private ListView listView;
    private List<VoteItem> items = new ArrayList<VoteItem>();
    VoteAdapter adapter;

    TextView vote_title, vote_location;
    EditText vote_participants;
    Button add_option;
    Button send_vote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        getSupportActionBar().setElevation(0);

        // Get intent that started this activity
        Intent intent = getIntent();
        // Get bundle that stores data of this activity
        final Bundle bundle = intent.getExtras();

        // Initialise ArrayAdapter adapter for view
        listView = (ListView) findViewById(R.id.option_list);
        // Add default first item to List
        items.add(new VoteItem(bundle.getString("event_start_date"), bundle.getString("event_start_time")));

        adapter = new VoteAdapter(this, R.layout.row_vote, items);
        listView.setAdapter(adapter);

        vote_title = (TextView) findViewById(R.id.vote_title);
        vote_location = (TextView) findViewById(R.id.vote_location);
        vote_participants = (EditText) findViewById(R.id.vote_participants);
        add_option = (Button) findViewById(R.id.add_option);
        send_vote = (Button) findViewById(R.id.send_vote);

        // Get data from bundle and set to relevant texts
        vote_title.setText(bundle.getString("event_title"));
        vote_location.setText(" @ " + bundle.getString("event_location"));

        add_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Button pressed", "Add option");
                adapter.add(new VoteItem(bundle.getString("event_start_date"), bundle.getString("event_start_time")));
                adapter.notifyDataSetChanged();
            }
        });

        send_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VotingDatabase votingDatabase = new VotingDatabase(getBaseContext());
                //votingDatabase.putInformation(vote_title, vote_location, vote_participants);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
