package mooncakemonster.orbitalcalendar.votereceive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteOptionItem;
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

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        VoteOptionItem voteItem = (VoteOptionItem) bundle.getSerializable("voteItem");

        if(voteItem != null) {
            // Set title for action bar
            getSupportActionBar().setTitle("Voting Result for " + voteItem.getEvent_title());

            listView = (ListView) findViewById(R.id.vote_result_list);
            saveDateSelectedByTargetParticipants();
            resultAdapter = new ResultAdapter(this, R.layout.row_vote_result,
                    resultDatabase.getAllTargetResults(resultDatabase, Integer.parseInt(voteItem.getEventId())));
            listView.setAdapter(resultAdapter);
            resultAdapter.notifyDataSetChanged();
        }
    }

    // This method retrieves the selected dates from target participants via Cloudant database.
    private void saveDateSelectedByTargetParticipants() {
        // Retrieve the user's document from Cloudant
        cloudantConnect.startPullReplication();
        db = new UserDatabase(this);
        HashMap<String, String> user = db.getUserDetails();
        String my_username = user.get("username");
        User my_user = cloudantConnect.getTargetUser(my_username);

        if(my_user.getSelected_event_title() != null) {
            // Store participants according to date and time selected into SQLite
            // TODO: END TIME is empty, find out why!
            Log.d("ResultDatabase", "START TIME " + my_user.getSelected_start_time());
            Log.d("ResultDatabase", "END TIME " + my_user.getSelected_end_time());

            // Check if voting request is accepted or rejected
            String action = "";
            if(my_user.getReject_reason() == null || my_user.getReject_reason().isEmpty()) action = "accept";
            else action = "reject";

            resultDatabase.storeParticipants(resultDatabase, new ResultItem("" + my_user.getSelected_event_id(),
                    my_user.getSelected_start_date(), my_user.getSelected_end_date(),
                    my_user.getSelected_start_time(), my_user.getSelected_end_time(), "",
                    my_user.getSelected_my_username(), "", "", ""), action);

            // Reset document once data is saved in the phone
            // TODO: Remove comment!
            //cloudantConnect.resetVotingResponse(my_user);
            cloudantConnect.startPushReplication();
        }
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
