package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by BAOJUN on 12/7/15.
 */
public class VotingResultActivity extends ActionBarActivity {

    CloudantConnect cloudantConnect;

    VotingDatabase votingDatabase;
    ResultDatabase resultDatabase;
    UserDatabase db;

    private ListView listView;
    ResultAdapter resultAdapter;

    private TextView event_title, event_location;
    private Button sort_list;
    private int selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_result);

        getSupportActionBar().setElevation(0);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this, "user");

        votingDatabase = new VotingDatabase(this);
        resultDatabase = new ResultDatabase(this);

        resultDatabase = new ResultDatabase(this);
        db = new UserDatabase(this);

        event_title = (TextView) findViewById(R.id.vote_result_title);
        event_location = (TextView) findViewById(R.id.vote_result_location);
        sort_list = (Button) findViewById(R.id.vote_result_sort);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        VoteItem voteItem = (VoteItem) bundle.getSerializable("voteItem");

        if(voteItem != null) {
            event_title.setText(voteItem.getEvent_title());
            event_location.setText("@ " + voteItem.getEvent_location());

            listView = (ListView) findViewById(R.id.vote_result_list);
            resultAdapter = new ResultAdapter(this, R.layout.row_vote_result,
                    resultDatabase.getAllTargetResults(resultDatabase, Integer.parseInt(voteItem.getEventId())));
            listView.setAdapter(new SlideExpandableListAdapter(resultAdapter, R.id.confirm_send_date, R.id.expandable_vote_result));
            resultAdapter.notifyDataSetChanged();
        }

        // Sort the list according to user's preference
        sort_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] sort_type = {"Sort by total votes", "Sort by date", "Sort by time"};

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VotingResultActivity.this);
                alertBuilder.setTitle("Sort Voting List").setSingleChoiceItems(sort_type, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                // (1) Sort according to total votes
                                selection = 0;
                                break;
                            case 1:
                                // (2) Sort according to date
                                selection = 1;
                                break;
                            case 2:
                                // (3) Sort according to time
                                selection = 2;
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
